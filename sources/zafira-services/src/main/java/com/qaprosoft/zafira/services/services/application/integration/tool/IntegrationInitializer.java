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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.integration.tool;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.integration.Integration;
import com.qaprosoft.zafira.models.db.integration.IntegrationGroup;
import com.qaprosoft.zafira.models.db.integration.IntegrationType;
import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationGroupService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationTypeService;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.proxy.IntegrationAdapterProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class IntegrationInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationInitializer.class);

    private static final String INITIALIZING_INTEGRATION_BY_TYPE_START = "Starting to initialize %s integration by type %s";
    private static final String ERR_MSG_GROUP_NOT_EXISTS = "Integration group with name '%s' does not exist";

    private final Map<String, IntegrationAdapterProxy> integrationProxies;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationGroupService integrationGroupService;

    public IntegrationInitializer(Map<String, IntegrationAdapterProxy> integrationProxies, IntegrationTypeService integrationTypeService, IntegrationGroupService integrationGroupService) {
        this.integrationProxies = integrationProxies;
        this.integrationTypeService = integrationTypeService;
        this.integrationGroupService = integrationGroupService;
    }

    public void initIntegration(Integration integration, String tenancyName) {
        IntegrationType integrationType = integrationTypeService.retrieveByIntegrationId(integration.getId());
        IntegrationGroup integrationGroup = integrationGroupService.retrieveByIntegrationTypeId(integrationType.getId());
        TenancyContext.setTenantName(tenancyName);
        initByType(integrationGroup.getName(), integrationType.getName(), integration);
        TenancyContext.setTenantName(null);
    }

    private void initByType(String integrationGroupName, String integrationTypeName, Integration integration) {
        LOGGER.info(String.format(INITIALIZING_INTEGRATION_BY_TYPE_START, integration.getName(), integrationTypeName));
        IntegrationAdapterProxy adapterProxy = integrationProxies.values().stream()
                                                                 .filter(integrationAdapterProxy -> integrationAdapterProxy.getGroup().equals(integrationGroupName))
                                                                 .findFirst()
                                                                 .orElseThrow(() -> new IntegrationException(String.format(ERR_MSG_GROUP_NOT_EXISTS, integrationGroupName)));
        adapterProxy.initializeByType(integrationTypeName, List.of(integration));
    }

}
