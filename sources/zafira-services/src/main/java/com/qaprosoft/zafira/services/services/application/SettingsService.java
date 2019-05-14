/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.services.application;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.SettingsMapper;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.Setting.SettingType;
import com.qaprosoft.zafira.models.db.Setting.Tool;
import com.qaprosoft.zafira.models.dto.ConnectedToolType;
import com.qaprosoft.zafira.models.push.events.ReinitEventMessage;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.impl.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.impl.ElasticsearchService;
import com.qaprosoft.zafira.services.services.application.integration.impl.RabbitMQService;
import com.qaprosoft.zafira.services.util.EventPushService;

@Service
public class SettingsService {

    private static final String ERR_MSG_MULTIPLE_TOOLS_UPDATE = "Unable to update settings for multiple tools at once";
    private static final String ERR_MSG_NOT_EXISTS_SETTING_UPDATE = "Unable to update not existing setting '%s'";
    private static final String ERR_MSG_INCORRECT_TOOL_SETTING_UPDATE = "Unable to update '%s': setting does not belong to specified tool '%s'";

    private final SettingsMapper settingsMapper;
    private final IntegrationService integrationService;
    private final EventPushService<ReinitEventMessage> eventPushService;

    public SettingsService(SettingsMapper settingsMapper,
            @Lazy IntegrationService integrationService,
            EventPushService<ReinitEventMessage> eventPushService) {
        this.settingsMapper = settingsMapper;
        this.integrationService = integrationService;
        this.eventPushService = eventPushService;
    }

    @Transactional(readOnly = true)
    public Setting getSettingByName(String name) throws ServiceException {
        return settingsMapper.getSettingByName(name);
    }

    @Transactional(readOnly = true)
    public Setting getSettingByType(SettingType type) {
        return settingsMapper.getSettingByName(type.name());
    }

    @Transactional(readOnly = true)
    public List<Setting> getSettingsByEncrypted(boolean isEncrypted) throws ServiceException {
        return settingsMapper.getSettingsByEncrypted(isEncrypted);
    }

    @Transactional(readOnly = true)
    public Map<Tool, Boolean> getToolsStatuses() {
        return Arrays.stream(Tool.values())
                .filter(tool -> !Arrays.asList(Tool.CRYPTO, Tool.ELASTICSEARCH).contains(tool))
                .collect(Collectors.toMap(tool -> tool, tool -> integrationService.getServiceByTool(tool).isEnabledAndConnected()));
    }

