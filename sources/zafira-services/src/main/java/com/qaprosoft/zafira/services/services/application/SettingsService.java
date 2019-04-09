/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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

import java.util.List;
import java.util.stream.Collectors;

import com.qaprosoft.zafira.models.push.events.ReinitEventMessage;
import com.qaprosoft.zafira.services.util.EventPushService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.SettingsMapper;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.Setting.SettingType;
import com.qaprosoft.zafira.models.db.Setting.Tool;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.integration.impl.AsynSendEmailTask;
import com.qaprosoft.zafira.services.services.application.integration.impl.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.impl.ElasticsearchService;
import com.qaprosoft.zafira.services.services.application.integration.Integration;
import com.qaprosoft.zafira.services.services.application.integration.impl.JenkinsService;
import com.qaprosoft.zafira.services.services.application.integration.impl.JiraService;
import com.qaprosoft.zafira.services.services.application.integration.impl.LDAPService;
import com.qaprosoft.zafira.services.services.application.integration.impl.RabbitMQService;
import com.qaprosoft.zafira.services.services.application.integration.impl.SlackService;
import com.qaprosoft.zafira.services.services.application.integration.impl.AmazonService;
import com.qaprosoft.zafira.services.services.application.integration.impl.google.GoogleService;

@Service
public class SettingsService {

    @Autowired
    @Lazy
    private SettingsMapper settingsMapper;

    @Autowired
    @Lazy
    private JiraService jiraService;

    @Autowired
    @Lazy
    private LDAPService ldapService;

    @Autowired
    @Lazy
    private GoogleService googleService;

    @Autowired
    @Lazy
    private ElasticsearchService elasticsearchService;

    @Autowired
    @Lazy
    private JenkinsService jenkinsService;

    @Autowired
    @Lazy
    private SlackService slackService;

    @Autowired
    @Lazy
    private AsynSendEmailTask emailTask;

    @Autowired
    @Lazy
    private AmazonService amazonService;

    @Autowired
    @Lazy
    private CryptoService cryptoService;

    @Autowired
    @Lazy
    private RabbitMQService rabbitMQService;

    @Autowired
    private EventPushService<ReinitEventMessage> eventPushService;

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
    public List<Setting> getSettingsByTool(Tool tool) throws ServiceException {
        List<Setting> result;
        switch (tool) {
        case ELASTICSEARCH:
            result = elasticsearchService.getSettings();
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

    @Transactional(readOnly = true)
    public List<Setting> getSettingsByIntegration(boolean isIntegrationTool) throws ServiceException {
        return settingsMapper.getSettingsByIntegration(isIntegrationTool);
    }

    public boolean isConnected(Tool tool) {
        return tool != null && !tool.equals(Tool.CRYPTO) && getServiceByTool(tool).isEnabledAndConnected(tool);
    }

    @Transactional(readOnly = true)
    public List<Tool> getTools() {
        return settingsMapper.getTools().stream()
                .filter(tool -> tool != null && !tool.equals(Tool.CRYPTO))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String getSettingValue(Setting.SettingType type) throws ServiceException {
        return getSettingByName(type.name()).getValue();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSetting(Setting setting) throws Exception {
        setting.setValue(StringUtils.isBlank(setting.getValue() != null ? setting.getValue().trim() : null) ? null : setting.getValue());
        settingsMapper.updateSetting(setting);
    }

    @Transactional(rollbackFor = Exception.class)
    public Setting createSetting(Setting setting) throws Exception {
        settingsMapper.createSetting(setting);
        return setting;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createSettingFile(byte[] fileBytes, String originalFileName, Tool tool, String settingName) throws Exception {
        Setting setting = getSettingsByTool(tool).stream().filter(s -> s.getName().equals(settingName)).findFirst().orElse(null);
        if(setting != null) {
            setting.setFile(fileBytes);
            setting.setValue(originalFileName);
            updateSetting(setting);
            getServiceByTool(setting.getTool()).init();
            notifyToolReinitiated(setting.getTool(), TenancyContext.getTenantName());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void reEncrypt() throws Exception {
        List<Setting> settings = getSettingsByEncrypted(true);
        for (Setting setting : settings) {
            String decValue = cryptoService.decrypt(setting.getValue());
            setting.setValue(decValue);
        }
        cryptoService.generateKey();
        notifyToolReinitiated(Tool.CRYPTO, TenancyContext.getTenantName());
        for (Setting setting : settings) {
            String encValue = cryptoService.encrypt(setting.getValue());
            setting.setValue(encValue);
            updateSetting(setting);
        }
    }

    /**
     * Sends message to broker to notify about changed integration.
     * 
     * @param tool that was re-initiated
     * @param tenant whose integration was updated
     */
    public void notifyToolReinitiated(Tool tool, String tenant) {
        eventPushService.convertAndSend(EventPushService.Type.SETTINGS, new ReinitEventMessage(tenant, rabbitMQService.getSettingQueueName(), tool));
        getServiceByTool(tool).init();
    }

    @RabbitListener(queues = "#{settingsQueue.name}")
    public void process(Message message) {
        ReinitEventMessage rm = new Gson().fromJson(new String(message.getBody()), ReinitEventMessage.class);
        if (! rabbitMQService.isSettingQueueConsumer(rm.getQueueName()) && getServiceByTool(rm.getTool()) != null) {
            TenancyContext.setTenantName(rm.getTenancy());
            getServiceByTool(rm.getTool()).init();
        }
    }
    
    @Transactional(readOnly = true)
    public String getPostgresVersion() throws ServiceException
    {
        return settingsMapper.getPostgresVersion();
    }

    @SuppressWarnings("rawtypes")
    public Integration getServiceByTool(Tool tool) {
        Integration service = null;
        switch (tool) {
        case GOOGLE:
            service = googleService;
            break;
        case LDAP:
            service = ldapService;
            break;
        case ELASTICSEARCH:
            service = elasticsearchService;
            break;
        case JIRA:
            service = jiraService;
            break;
        case JENKINS:
            service = jenkinsService;
            break;
        case SLACK:
            service = slackService;
            break;
        case CRYPTO:
            service = cryptoService;
            break;
        case EMAIL:
            service = emailTask;
            break;
        case AMAZON:
            service = amazonService;
            break;
        case RABBITMQ:
            service = rabbitMQService;
            break;
        default:
            break;
        }
        return service;
    }
}
