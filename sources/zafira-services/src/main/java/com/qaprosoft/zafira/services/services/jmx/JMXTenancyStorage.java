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
package com.qaprosoft.zafira.services.services.jmx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.jmx.models.AbstractType;

@Component
public class JMXTenancyStorage {

    private static Map<Setting.Tool, Map<String, ? extends AbstractType>> tenancyEntity = new ConcurrentHashMap<>();

    @SuppressWarnings("all")
    public synchronized static <T extends AbstractType> void putType(Setting.Tool tool, T t) {
        if (tenancyEntity.get(tool) == null) {
            Map<String, T> typeMap = new ConcurrentHashMap<>();
            typeMap.put(TenancyContext.getTenantName(), t);
            tenancyEntity.put(tool, typeMap);
        } else {
            ((Map<String, T>) tenancyEntity.get(tool)).put(TenancyContext.getTenantName(), t);
        }
    }

    @SuppressWarnings("all")
    public static <T> T getType(Setting.Tool tool) {
        Map<String, ? extends AbstractType> clientMap = tenancyEntity.get(tool);
        return clientMap == null ? null : (T) tenancyEntity.get(tool).get(TenancyContext.getTenantName());
    }
}
