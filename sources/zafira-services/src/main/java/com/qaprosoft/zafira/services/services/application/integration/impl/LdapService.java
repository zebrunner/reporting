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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.context.LDAPContext;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

import static com.qaprosoft.zafira.models.db.Setting.Tool.LDAP;

@Component
public class LdapService extends AbstractIntegration<LDAPContext> {

    public LdapService(SettingsService settingsService, CryptoService cryptoService) {
        super(settingsService, cryptoService, LDAP, LDAPContext.class);
    }

    /**
     * Search user by username in ldap context
     * Throws an {@link com.qaprosoft.zafira.services.exceptions.IntegrationException} if integration is not configured
     * @param username - username to search
     * @return - search info
     */
    public DirContextOperations searchUser(String username) {
        return context().getFilterBasedLdapUserSearch().searchForUser(username);
    }

    @Override
    public boolean isConnected() {
        try
        {
            LdapContextSource contextSource = context().getLdapContextSource();
            contextSource.getContext(contextSource.getUserDn(), contextSource.getPassword());
            return true;
        } catch(Exception e)
        {
            return false;
        }
    }

}
