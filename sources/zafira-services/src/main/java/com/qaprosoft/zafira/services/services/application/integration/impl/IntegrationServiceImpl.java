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

import com.qaprosoft.zafira.dbaccess.persistence.IntegrationRepository;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.entity.integration.IntegrationSetting;
import com.qaprosoft.zafira.models.entity.integration.IntegrationType;
import com.qaprosoft.zafira.services.exceptions.EntityNotExistsException;
import com.qaprosoft.zafira.services.exceptions.IllegalOperationException;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationGroupService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationSettingService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationTypeService;
import com.qaprosoft.zafira.services.util.EventPushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    private static final String ERR_MSG_NOT_MULTIPLE_ALLOWED_INTEGRATION = "Integration with type '%s' is not multiple allowed";
    private static final String ERR_MSG_INTEGRATION_NOT_FOUND_BY_ID = "Integration with id '%d' not found";
    private static final String ERR_MSG_INTEGRATION_NOT_FOUND_BY_BACK_REFERENCE_ID = "Integration with back reference id '%s' not found";
    private static final String ERR_MSG_DEFAULT_VALUE_IS_NOT_PROViDED_BY_TYPE_ID = "Default value for integration with id '%s' is nod provided";

    private final IntegrationRepository integrationRepository;
    private final IntegrationGroupService integrationGroupService;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationSettingService integrationSettingService;
    private final EventPushService eventPushService;

    public IntegrationServiceImpl(
            IntegrationRepository integrationRepository,
            IntegrationGroupService integrationGroupService,
            IntegrationTypeService integrationTypeService,
            IntegrationSettingService integrationSettingService,
            EventPushService eventPushService
    ) {
        this.integrationRepository = integrationRepository;
        this.integrationGroupService = integrationGroupService;
        this.integrationTypeService = integrationTypeService;
        this.integrationSettingService = integrationSettingService;
        this.eventPushService = eventPushService;
    }

    // TODO: 2019-08-23 add notification and add to context mapping
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integration create(Integration integration, Long integrationTypeId) {
        unassignCurrentDefaultIntegrationIfNeed(integration, integrationTypeId);
        integration.setEnabled(true);
        IntegrationType integrationType = integrationTypeService.retrieveById(integrationTypeId);
        verifyIntegration(integrationType);
        integration.setType(integrationType);
        integrationRepository.save(integration);
//        Set<IntegrationSetting> integrationSettingSet = integrationSettingService.create(integration.getSettings(), integration.getId());
//        integration.setSettings(new ArrayList<>(integrationSettingSet));
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
    public List<Integration> retrieveAll() {
        return integrationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveByIntegrationTypeId(Long integrationTypeId) {
        return integrationRepository.getIntegrationsByTypeId(integrationTypeId);
    }

    // TODO: 2019-08-23 add notification and update in context mapping
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integration update(Integration integration) {
        unassignCurrentDefaultIntegrationIfNeed(integration, null);
        IntegrationType integrationType = integrationTypeService.retrieveById(integration.getId());
        verifyIntegration(integrationType);
        integrationRepository.save(integration);
//        integrationSettingService.update(integration.getSettings(), integration.getId());
        return integration;
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

    /**
     * Sends message to broker to notify about changed integration.
     *
     * @param tool that was re-initiated
     * @param tenant whose integration was updated
     */
    /*public void notifyToolReinitiated(Tool tool, String tenant) {
        eventPushService.convertAndSend(EventPushService.Type.SETTINGS, new ReinitEventMessage(tenant, tool));
        initIntegration(tool, tenant);
    }

    @RabbitListener(queues = "#{settingsQueue.name}")
    public void process(Message message) {
        ReinitEventMessage rm = new Gson().fromJson(new String(message.getBody()), ReinitEventMessage.class);
        if (!eventPushService.isSettingQueueConsumer(message)) {
            initIntegration(rm.getTool(), rm.getTenancy());
        }
    }*/

}
