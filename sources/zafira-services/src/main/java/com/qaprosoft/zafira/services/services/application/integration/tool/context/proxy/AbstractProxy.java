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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProxy.class);

    private static final String ERR_MSG_MANDATORY_INTEGRATION_PARAMS_MISSING = "Integration settings %s are mandatory for integration with name '%s'";

    private static final Map<String, Map<Long, IntegrationAdapter>> TENANT_ADAPTERS = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;
    private final IntegrationGroupService integrationGroupService;
    private final String group;
    private final Map<String, Class<? extends IntegrationAdapter>> adapterClasses;
    private final Map<String, Object> additionalParameters;

    public AbstractProxy(ApplicationContext applicationContext,
                         IntegrationGroupService integrationGroupService,
                         String group,
                         Map<String, Class<? extends IntegrationAdapter>> adapterClasses,
                         Map<String, Object> additionalParameters) {
        this.applicationContext = applicationContext;
        this.integrationGroupService = integrationGroupService;
        this.group = group;
        this.adapterClasses = adapterClasses;
        this.additionalParameters = additionalParameters;
    }

    public AbstractProxy(ApplicationContext applicationContext, IntegrationGroupService integrationGroupService, String group, Map<String, Class<? extends IntegrationAdapter>> adapterClasses) {
        this(applicationContext, integrationGroupService, group, adapterClasses, null);
    }

    public static synchronized void putAdapter(IntegrationAdapter integrationAdapter) {
        if (TENANT_ADAPTERS.get(TenancyContext.getTenantName()) == null) {
            TENANT_ADAPTERS.put(TenancyContext.getTenantName(), new ConcurrentHashMap<>());
        }
        TENANT_ADAPTERS.get(TenancyContext.getTenantName()).put(integrationAdapter.getIntegration().getId(), integrationAdapter);
    }

    public static synchronized Optional<IntegrationAdapter> getAdapter(Long id) {
        Map<Long, IntegrationAdapter> adapters = TENANT_ADAPTERS.get(TenancyContext.getTenantName());
        return Optional.ofNullable(adapters.get(id));
    }

    public static synchronized Optional<IntegrationAdapter> getDefaultAdapter(String type) {
        Map<Long, IntegrationAdapter> adapters = TENANT_ADAPTERS.get(TenancyContext.getTenantName());
        return adapters.values().stream()
                       .filter(adapter -> adapter.getType().equals(type) && adapter.getIntegration().isDefault())
                       .findFirst();
    }

    public void init() {
        IntegrationGroup integrationGroup = integrationGroupService.retrieveByName(getGroup());
        List<IntegrationType> integrationTypes = integrationGroup.getIntegrationTypes();
        integrationTypes.forEach(integrationType -> {
            Class<? extends IntegrationAdapter> adapterClass = adapterClasses.get(integrationType.getName());
            List<Integration> integrations = integrationType.getIntegrations();

            // check if number of enabled integrations is equal to number of valid integrations
            // if yes - ok, proceed
            // if no -> log all invalid integrations, fail startup

            /*List<Integration> enabledIntegrations = integrations.stream()
                                                                .filter(Integration::isEnabled)
                                                                .collect(Collectors.toList());

            Long numberOfValidIntegrations = enabledIntegrations.stream()
                                                                .filter(this::isValidIntegration)
                                                                .count();

            List<IntegrationAdapter> integrationAdapters = Collections.emptyList();
            if (enabledIntegrations.size() != numberOfValidIntegrations) {
                // throw exception, fail startup
            } else {
                integrationAdapters = enabledIntegrations.stream()
                                                         .map(integration -> createAdapterInstance(integration, adapterClass))
                                                         .collect(Collectors.toList());

                integrationAdapters.forEach(AbstractProxy::putAdapter);
            }*/




            List<IntegrationAdapter> integrationAdapters = integrations.stream()
                                                                       .filter(Integration::isEnabled)
                                                                       .filter(this::isValidIntegration)
                                                                       .map(integration -> createAdapterInstance(integration, adapterClass)).collect(Collectors.toList());
            integrationAdapters.forEach(AbstractProxy::putAdapter);
        });
    }

    private boolean isValidIntegration(Integration integration) {
        boolean valid = true;
        List<IntegrationSetting> integrationSettings = integration.getIntegrationSettings();
        String mandatoryEmptySettings = integrationSettings.stream()
                                                           .filter(integrationSetting -> integrationSetting.getIntegrationParam().isMandatory() && integrationSetting.getValue() != null && !integrationSetting.getValue().isBlank())
                                                           .map(integrationSetting -> integrationSetting.getIntegrationParam().getName())
                                                           .collect(Collectors.joining(", "));

        if (mandatoryEmptySettings != null && mandatoryEmptySettings.length() != 0) {
            valid = false;
            LOGGER.error(String.format(ERR_MSG_MANDATORY_INTEGRATION_PARAMS_MISSING, mandatoryEmptySettings, integration.getName()));
        }

        return valid;
    }

    private IntegrationAdapter createAdapterInstance(Integration integration, Class<? extends IntegrationAdapter> adapterClass) {
        IntegrationAdapter integrationAdapter = null;
        Constructor constructor = getCorrectConstructor(adapterClass);
        Object[] params = buildParameterObjects(constructor, integration);
        try {
            integrationAdapter = (IntegrationAdapter) constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IntegrationException(String.format("Cannot to create %s adapter instance", adapterClass.getSimpleName()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return integrationAdapter;
    }

    private Constructor getCorrectConstructor(Class<? extends IntegrationAdapter> adapterClass) {
        return Arrays.stream(adapterClass.getDeclaredConstructors())
                     .filter(this::hasConstructorIntegrationParam)
                     .findFirst()
                     .orElseThrow(() -> new IntegrationException("Adapter must to have at least one constructor with Integration type parameter"));
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
