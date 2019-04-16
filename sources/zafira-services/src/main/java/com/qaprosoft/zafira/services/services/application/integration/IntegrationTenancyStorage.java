/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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
package com.qaprosoft.zafira.services.services.application.integration;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.qaprosoft.zafira.services.services.application.integration.impl.CryptoService;
import com.qaprosoft.zafira.services.util.TenancyDbInitial;
import org.apache.log4j.Logger;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.context.AbstractContext;
import com.qaprosoft.zafira.services.services.management.TenancyService;
import com.qaprosoft.zafira.services.util.TenancyInitial;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("integrationService")
public class IntegrationTenancyStorage implements TenancyInitial, TenancyDbInitial {

    private static final Logger LOGGER = Logger.getLogger(IntegrationTenancyStorage.class);

    private static final Map<Setting.Tool, Map<String, ? extends AbstractContext>> tenancyEntity = new ConcurrentHashMap<>();
    
    private final TenancyService tenancyService;
    private final SettingsService settingsService;
    private final IntegrationService integrationService;
    private final CryptoService cryptoService;

    public IntegrationTenancyStorage(TenancyService tenancyService, SettingsService settingsService, IntegrationService integrationService, CryptoService cryptoService) {
        this.tenancyService = tenancyService;
        this.settingsService = settingsService;
        this.integrationService = integrationService;
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
        Arrays.stream(Setting.Tool.getValues()).forEach(tool -> integrationService.getServiceByTool(tool).init());
    }

    @Override
    public void initDb() {
        try {
            cryptoService.getKey();
            for(Setting setting : settingsService.getAllSettings()) {
                if(setting.isValueForEncrypting() && !setting.isEncrypted()) {
                    setting.setValue(cryptoService.encrypt(setting.getValue()));
                    setting.setEncrypted(true);
                    settingsService.updateSetting(setting);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to encrypt value: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized static <T extends AbstractContext> void putContext(Setting.Tool tool, T t) {
        if (tenancyEntity.get(tool) == null) {
            Map<String, T> typeMap = new ConcurrentHashMap<>();
            typeMap.put(TenancyContext.getTenantName(), t);
            tenancyEntity.put(tool, typeMap);
        } else {
            ((Map<String, T>) tenancyEntity.get(tool)).put(TenancyContext.getTenantName(), t);
        }
    }

    public synchronized static void removeContext(Setting.Tool tool) {
        Map<String, ? extends AbstractContext> context = tenancyEntity.get(tool);
        String tenantName = TenancyContext.getTenantName();
        if(context != null && context.get(tenantName) != null) {
            context.remove(tenantName);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getContext(Setting.Tool tool) {
        Map<String, ? extends AbstractContext> clientMap = tenancyEntity.get(tool);
        return clientMap == null ? Optional.empty() : Optional.ofNullable((T) tenancyEntity.get(tool).get(TenancyContext.getTenantName()));
    }

}
