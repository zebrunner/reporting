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
import com.qaprosoft.zafira.models.db.integration.Integration;
import com.qaprosoft.zafira.models.db.integration.IntegrationGroup;
import com.qaprosoft.zafira.models.db.integration.IntegrationSetting;
import com.qaprosoft.zafira.models.db.integration.IntegrationType;
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

    private final IntegrationMapper integrationMapper;
    private final IntegrationGroupService integrationGroupService;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationSettingService integrationSettingService;
    private final EventPushService eventPushService;

    public IntegrationServiceImpl(
            IntegrationMapper integrationMapper,
            IntegrationGroupService integrationGroupService,
            IntegrationTypeService integrationTypeService,
            IntegrationSettingService integrationSettingService,
            EventPushService eventPushService
    ) {
        this.integrationMapper = integrationMapper;
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
        verifyIntegration(integrationTypeId);
        integrationMapper.create(integration, integrationTypeId);
        Set<IntegrationSetting> integrationSettingSet = integrationSettingService.create(integration.getIntegrationSettings(), integration.getId());
        integration.setIntegrationSettings(new ArrayList<>(integrationSettingSet));
        return integration;
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveById(Long id) {
        Integration integration = integrationMapper.findById(id);
        if (integration == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_NOT_FOUND_BY_ID, id));
        }
        return integration;
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveByBackReferenceId(String backReferenceId) {
        Integration integration = integrationMapper.findByBackReferenceId(backReferenceId);
        if (integration == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_NOT_FOUND_BY_BACK_REFERENCE_ID, backReferenceId));
        }
        return integration;
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveDefaultByIntegrationTypeId(Long integrationTypeId) {
        return integrationMapper.findDefaultByIntegrationTypeId(integrationTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveAll() {
        return integrationMapper.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveByIntegrationTypeId(Long integrationTypeId) {
        return integrationMapper.findByIntegrationTypeId(integrationTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveByIntegrationGroupName(String integrationGroupName) {
        return integrationMapper.findByIntegrationGroupName(integrationGroupName);
    }

    // TODO: 2019-08-23 add notification and update in context mapping
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integration update(Integration integration) {
        unassignCurrentDefaultIntegrationIfNeed(integration, null);
        IntegrationType integrationType = integrationTypeService.retrieveByIntegrationId(integration.getId());
        verifyIntegration(integrationType.getId());
        integrationMapper.update(integration);
        integrationSettingService.update(integration.getIntegrationSettings(), integration.getId());
        return integration;
    }

    private void unassignCurrentDefaultIntegrationIfNeed(Integration integration, Long integrationTypeId) {
        if (integration.isDefault()) {
            if (integrationTypeId == null) {
                IntegrationType integrationType = integrationTypeService.retrieveByIntegrationId(integration.getId());
                integrationTypeId = integrationType.getId();
            }
            Integration defaultIntegration = retrieveDefaultByIntegrationTypeId(integrationTypeId);
            defaultIntegration.setDefault(false);
            update(defaultIntegration);
        }
    }

    private void verifyIntegration(Long integrationTypeId) {
        IntegrationType integrationType = integrationTypeService.retrieveById(integrationTypeId);
        IntegrationGroup integrationGroup = integrationGroupService.retrieveByIntegrationTypeId(integrationType.getId());
        if (!integrationGroup.isMultipleAllowed()) {
            List<Integration> integrations = retrieveByIntegrationTypeId(integrationType.getId());
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
