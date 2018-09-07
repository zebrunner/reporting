/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.dbaccess.utils;

import com.qaprosoft.zafira.models.db.Tenancy;

/**
 * TenancyContext - stores client tenant ID.
 * 
 * @author akhursevich
 */
public class TenancyContext {

	private static ThreadLocal<String> tenant = new InheritableThreadLocal<>();

    public static void setTenantName(String tenantName) {
        tenant.set(tenantName != null ? tenantName.toLowerCase() : null);
    }

    public static String getTenantName() {
    		String tenantName = tenant.get();
        return tenantName != null ? tenantName : Tenancy.getDefaultTenant();
    }
}
