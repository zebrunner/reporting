package com.qaprosoft.zafira.services.services.jmx.models;

import com.qaprosoft.zafira.services.services.jmx.ldap.LDAPUserDetailsContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

public class LDAPType extends AbstractType
{

    private LdapContextSource ldapContextSource;
    private BindAuthenticator bindAuthenticator;
    private FilterBasedLdapUserSearch filterBasedLdapUserSearch;
    private LdapAuthenticationProvider ldapAuthenticationProvider;

    public LDAPType(String dn, String searchFilter, String url, String managerUser, String managerPassword, LDAPUserDetailsContextMapper ldapUserDetailsContextMapper)
    {
        this.ldapContextSource = new LdapContextSource();
        this.ldapContextSource.setUrl(url);
        this.ldapContextSource.setUserDn(managerUser);
        this.ldapContextSource.setPassword(managerPassword);
        this.ldapContextSource.afterPropertiesSet();
        this.filterBasedLdapUserSearch = new FilterBasedLdapUserSearch(dn, searchFilter, ldapContextSource);
        this.filterBasedLdapUserSearch.setSearchSubtree(true);
        this.bindAuthenticator = new BindAuthenticator(this.ldapContextSource);
        this.bindAuthenticator.setUserSearch(this.filterBasedLdapUserSearch);
        this.ldapAuthenticationProvider = new LdapAuthenticationProvider(this.bindAuthenticator);
        this.ldapAuthenticationProvider.setUserDetailsContextMapper(ldapUserDetailsContextMapper);
    }

    public LdapContextSource getLdapContextSource()
    {
        return ldapContextSource;
    }

    public void setLdapContextSource(LdapContextSource ldapContextSource)
    {
        this.ldapContextSource = ldapContextSource;
    }

    public BindAuthenticator getBindAuthenticator()
    {
        return bindAuthenticator;
    }

    public void setBindAuthenticator(BindAuthenticator bindAuthenticator)
    {
        this.bindAuthenticator = bindAuthenticator;
    }

    public FilterBasedLdapUserSearch getFilterBasedLdapUserSearch()
    {
        return filterBasedLdapUserSearch;
    }

    public void setFilterBasedLdapUserSearch(FilterBasedLdapUserSearch filterBasedLdapUserSearch)
    {
        this.filterBasedLdapUserSearch = filterBasedLdapUserSearch;
    }

    public LdapAuthenticationProvider getLdapAuthenticationProvider()
    {
        return ldapAuthenticationProvider;
    }

    public void setLdapAuthenticationProvider(LdapAuthenticationProvider ldapAuthenticationProvider)
    {
        this.ldapAuthenticationProvider = ldapAuthenticationProvider;
    }
}
