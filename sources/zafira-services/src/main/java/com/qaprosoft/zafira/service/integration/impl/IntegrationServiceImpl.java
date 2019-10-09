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
package com.qaprosoft.zafira.service.integration.impl;

import com.qaprosoft.zafira.dbaccess.persistence.IntegrationRepository;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.entity.integration.IntegrationGroup;
import com.qaprosoft.zafira.models.entity.integration.IntegrationInfo;
import com.qaprosoft.zafira.models.entity.integration.IntegrationSetting;
import com.qaprosoft.zafira.models.entity.integration.IntegrationType;
import com.qaprosoft.zafira.models.push.events.ReinitEventMessage;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import com.qaprosoft.zafira.service.integration.IntegrationGroupService;
import com.qaprosoft.zafira.service.integration.IntegrationService;
import com.qaprosoft.zafira.service.integration.IntegrationSettingService;
import com.qaprosoft.zafira.service.integration.IntegrationTypeService;
import com.qaprosoft.zafira.service.integration.core.IntegrationInitializer;
import com.qaprosoft.zafira.service.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.service.util.EventPushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.INTEGRATION_CAN_NOT_BE_CREATED;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_NOT_FOUND;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.INVITATION_NOT_FOUND;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    private static final String ERR_MSG_MULTIPLE_INTEGRATIONS_ARE_NOT_ALLOWED = "Multiple integrations of type '%s' are not allowed";
    private static final String ERR_MSG_INTEGRATION_NOT_FOUND_BY_ID = "Integration with id '%d' not found";
    private static final String ERR_MSG_INTEGRATION_NOT_FOUND_BY_BACK_REFERENCE_ID = "Integration with back reference id '%s' not found";
    private static final String ERR_MSG_DEFAULT_VALUE_IS_NOT_PROVIDED_BY_TYPE_ID = "Default value for integration with id '%d' is not provided";
    private static final String ERR_MSG_DEFAULT_VALUE_IS_NOT_PROVIDED_BY_NAME = "Default value for integration with name '%s' is not provided";

    private final IntegrationRepository integrationRepository;
    private final IntegrationGroupService integrationGroupService;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationSettingService integrationSettingService;
    private final EventPushService<ReinitEventMessage> eventPushService;
    private final IntegrationInitializer integrationInitializer;

    public IntegrationServiceImpl(
            IntegrationRepository integrationRepository,
            IntegrationGroupService integrationGroupService,
            IntegrationTypeService integrationTypeService,
            IntegrationSettingService integrationSettingService,
            EventPushService<ReinitEventMessage> eventPushService,
            IntegrationInitializer integrationInitializer
    ) {
        this.integrationRepository = integrationRepository;
        this.integrationGroupService = integrationGroupService;
        this.integrationTypeService = integrationTypeService;
        this.integrationSettingService = integrationSettingService;
        this.eventPushService = eventPushService;
        this.integrationInitializer = integrationInitializer;
    }

    @Override
    @Transactional()
    public Integration create(Integration integration, Long typeId) {
        IntegrationType type = integrationTypeService.retrieveById(typeId);
        verifyMultipleAllowedForType(type);
        unassignIfDefault(integration, typeId);
        String backReferenceId = generateBackReferenceId(typeId);
        integration.setId(null);
        // TODO: 9/11/19 check with PO if we can persist integration without enabling it / connecting to it
        integration.setEnabled(true);
        integration.setType(type);
        integration.setBackReferenceId(backReferenceId);
        integration = integrationRepository.save(integration);

        for (IntegrationSetting setting : integration.getSettings()) {
            setting.setIntegration(integration);
        }
        List<IntegrationSetting> integrationSettings = integrationSettingService.batchCreate(integration.getSettings(), typeId);
        integration.setSettings(integrationSettings);

        notifyToolReinitiated(integration);
        return integration;
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveById(Long id) {
        return integrationRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_NOT_FOUND, ERR_MSG_INTEGRATION_NOT_FOUND_BY_ID, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveByBackReferenceId(String backReferenceId) {
        return integrationRepository.findIntegrationByBackReferenceId(backReferenceId)
                                    .orElseThrow(() -> new ResourceNotFoundException(INVITATION_NOT_FOUND, ERR_MSG_INTEGRATION_NOT_FOUND_BY_BACK_REFERENCE_ID, backReferenceId));
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveByJobAndIntegrationTypeName(Job job, String integrationTypeName) {
        List<Integration> integrations = retrieveByIntegrationsTypeName(integrationTypeName);
        String jenkinsHost = job.getJenkinsHost();
        return getIntegrationByJenkinsHost(integrations, jenkinsHost);
    }

    private Integration getIntegrationByJenkinsHost(List<Integration> integrations, String jenkinsHost) {
        return integrations.stream()
                           .filter(integration -> findIntegrationSettingWithJenkinsHost(jenkinsHost, integration))
                           .findAny().orElse(new Integration());
    }

    private boolean findIntegrationSettingWithJenkinsHost(String jenkinsHost, Integration integration) {
        return integration.getSettings()
                          .stream()
                          .anyMatch(setting -> setting.getValue().equals(jenkinsHost));
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveDefaultByIntegrationTypeId(Long integrationTypeId) {
        return integrationRepository.findIntegrationByTypeIdAndDefaultIsTrue(integrationTypeId)
                                    .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_NOT_FOUND, ERR_MSG_DEFAULT_VALUE_IS_NOT_PROVIDED_BY_TYPE_ID, integrationTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveIntegrationsByTypeId(Long typeId) {
        return integrationRepository.getIntegrationsByTypeId(typeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveIntegrationsByGroupId(Long groupId) {
        return integrationRepository.findByGroupId(groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveDefaultByIntegrationTypeName(String integrationTypeName) {
        return integrationRepository.findIntegrationByTypeNameAndDefaultIsTrue(integrationTypeName)
                                    .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_NOT_FOUND, ERR_MSG_DEFAULT_VALUE_IS_NOT_PROVIDED_BY_NAME, integrationTypeName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveAll() {
        return integrationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveIntegrationsByGroupName(String integrationGroupName) {
        return integrationRepository.findIntegrationsByGroupName(integrationGroupName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveByIntegrationsTypeName(String integrationTypeName) {
        return integrationRepository.findIntegrationsByTypeName(integrationTypeName);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Map<String, List<IntegrationInfo>>> retrieveInfo() {
        List<IntegrationGroup> integrationGroups = integrationGroupService.retrieveAll();
        return integrationGroups.stream().map(integrationGroup -> {
            Map<String, List<IntegrationInfo>> integrationInfos = integrationGroup.getTypes().stream().map(integrationType -> {
                List<Integration> integrations = retrieveIntegrationsByTypeId(integrationType.getId());
                List<IntegrationInfo> integrationConnections = buildInfo(integrationGroup.getName(), integrations);
                return new AbstractMap.SimpleEntry<>(integrationType.getName(), integrationConnections);
            }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
            return new AbstractMap.SimpleEntry<>(integrationGroup.getName(), integrationInfos);
        }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationInfo retrieveInfoByIntegrationId(String groupName, Long id) {
        Integration integration = retrieveById(id);
        return collectRuntimeIntegrationInfo(groupName, integration);
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationInfo retrieveInfoByIntegration(Integration integration) {
        IntegrationGroup integrationGroup = integrationGroupService.retrieveByIntegrationTypeId(integration.getType().getId());
        return collectRuntimeIntegrationInfo(integrationGroup.getName(), integration);
    }

    private List<IntegrationInfo> buildInfo(String groupName, List<Integration> integrations) {
        return integrations.stream()
                           .map(integration -> collectRuntimeIntegrationInfo(groupName, integration))
                           .collect(Collectors.toList());
    }

    private IntegrationInfo collectRuntimeIntegrationInfo(String groupName, Integration integration) {
        AbstractIntegrationService integrationService = integrationInitializer.getIntegrationServices().get(groupName);
        boolean enabled = integration.isEnabled();
        boolean connected = false;
        if (enabled) {
            connected = integrationService.isEnabledAndConnected(integration.getId());
        }
        // TODO: 9/11/19 switch connected and enabled places to avoid confusion
        return new IntegrationInfo(integration.getId(), integration.getBackReferenceId(), integration.isDefault(), connected, enabled);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integration update(Integration integration) {
        IntegrationType integrationType = integrationTypeService.retrieveByIntegrationId(integration.getId());
        unassignIfDefault(integration, null);

        Integration dbIntegration = retrieveById(integration.getId());
        integration.setBackReferenceId(dbIntegration.getBackReferenceId());
        integration.setType(dbIntegration.getType());

        for (IntegrationSetting setting : integration.getSettings()) {
            setting.setIntegration(integration);
        }
        List<IntegrationSetting> integrationSettings = integrationSettingService.batchUpdate(integration.getSettings(), integrationType.getId());
        integration.setSettings(integrationSettings);

        integration = integrationRepository.save(integration);

        notifyToolReinitiated(integration);

        return integration;
    }

    private String generateBackReferenceId(Long integrationTypeId) {
        IntegrationType integrationType = integrationTypeService.retrieveById(integrationTypeId);
        return integrationType.getName() + "_" + UUID.randomUUID().toString();
    }

    private void unassignIfDefault(Integration integration, Long integrationTypeId) {
        if (integration.isDefault()) {
            if (integrationTypeId == null) { // can be null on update
                IntegrationType integrationType = integrationTypeService.retrieveByIntegrationId(integration.getId());
                integrationTypeId = integrationType.getId();
            }
            Integration defaultIntegration = retrieveDefaultByIntegrationTypeId(integrationTypeId);
            defaultIntegration.setDefault(false);
            integrationRepository.saveAndFlush(defaultIntegration);
        }
    }

    private void verifyMultipleAllowedForType(IntegrationType integrationType) {
        IntegrationGroup integrationGroup = integrationGroupService.retrieveByIntegrationTypeId(integrationType.getId());
        if (!integrationGroup.isMultipleAllowed()) {
            // TODO: 9/11/19 switch to count by type
            List<Integration> integrations = retrieveIntegrationsByTypeId(integrationType.getId());
            if (!integrations.isEmpty()) {
                throw new IllegalOperationException(INTEGRATION_CAN_NOT_BE_CREATED, ERR_MSG_MULTIPLE_INTEGRATIONS_ARE_NOT_ALLOWED, integrationType.getName());
            }
        }
    }

    private void notifyToolReinitiated(Integration integration) {
        String tenantName = TenancyContext.getTenantName();
        eventPushService.convertAndSend(EventPushService.Type.SETTINGS, new ReinitEventMessage(tenantName, integration.getId()));
        integrationInitializer.initIntegration(integration, tenantName);
    }

}
