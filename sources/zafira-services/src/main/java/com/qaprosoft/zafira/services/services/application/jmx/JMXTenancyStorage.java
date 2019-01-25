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
package com.qaprosoft.zafira.services.services.application.jmx;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.jmx.context.AbstractContext;
import com.qaprosoft.zafira.services.services.management.TenancyService;
import com.qaprosoft.zafira.services.util.TenancyInitial;

@Component
public class JMXTenancyStorage implements TenancyInitial {

    private static final Logger LOGGER = Logger.getLogger( JMXTenancyStorage.class);
    
    @Autowired
    private TenancyService tenancyService;

    @Autowired
    private SettingsService settingsService;
    
    @Autowired
    private CryptoService cryptoService;

    private static final Map<Setting.Tool, Map<String, ? extends AbstractContext>> tenancyEntity = new ConcurrentHashMap<>();

    @PostConstruct
    public void post() {
        tenancyService.iterateItems(this::init);
    }

    @Override
    public void init() {
        try {
            for(Setting setting : settingsService.getAllSettings()) {
                if(setting.isValueForEncrypting() && !setting.isEncrypted()) {
                    setting.setValue(cryptoService.encrypt(setting.getValue()));
                    settingsService.updateSetting(setting);
                }
            }
        } catch (Exception e) {
           LOGGER.error("Unable to encrypt value: " + e.getMessage(), e);
        }
        Arrays.stream(Setting.Tool.values()).forEach(tool -> {
            settingsService.getServiceByTool(tool).init();
        });
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

    @SuppressWarnings("unchecked")
    public static <T> T getContext(Setting.Tool tool) {
        Map<String, ? extends AbstractContext> clientMap = tenancyEntity.get(tool);
        return clientMap == null ? null : (T) tenancyEntity.get(tool).get(TenancyContext.getTenantName());
    }


}
