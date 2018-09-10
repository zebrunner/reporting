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
package com.qaprosoft.zafira.services.services.application.jmx.ldap;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.jmx.JMXTenancyStorage;
import com.qaprosoft.zafira.services.services.application.jmx.context.LDAPContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;
import org.springframework.security.ldap.ppolicy.PasswordPolicyException;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.annotation.PostConstruct;
import java.util.Collection;

/** Uses for load user from LDAP and recognize it.
 * Need override LdapAuthenticationProvider cause superclass has private getAuthenticator only
 * @author brutskov
 */
public class LDAPAuthenticationProvider extends AbstractLdapAuthenticationProvider {

    @Autowired
    private LDAPUserDetailsContextMapper ldapUserDetailsContextMapper;

    private LdapAuthoritiesPopulator authoritiesPopulator = new NullLdapAuthoritiesPopulator();

    @PostConstruct
    private void init() {
        super.setUserDetailsContextMapper(this.ldapUserDetailsContextMapper);
    }

    /**
     * Uses the same logic as superclass
     * @param authentication - authentication callback
     * @return context operation
     */
    @Override
    protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken authentication) {
        LdapAuthenticator ldapAuthenticator = getAuthenticator();
        if(ldapAuthenticator == null) {
            throw new InternalAuthenticationServiceException("Provide LDAP integration before");
        }
        try {
            return ldapAuthenticator.authenticate(authentication);
        }
        catch (PasswordPolicyException ppe) {
            throw new LockedException(this.messages.getMessage(ppe.getStatus().getErrorCode(), ppe.getStatus().getDefaultMessage()));
        }
        catch (UsernameNotFoundException notFound) {
            throw new BadCredentialsException(this.messages.getMessage("LdapAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        catch (NamingException ldapAccessFailure) {
            throw new InternalAuthenticationServiceException(ldapAccessFailure.getMessage(), ldapAccessFailure);
        }
    }

    private static LdapAuthenticator getAuthenticator() {
        LDAPContext ldapContext = getContext();
        return ldapContext != null ? ldapContext.getBindAuthenticator() : null;
    }

    private static LDAPContext getContext() {
        return JMXTenancyStorage.<LDAPContext>getContext(Setting.Tool.LDAP);
    }

    @Override
    protected UserDetailsContextMapper getUserDetailsContextMapper() {
        return this.ldapUserDetailsContextMapper;
    }

    protected LdapAuthoritiesPopulator getAuthoritiesPopulator() {
        return this.authoritiesPopulator;
    }

    @Override
    protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData, String username, String password) {
        return getAuthoritiesPopulator().getGrantedAuthorities(userData, username);
    }
}
