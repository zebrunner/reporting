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
package com.qaprosoft.zafira.services.services.application.integration.tool.impl;

import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.accessmanagement.AccessManagementAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.proxy.AccessManagementProxy;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.stereotype.Component;

@Component
public class AccessManagementService extends AbstractIntegrationService<AccessManagementAdapter> {

    public AccessManagementService(IntegrationService integrationService, AccessManagementProxy accessManagementProxy) {
        super(integrationService, accessManagementProxy, "LDAP");
    }

    public boolean isUserExists(String username) {
        AccessManagementAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.isUserExists(username);
    }

    public BindAuthenticator getBindAuthenticator() {
        AccessManagementAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.getBindAuthenticator();
    }
}
