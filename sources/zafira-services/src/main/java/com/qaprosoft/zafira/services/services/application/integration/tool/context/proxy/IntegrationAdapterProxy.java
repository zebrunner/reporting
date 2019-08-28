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
package com.qaprosoft.zafira.services.services.application.integration.tool.context.proxy;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.integration.Integration;
import com.qaprosoft.zafira.models.db.integration.IntegrationGroup;
import com.qaprosoft.zafira.models.db.integration.IntegrationSetting;
import com.qaprosoft.zafira.models.db.integration.IntegrationType;
import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationGroupService;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.IntegrationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//todo remove integration group service from here
public abstract class IntegrationAdapterProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationAdapterProxy.class);

    private static final String ERR_MSG_MANDATORY_INTEGRATION_PARAMS_MISSING = "Integration settings [%s] are mandatory for integration %s";
    private static final String ERR_MSG_CANNOT_CREATE_ADAPTER = "Cannot create an instance of %s adapter for integration %s";


    private static final Map<String, Map<Long, IntegrationAdapter>> TENANT_ADAPTERS = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;
    private final IntegrationGroupService integrationGroupService;
    private final String group;
    private final Map<String, Class<? extends IntegrationAdapter>> adapterClasses;
    private final Map<String, Object> additionalParameters;

    public IntegrationAdapterProxy(
            ApplicationContext applicationContext,
            IntegrationGroupService integrationGroupService,
            String group,
            Map<String, Class<? extends IntegrationAdapter>> adapterClasses,
            Map<String, Object> additionalParameters
    ) {
        this.applicationContext = applicationContext;
        this.integrationGroupService = integrationGroupService;
        this.group = group;
        this.adapterClasses = adapterClasses;
        this.additionalParameters = additionalParameters;
    }

    public IntegrationAdapterProxy(
            ApplicationContext applicationContext,
            IntegrationGroupService integrationGroupService,
            String group,
            Map<String, Class<? extends IntegrationAdapter>> adapterClasses
    ) {
        this(applicationContext, integrationGroupService, group, adapterClasses, null);
    }

    private static synchronized void putAdapter(IntegrationAdapter integrationAdapter) {
        String tenant = TenancyContext.getTenantName();
        if (TENANT_ADAPTERS.get(tenant) == null) {
            TENANT_ADAPTERS.put(tenant, new ConcurrentHashMap<>());
        }

        Long integrationId = integrationAdapter.getIntegrationId();
        TENANT_ADAPTERS.get(tenant).put(integrationId, integrationAdapter);
    }

    public static synchronized Optional<IntegrationAdapter> getAdapter(Long integrationId) {
        Map<Long, IntegrationAdapter> adapters = TENANT_ADAPTERS.get(TenancyContext.getTenantName());
        return Optional.ofNullable(adapters.get(integrationId));
    }

    public static synchronized Optional<IntegrationAdapter> getDefaultAdapter(String type) {

        //todo get default integration by type :: integrationService.getDefaultIntegration(type);
        // return adapter by integration id from above
        Map<Long, IntegrationAdapter> adapters = TENANT_ADAPTERS.get(TenancyContext.getTenantName());
        return adapters.values()
                       .stream()
//                       .filter(adapter -> adapter.getType().equals(type))
//                       .filter(adapter -> adapter.getIntegration().isDefault())
                       .findFirst();
    }

    public void init() {
        IntegrationGroup integrationGroup = integrationGroupService.retrieveByName(getGroup());
        List<IntegrationType> integrationTypes = integrationGroup.getIntegrationTypes();

        integrationTypes.forEach(integrationType -> {


            List<Integration> integrations = integrationType.getIntegrations();

            // collect all enabled integrations first
            List<Integration> enabledIntegrations = integrations.stream()
                                                                .filter(Integration::isEnabled)
                                                                .collect(Collectors.toList());

            // make sure all of those have mandatory settings in place
            long validIntegrationsCount = enabledIntegrations.stream()
                                                             .filter(this::hasMandatorySettingsSet)
                                                             .count();


            // if not all enabled integrations are properly configured - fail startup, otherwise initialize adapters
            if (enabledIntegrations.size() != validIntegrationsCount) {
                //todo add code and message
                throw new IntegrationException();
            } else {
                Class<? extends IntegrationAdapter> adapterClass = adapterClasses.get(integrationType.getName());
                initializeAdapters(adapterClass, enabledIntegrations);
            }
        });
    }

    private void initializeAdapters(Class<? extends IntegrationAdapter> adapterClass, List<Integration> enabledIntegrations) {
        enabledIntegrations.stream()
                           .map(integration -> createAdapter(integration, adapterClass))
                           .forEach(IntegrationAdapterProxy::putAdapter);
    }

    private boolean hasMandatorySettingsSet(Integration integration) {
        List<IntegrationSetting> integrationSettings = integration.getIntegrationSettings();

        // go over all mandatory integration settings and check if those have values set
        String missingSettings = integrationSettings.stream()
                                                    .filter(integrationSetting -> integrationSetting.getIntegrationParam().isMandatory())
                                                    .filter(integrationSetting -> integrationSetting.getValue() != null && !integrationSetting.getValue().isBlank())
                                                    .map(integrationSetting -> integrationSetting.getIntegrationParam().getName())
                                                    .collect(Collectors.joining(", "));

        if (!missingSettings.isEmpty()) {
            LOGGER.error(String.format(ERR_MSG_MANDATORY_INTEGRATION_PARAMS_MISSING, missingSettings, integration.getName()));
            return false;
        } else {
            return true;
        }
    }

    private IntegrationAdapter createAdapter(Integration integration, Class<? extends IntegrationAdapter> adapterClass) {
        IntegrationAdapter adapter = null;
        Constructor constructor = getCorrectConstructor(adapterClass);
        Object[] params = buildParameterObjects(constructor, integration);
        try {
            adapter = (IntegrationAdapter) constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IntegrationException(String.format(ERR_MSG_CANNOT_CREATE_ADAPTER, adapterClass.getSimpleName(), integration.getName()));
        } catch (Exception e) {
            // todo interrupt execution since we can not create adapters for all integrations?
            LOGGER.error(e.getMessage(), e);
        }
        // can be null?
        return adapter;
    }

    private Constructor getCorrectConstructor(Class<? extends IntegrationAdapter> adapterClass) {
        return Arrays.stream(adapterClass.getDeclaredConstructors())
                     .filter(this::hasConstructorIntegrationParam)
                     .findFirst()
                     .orElseThrow(() -> new IntegrationException("Adapter must have at least one constructor with Integration type parameter"));
    }

    private boolean hasConstructorIntegrationParam(Constructor constructor) {
        return Arrays.stream(constructor.getParameterTypes()).anyMatch(Integration.class::isAssignableFrom);
    }

    private Object[] buildParameterObjects(Constructor constructor, Integration integration) {
        Parameter[] parameters = constructor.getParameters();
        return Arrays.stream(parameters)
                     .map(parameter -> {
                         Object param;
                         if (Integration.class.isAssignableFrom(parameter.getType())) {
                             param = integration;
                         } else if (Map.class.isAssignableFrom(parameter.getType())) {
                             param = additionalParameters;
                         } else {
                             param = applicationContext.getBean(parameter.getType());
                         }
                         return param;
                     })
                     .toArray(Object[]::new);
    }

    public String getGroup() {
        return group;
    }

}
