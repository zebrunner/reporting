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

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.context.LDAPContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.LDAP;

@Component
public class LdapService extends AbstractIntegration<LDAPContext> {

    private final static Logger LOGGER = Logger.getLogger(LdapService.class);

    private final SettingsService settingsService;
    private final CryptoService cryptoService;

    public LdapService(SettingsService settingsService, CryptoService cryptoService) {
        super(LDAP);
        this.settingsService = settingsService;
        this.cryptoService = cryptoService;
    }

    @Override
    public void init() {

        String dn = null;
        String searchFilter = null;
        String url = null;
        String managerUser = null;
        String managerPassword = null;
        boolean enabled = false;

        try {
            List<Setting> ldapSettings = settingsService.getSettingsByTool(LDAP);
            for (Setting setting : ldapSettings)
            {
                if(setting.isEncrypted())
                {
                    setting.setValue(cryptoService.decrypt(setting.getValue()));
                }
                switch (Setting.SettingType.valueOf(setting.getName()))
                {
                    case LDAP_DN:
                        dn = setting.getValue();
                        break;
                    case LDAP_SEARCH_FILTER:
                        searchFilter = setting.getValue();
                        break;
                    case LDAP_URL:
                        url = setting.getValue();
                        break;
                    case LDAP_MANAGER_USER:
                        managerUser = setting.getValue();
                        break;
                    case LDAP_MANAGER_PASSWORD:
                        managerPassword = setting.getValue();
                        break;
                    case LDAP_ENABLED:
                        enabled = Boolean.valueOf(setting.getValue());
                        break;
                    default:
                        break;
                }
            }
            init(dn, searchFilter, url, managerUser, managerPassword, enabled);
        } catch(Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    public void init(String dn, String searchFilter, String url, String managerUser, String managerPassword, boolean enabled){
        try
        {
            if(!StringUtils.isBlank(dn) && !StringUtils.isBlank(searchFilter) && !StringUtils.isBlank(url) && !StringUtils.isBlank(managerUser) && !StringUtils.isBlank(managerPassword)) {
                putContext(new LDAPContext(dn, searchFilter, url, managerUser, managerPassword, enabled));
            }
        } catch (Exception e)
        {
            LOGGER.error("Unable to initialize Ldap integration: " + e.getMessage());
        }
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
