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

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.application.Setting;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.management.MngTenancyService;
import com.qaprosoft.zafira.services.services.application.jmx.models.AbstractType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JMXTenancyStorage {

    @Autowired
    private MngTenancyService mngTenancyService;

    @Autowired
    private SettingsService settingsService;

    private static Map<Setting.Tool, Map<String, ? extends AbstractType>> tenancyEntity = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        mngTenancyService.iterateItems(tenancy -> {
            Arrays.stream(Setting.Tool.values()).forEach(tool -> {
                settingsService.getServiceByTool(tool).init();
            });
        });
    }

    @SuppressWarnings("unchecked")
    public synchronized static <T extends AbstractType> void putType(Setting.Tool tool, T t) {
        if (tenancyEntity.get(tool) == null) {
            Map<String, T> typeMap = new ConcurrentHashMap<>();
            typeMap.put(TenancyContext.getTenantName(), t);
            tenancyEntity.put(tool, typeMap);
        } else {
            ((Map<String, T>) tenancyEntity.get(tool)).put(TenancyContext.getTenantName(), t);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getType(Setting.Tool tool) {
        Map<String, ? extends AbstractType> clientMap = tenancyEntity.get(tool);
        return clientMap == null ? null : (T) tenancyEntity.get(tool).get(TenancyContext.getTenantName());
    }


}
