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

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.qaprosoft.zafira.models.db.integration.Integration;
import com.qaprosoft.zafira.services.services.application.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationSettingService;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.proxy.AbstractProxy;
import com.qaprosoft.zafira.services.util.TenancyDbInitial;

import com.qaprosoft.zafira.services.services.management.TenancyService;
import com.qaprosoft.zafira.services.util.TenancyInitial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@DependsOn("databaseStateManager")
public class IntegrationTenancyStorage implements TenancyInitial, TenancyDbInitial {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTenancyStorage.class);

    private final TenancyService tenancyService;
    private final IntegrationService integrationService;
    private final IntegrationSettingService integrationSettingService;
    private final Map<String, AbstractProxy> integrationProxies;
    private final CryptoService cryptoService;

    public IntegrationTenancyStorage(TenancyService tenancyService, IntegrationService integrationService, IntegrationSettingService integrationSettingService,
                                     Map<String, AbstractProxy> integrationProxies, CryptoService cryptoService) {
        this.tenancyService = tenancyService;
        this.integrationService = integrationService;
        this.integrationSettingService = integrationSettingService;
        this.integrationProxies = integrationProxies;
        this.cryptoService = cryptoService;
    }

    @PostConstruct
    public void post() {
        tenancyService.iterateItems(() -> {
            initDb();
            init();
        });
    }

    @Override
    public void init() {
        integrationProxies.forEach((name, proxy) -> {
            LOGGER.info(String.format("Starting to initialize %s integration proxy", name));
            proxy.init();
        });
    }

    @Override
    public void initDb() {
        try {
            cryptoService.init();
            List<Integration> integrations = integrationService.retrieveAll();
            integrations.forEach(integration -> {
                LOGGER.info(String.format("Starting to initialize %s integration", integration.getName()));
                integration.getIntegrationSettings().forEach(integrationSetting -> {
                    LOGGER.info(String.format("Starting to initialize %s setting", integrationSetting.getIntegrationParam().getName()));
                    if (!StringUtils.isEmpty(integrationSetting.getValue()) && integrationSetting.getIntegrationParam().isNeedEncryption() && !integrationSetting.isEncrypted()) {
                        integrationSetting.setValue(cryptoService.encrypt(integrationSetting.getValue()));
                        integrationSetting.setEncrypted(true);
                        integrationSettingService.update(integrationSetting);
                    }
                });
            });
        } catch (Exception e) {
            LOGGER.error("Unable to encrypt value: " + e.getMessage(), e);
        }
    }
/*
    @SuppressWarnings("unchecked")
    public synchronized static <T extends AbstractAdapter> void putContext(Setting.Tool tool, T t) {
        if (tenancyEntity.get(tool) == null) {
            Map<String, T> typeMap = new ConcurrentHashMap<>();
            typeMap.put(TenancyContext.getTenantName(), t);
            tenancyEntity.put(tool, typeMap);
        } else {
            ((Map<String, T>) tenancyEntity.get(tool)).put(TenancyContext.getTenantName(), t);
        }
    }

    public synchronized static void removeContext(Setting.Tool tool) {
        Map<String, ? extends AbstractAdapter> context = tenancyEntity.get(tool);
        String tenantName = TenancyContext.getTenantName();
        if (context != null && context.get(tenantName) != null) {
            context.remove(tenantName);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getContext(Setting.Tool tool) {
        Map<String, ? extends AbstractAdapter> clientMap = tenancyEntity.get(tool);
        return clientMap == null ? Optional.empty() : Optional.ofNullable((T) tenancyEntity.get(tool).get(TenancyContext.getTenantName()));
    }*/

}
