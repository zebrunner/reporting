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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.IntegrationMapper;
import com.qaprosoft.zafira.dbaccess.persistence.IntegrationRepository;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.entity.integration.IntegrationSetting;
import com.qaprosoft.zafira.models.entity.integration.IntegrationType;
import com.qaprosoft.zafira.models.push.events.ReinitEventMessage;
import com.qaprosoft.zafira.services.exceptions.EntityNotExistsException;
import com.qaprosoft.zafira.services.exceptions.IllegalOperationException;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationGroupService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationSettingService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationTypeService;
import com.qaprosoft.zafira.services.services.application.integration.core.IntegrationInitializer;
import com.qaprosoft.zafira.services.util.EventPushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    private static final String ERR_MSG_NOT_MULTIPLE_ALLOWED_INTEGRATION = "Integration with type '%s' is not multiple allowed";
    private static final String ERR_MSG_INTEGRATION_NOT_FOUND_BY_ID = "Integration with id '%d' not found";
    private static final String ERR_MSG_INTEGRATION_NOT_FOUND_BY_BACK_REFERENCE_ID = "Integration with back reference id '%s' not found";
    private static final String ERR_MSG_DEFAULT_VALUE_IS_NOT_PROViDED_BY_TYPE_ID = "Default value for integration with id '%d' is nod provided";
    private static final String ERR_MSG_DEFAULT_VALUE_IS_NOT_PROViDED_BY_NAME = "Default value for integration with name '%s' is nod provided";

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
    @Transactional(rollbackFor = Exception.class)
    public Integration create(Integration integration, Long integrationTypeId) {
        unassignCurrentDefaultIntegrationIfNeed(integration, integrationTypeId);
        integration.setEnabled(true);
        IntegrationType integrationType = integrationTypeService.retrieveById(integrationTypeId);
        verifyIntegration(integrationType);
        integration.setType(integrationType);
        String backReferenceId = generateBackReferenceId(integrationTypeId);
        integration.setBackReferenceId(backReferenceId);
        integrationRepository.save(integration);
//        Set<IntegrationSetting> integrationSettingSet = integrationSettingService.create(integration.getIntegrationSettings(), integration.getId());
//        integration.setIntegrationSettings(new ArrayList<>(integrationSettingSet));
        return integration;
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveById(Long id) {
        return integrationRepository.findById(id)
                                    .orElseThrow(() -> new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_NOT_FOUND_BY_ID, id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveByBackReferenceId(String backReferenceId) {
        return integrationRepository.findIntegrationByBackReferenceId(backReferenceId)
                                    .orElseThrow(() -> new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_NOT_FOUND_BY_BACK_REFERENCE_ID, backReferenceId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveDefaultByIntegrationTypeId(Long integrationTypeId) {
        return integrationRepository.findIntegrationByTypeIdAndDefaultIsTrue(integrationTypeId)
                                    .orElseThrow(() -> new EntityNotExistsException(String.format(ERR_MSG_DEFAULT_VALUE_IS_NOT_PROViDED_BY_TYPE_ID, integrationTypeId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List <Integration> getIntegrationsByTypeId(Long typeId) {
        return integrationRepository.getIntegrationsByTypeId(typeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveDefaultByIntegrationTypeName(String integrationTypeName) {
        return integrationRepository.findIntegrationByTypeNameAndDefaultIsTrue(integrationTypeName)
                                    .orElseThrow(() -> new EntityNotExistsException(String.format(ERR_MSG_DEFAULT_VALUE_IS_NOT_PROViDED_BY_NAME, integrationTypeName)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveAll() {
        return integrationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveByIntegrationTypeId(Long integrationTypeId) {
        return integrationRepository.getIntegrationsByTypeId(integrationTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveByIntegrationGroupName(String integrationGroupName) {
        return integrationRepository.findIntegrationByTypeGroupName(integrationGroupName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integration update(Integration integration) {
        unassignCurrentDefaultIntegrationIfNeed(integration, null);
        IntegrationType integrationType = integrationTypeService.retrieveById(integration.getId());
        verifyIntegration(integrationType);
        integrationRepository.save(integration);
//        integrationSettingService.update(integration.getSettings(), integration.getId());
        notifyToolReinitiated(integration);
        return integration;
    }

    private String generateBackReferenceId(Long integrationTypeId) {
        IntegrationType integrationType = integrationTypeService.retrieveById(integrationTypeId);
        return integrationType.getName() + "_" + UUID.randomUUID().toString();
    }

    private void unassignCurrentDefaultIntegrationIfNeed(Integration integration, Long integrationTypeId) {
        if (integration.isDefault()) {
            if (integrationTypeId == null) {
                IntegrationType integrationType = integrationTypeService.retrieveById(integration.getId());
                integrationTypeId = integrationType.getId();
            }
            Integration defaultIntegration = retrieveDefaultByIntegrationTypeId(integrationTypeId);
            defaultIntegration.setDefault(false);
            update(defaultIntegration);
        }
    }

    private void verifyIntegration(IntegrationType integrationType) {
        if (!integrationType.getGroup().isMultipleAllowed()) {
            List<Integration> integrations = integrationType.getIntegrations();
            if (integrations.size() != 0) {
                throw new IllegalOperationException(String.format(ERR_MSG_NOT_MULTIPLE_ALLOWED_INTEGRATION, integrationType.getName()));
            }
        }
    }

    private void notifyToolReinitiated(Integration integration) {
        String tenantName = TenancyContext.getTenantName();
        eventPushService.convertAndSend(EventPushService.Type.SETTINGS, new ReinitEventMessage(tenantName, integration.getId()));
        integrationInitializer.initIntegration(integration, tenantName);
    }

}
