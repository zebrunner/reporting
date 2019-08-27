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
package com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.accessmanagement;

import com.qaprosoft.zafira.models.db.integration.Integration;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AdapterParam;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

public class LdapIntegrationAdapter extends AbstractIntegrationAdapter implements AccessManagementAdapter {

    private final String url;
    private final String managerUser;
    private final String managerPassword;
    private final String dn;
    private final String searchFilter;

    private final LdapContextSource ldapContextSource;
    private final BindAuthenticator bindAuthenticator;
    private final FilterBasedLdapUserSearch filterBasedLdapUserSearch;

    public LdapIntegrationAdapter(Integration integration) {
        super("LDAP", integration);

        this.url = getAttributeValue(LdapParam.LDAP_URL);
        this.managerUser = getAttributeValue(LdapParam.LDAP_MANAGER_USER);
        this.managerPassword = getAttributeValue(LdapParam.LDAP_MANAGER_PASSWORD);
        this.dn = getAttributeValue(LdapParam.LDAP_DN);
        this.searchFilter = getAttributeValue(LdapParam.LDAP_SEARCH_FILTER);

        this.ldapContextSource = new LdapContextSource();
        this.ldapContextSource.setUrl(url);
        this.ldapContextSource.setUserDn(managerUser);
        this.ldapContextSource.setPassword(managerPassword);
        this.ldapContextSource.afterPropertiesSet();
        this.filterBasedLdapUserSearch = new FilterBasedLdapUserSearch(dn, searchFilter, ldapContextSource);
        this.filterBasedLdapUserSearch.setSearchSubtree(true);
        this.bindAuthenticator = new BindAuthenticator(this.ldapContextSource);
        this.bindAuthenticator.setUserSearch(this.filterBasedLdapUserSearch);
    }

    private enum LdapParam implements AdapterParam {
        LDAP_URL("LDAP_URL"),
        LDAP_MANAGER_USER("LDAP_MANAGER_USER"),
        LDAP_MANAGER_PASSWORD("LDAP_MANAGER_PASSWORD"),
        LDAP_DN("LDAP_DN"),
        LDAP_SEARCH_FILTER("LDAP_SEARCH_FILTER");

        private final String name;

        LdapParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        try {
            ldapContextSource.getContext(ldapContextSource.getUserDn(), ldapContextSource.getPassword());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isUserExists(String username) {
        return filterBasedLdapUserSearch.searchForUser(username) != null;
    }

    public String getUrl() {
        return url;
    }

    public String getManagerUser() {
        return managerUser;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public String getDn() {
        return dn;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public LdapContextSource getLdapContextSource() {
        return ldapContextSource;
    }

    @Override
    public BindAuthenticator getBindAuthenticator() {
        return bindAuthenticator;
    }

    public FilterBasedLdapUserSearch getFilterBasedLdapUserSearch() {
        return filterBasedLdapUserSearch;
    }
}
