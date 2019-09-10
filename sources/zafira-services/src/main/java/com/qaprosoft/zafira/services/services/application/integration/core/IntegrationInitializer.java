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
package com.qaprosoft.zafira.services.services.application.integration.core;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.entity.integration.IntegrationGroup;
import com.qaprosoft.zafira.models.entity.integration.IntegrationType;
import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationGroupService;
import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.proxy.IntegrationAdapterProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IntegrationInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationInitializer.class);

    private static final String INITIALIZING_INTEGRATION_BY_TYPE_START = "Initializing integration %s of type %s";
    private static final String ERR_MSG_GROUP_NOT_EXISTS = "Integration group with name %s does not exist";

    private final Map<String, IntegrationAdapterProxy> integrationProxies;
    private final Map<String, AbstractIntegrationService> integrationServices;
    private final IntegrationGroupService integrationGroupService;

    public IntegrationInitializer(
            @Lazy Map<String, IntegrationAdapterProxy> integrationProxies,
            @Lazy Map<String, AbstractIntegrationService> integrationServices,
            IntegrationGroupService integrationGroupService
    ) {
        this.integrationProxies = integrationProxies;
        this.integrationServices = integrationServices;
        this.integrationGroupService = integrationGroupService;
    }

    public void initIntegration(Integration integration, String tenant) {
        IntegrationType type = integration.getType();
        IntegrationGroup group = integrationGroupService.retrieveByIntegrationTypeId(type.getId());

        TenancyContext.setTenantName(tenant);
        initByType(group.getName(), type.getName(), integration);
        TenancyContext.setTenantName(null);
    }

    private void initByType(String group, String type, Integration integration) {
        LOGGER.info(String.format(INITIALIZING_INTEGRATION_BY_TYPE_START, integration.getName(), type));

        IntegrationAdapterProxy adapterProxy = integrationProxies.values().stream()
                                                                 .filter(integrationAdapterProxy -> integrationAdapterProxy.getGroup().equals(group))
                                                                 .findFirst()
                                                                 .orElseThrow(() -> new IntegrationException(String.format(ERR_MSG_GROUP_NOT_EXISTS, group)));

        adapterProxy.initializeByType(type, List.of(integration));
    }

    public Map<String, AbstractIntegrationService> getIntegrationServices() {
        return integrationServices.entrySet().stream()
                                  .collect(Collectors.toMap(entry -> entry.getValue().getIntegrationAdapterProxy().getGroup(), Map.Entry::getValue));
    }
}
