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
package com.qaprosoft.zafira.service.integration.tool.proxy;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.entity.integration.IntegrationGroup;
import com.qaprosoft.zafira.models.entity.integration.IntegrationSetting;
import com.qaprosoft.zafira.models.entity.integration.IntegrationType;
import com.qaprosoft.zafira.service.CryptoService;
import com.qaprosoft.zafira.service.exception.IntegrationException;
import com.qaprosoft.zafira.service.integration.IntegrationGroupService;
import com.qaprosoft.zafira.service.integration.IntegrationService;
import com.qaprosoft.zafira.service.integration.tool.adapter.IntegrationAdapter;
import org.apache.commons.lang.StringUtils;
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

public abstract class IntegrationAdapterProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationAdapterProxy.class);

    private static final String ERR_MSG_MANDATORY_INTEGRATION_PARAMS_MISSING = "Integration settings [%s] are mandatory for integration %s";
    private static final String ERR_MSG_CANNOT_CREATE_ADAPTER = "Cannot create an instance of %s adapter for integration %s";

    private static final Map<String, Map<Long, IntegrationAdapter>> TENANT_ADAPTERS = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;
    private final IntegrationGroupService integrationGroupService;
    private final IntegrationService integrationService;
    private final CryptoService cryptoService;
    private final String group;
    private final Map<String, Class<? extends IntegrationAdapter>> adapterClasses;
    private final Map<String, Object> additionalParameters;

    public IntegrationAdapterProxy(
            ApplicationContext applicationContext,
            IntegrationGroupService integrationGroupService,
            IntegrationService integrationService,
            CryptoService cryptoService,
            String group,
            Map<String, Class<? extends IntegrationAdapter>> adapterClasses,
            Map<String, Object> additionalParameters
    ) {
        this.applicationContext = applicationContext;
        this.integrationGroupService = integrationGroupService;
        this.integrationService = integrationService;
        this.cryptoService = cryptoService;
        this.group = group;
        this.adapterClasses = adapterClasses;
        this.additionalParameters = additionalParameters;
    }

    public IntegrationAdapterProxy(
            ApplicationContext applicationContext,
            IntegrationGroupService integrationGroupService,
            IntegrationService integrationService,
            CryptoService cryptoService,
            String group,
            Map<String, Class<? extends IntegrationAdapter>> adapterClasses
    ) {
        this(applicationContext, integrationGroupService, integrationService, cryptoService, group, adapterClasses, null);
    }

    private static synchronized void putAdapter(IntegrationAdapter integrationAdapter) {
        String tenant = TenancyContext.getTenantName();
        if (TENANT_ADAPTERS.get(tenant) == null) {
            TENANT_ADAPTERS.put(tenant, new ConcurrentHashMap<>());
        }

        Long integrationId = integrationAdapter.getIntegrationId();
        TENANT_ADAPTERS.get(tenant).put(integrationId, integrationAdapter);
    }

    public synchronized Optional<IntegrationAdapter> getAdapter(String backReferenceId) {
        Integration integration = integrationService.retrieveByBackReferenceId(backReferenceId);
        return getAdapter(integration.getId());
    }

    public static synchronized Optional<IntegrationAdapter> getAdapter(Long integrationId) {
        Map<Long, IntegrationAdapter> adapters = TENANT_ADAPTERS.get(TenancyContext.getTenantName());
        IntegrationAdapter adapter = adapters != null ? adapters.get(integrationId) : null;
        return Optional.ofNullable(adapter);
    }

    public synchronized Optional<IntegrationAdapter> getDefaultAdapter(String type) {
        Integration defaultIntegration = integrationService.retrieveDefaultByIntegrationTypeName(type);
        return getAdapter(defaultIntegration.getId());
    }

    public void init() {
        IntegrationGroup integrationGroup = integrationGroupService.retrieveByName(getGroup());
        List<IntegrationType> integrationTypes = integrationGroup.getTypes();
        integrationTypes.forEach(integrationType -> initializeByType(integrationType.getName(), integrationService.retrieveIntegrationsByTypeId(integrationType.getId())));
    }

    // TODO: 9/12/19 discuss about possibility to create empty integrations
    public void initializeByType(String integrationTypeName, List<Integration> integrations) {
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
            // TODO: 10/11/19 uncomment and remove logger when customer integrations will be correct (build proccess is down if current integrations are not valid)
            LOGGER.error("Integrations " + integrations.stream().map(Integration::getName).collect(Collectors.joining(", ")) + " was not initialized");
            //throw new IntegrationException();
        } else {
            Class<? extends IntegrationAdapter> adapterClass = adapterClasses.get(integrationTypeName);
            initializeAdapters(adapterClass, enabledIntegrations);
        }
    }

    private void initializeAdapters(Class<? extends IntegrationAdapter> adapterClass, List<Integration> enabledIntegrations) {
        enabledIntegrations.stream()
                           .map(integration -> createAdapter(integration, adapterClass))
                           .forEach(IntegrationAdapterProxy::putAdapter);
    }

    /**
     * Checks whether integration has missing mandatory setting or not
     */
    private boolean hasMandatorySettingsSet(Integration integration) {
        List<IntegrationSetting> integrationSettings = integration.getSettings();

        String missingSettings = integrationSettings.stream()
                                                    .filter(integrationSetting -> integrationSetting.getParam().isMandatory())
                                                    .filter(integrationSetting -> !hasValueSet(integrationSetting))
                                                    .map(integrationSetting -> integrationSetting.getParam().getName())
                                                    .collect(Collectors.joining(", "));

        if (missingSettings.isEmpty()) {
            return true;
        } else {
            LOGGER.error(String.format(ERR_MSG_MANDATORY_INTEGRATION_PARAMS_MISSING, missingSettings, integration.getName()));
            return false;
        }
    }

    private boolean hasValueSet(IntegrationSetting setting) {
        boolean hasTextValueSet = setting.getValue() != null && !setting.getValue().isBlank();
        boolean hasBinaryValueSet = setting.getBinaryData() != null && setting.getBinaryData().length != 0;

        return hasTextValueSet || hasBinaryValueSet;
    }

    private IntegrationAdapter createAdapter(Integration integration, Class<? extends IntegrationAdapter> adapterClass) {
        LOGGER.info("Creating adapter for integration " + integration.getName());
        integration.getSettings().forEach(setting -> {
            if (setting.isEncrypted()) {
                if (StringUtils.isNotEmpty(setting.getValue())) {
                    String decryptedValue = cryptoService.decrypt(setting.getValue());
                    setting.setValue(decryptedValue);
                    setting.setEncrypted(false);
                }
            }
        });

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