    @Transactional(readOnly = true)
    public List<Setting> getSettingsByTool(Tool tool) throws ServiceException {
        List<Setting> result;
        switch (tool) {
        case ELASTICSEARCH:
            result = getElasticsearchService().getSettings();
            break;
        default:
            result = settingsMapper.getSettingsByTool(tool);
            break;
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteSettingById(long id) throws ServiceException {
        settingsMapper.deleteSettingById(id);
    }

    @Transactional(readOnly = true)
    public List<Setting> getAllSettings() throws ServiceException {
        return settingsMapper.getAllSettings();
    }

    public boolean isConnected(Tool tool) {
        return tool != null && !tool.equals(Tool.CRYPTO) && integrationService.getServiceByTool(tool).isEnabledAndConnected();
    }

    @Transactional(readOnly = true)
    public String getSettingValue(Setting.SettingType type) throws ServiceException {
        return getSettingByName(type.name()).getValue();
    }

    @Transactional(rollbackFor = Exception.class)
    public ConnectedToolType updateSettings(List<Setting> settings) throws ServiceException {
        ConnectedToolType connectedTool = null;
        if (!CollectionUtils.isEmpty(settings)) {
            Tool tool = settings.get(0).getTool();
            validateSettingsOwns(settings, tool);
            settings.forEach(setting -> {
                decryptSetting(setting);
                updateSetting(setting);
            });
            notifyToolReinitiated(tool, TenancyContext.getTenantName());
            connectedTool = new ConnectedToolType();
            connectedTool.setName(tool);
            connectedTool.setSettingList(settings);
            connectedTool.setConnected(integrationService.getServiceByTool(tool).isEnabledAndConnected());
        }
        return connectedTool;
    }

    @Transactional(rollbackFor = Exception.class)
    public Setting updateSetting(Setting setting) throws ServiceException {
        setting.setValue(StringUtils.isBlank(setting.getValue() != null ? setting.getValue().trim() : null) ? null : setting.getValue());
        settingsMapper.updateSetting(setting);
        return setting;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateIntegrationSetting(Setting setting) throws ServiceException {
        setting.setValue(StringUtils.isBlank(setting.getValue() != null ? setting.getValue().trim() : null) ? null : setting.getValue());
        settingsMapper.updateIntegrationSetting(setting);
    }

    @Transactional(rollbackFor = Exception.class)
    public Setting createSetting(Setting setting) throws Exception {
        settingsMapper.createSetting(setting);
        return setting;
    }

    @Transactional(rollbackFor = Exception.class)
    public ConnectedToolType createSettingFile(byte[] fileBytes, String originalFileName, String name, Tool tool) throws Exception {
        Setting dbSetting = getSettingByNameSafely(name, tool);
        if (dbSetting != null) {
            dbSetting.setFile(fileBytes);
            dbSetting.setValue(originalFileName);
            updateSetting(dbSetting);
            integrationService.getServiceByTool(dbSetting.getTool()).init();
            notifyToolReinitiated(dbSetting.getTool(), TenancyContext.getTenantName());
        }
        ConnectedToolType connectedToolType = new ConnectedToolType();
        connectedToolType.setName(dbSetting.getTool());
        connectedToolType.setSettingList(Collections.singletonList(dbSetting));
        connectedToolType.setConnected(integrationService.getServiceByTool(dbSetting.getTool()).isEnabledAndConnected());
        return connectedToolType;
    }

    @Transactional(rollbackFor = Exception.class)
    public void reEncrypt() throws Exception {
        List<Setting> settings = getSettingsByEncrypted(true);
        CryptoService cryptoService = getCryptoMQService();
        for (Setting setting : settings) {
            String decValue = cryptoService.decrypt(setting.getValue());
            setting.setValue(decValue);
        }
        cryptoService.regenerateKey();
        notifyToolReinitiated(Tool.CRYPTO, TenancyContext.getTenantName());
        for (Setting setting : settings) {
            String encValue = cryptoService.encrypt(setting.getValue());
            setting.setValue(encValue);
            updateSetting(setting);
        }
    }

    private CryptoService getCryptoMQService() {
        return integrationService.getServiceByTool(Tool.CRYPTO);
    }

    @Transactional(readOnly = true)
    public String getPostgresVersion() throws ServiceException {
        return settingsMapper.getPostgresVersion();
    }

    /**
     * Sends message to broker to notify about changed integration.
     *
     * @param tool that was re-initiated
     * @param tenant whose integration was updated
     */
    public void notifyToolReinitiated(Tool tool, String tenant) {
        eventPushService.convertAndSend(EventPushService.Type.SETTINGS, new ReinitEventMessage(tenant, tool));
        integrationService.getServiceByTool(tool).init();
    }

    @RabbitListener(queues = "#{settingsQueue.name}")
    public void process(Message message) {
        ReinitEventMessage rm = new Gson().fromJson(new String(message.getBody()), ReinitEventMessage.class);
        if (!getRabbitMQService().isSettingQueueConsumer(message.getMessageProperties().getConsumerQueue())
                && integrationService.getServiceByTool(rm.getTool()) != null) {
            TenancyContext.setTenantName(rm.getTenancy());
            integrationService.getServiceByTool(rm.getTool()).init();
        }
    }

    private void decryptSetting(Setting setting) {
        Setting dbSetting = getSettingByNameSafely(setting.getName(), setting.getTool());
        setting.setEncrypted(dbSetting.isEncrypted());
        if (dbSetting.isValueForEncrypting()) {
            if (StringUtils.isBlank(setting.getValue())) {
                setting.setEncrypted(false);
            } else {
                if (!setting.getValue().equals(dbSetting.getValue())) {
                    CryptoService cryptoService = integrationService.getServiceByTool(Tool.CRYPTO);
                    setting.setValue(cryptoService.encrypt(setting.getValue()));
                    setting.setEncrypted(true);
                }
            }
        }
    }

    private Setting getSettingByNameSafely(String name, Tool tool) {
        Setting setting = getSettingByName(name);
        if (setting == null) {
            throw new ForbiddenOperationException(String.format(ERR_MSG_NOT_EXISTS_SETTING_UPDATE, name));
        }
        if (!setting.getTool().equals(tool)) {
            throw new ForbiddenOperationException(String.format(ERR_MSG_INCORRECT_TOOL_SETTING_UPDATE, setting.getName(), tool));
        }
        return setting;
    }

    private void validateSettingsOwns(List<Setting> settings, Tool tool) {
        settings.forEach(setting -> {
            if (!tool.equals(setting.getTool())) {
                throw new ForbiddenOperationException(ERR_MSG_MULTIPLE_TOOLS_UPDATE);
            }
        });
    }

    private ElasticsearchService getElasticsearchService() {
        return integrationService.getServiceByTool(Tool.ELASTICSEARCH);
    }

    private RabbitMQService getRabbitMQService() {
        return integrationService.getServiceByTool(Tool.RABBITMQ);
    }

}
