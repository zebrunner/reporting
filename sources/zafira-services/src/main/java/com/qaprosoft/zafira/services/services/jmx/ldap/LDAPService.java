package com.qaprosoft.zafira.services.services.jmx.ldap;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.jmx.CryptoService;
import com.qaprosoft.zafira.services.services.jmx.IJMXService;
import com.qaprosoft.zafira.services.services.jmx.models.LDAPType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.jmx.export.annotation.*;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.LDAP;

@ManagedResource(objectName="bean:name=ldapService", description="Ldap init Managed Bean",
        currencyTimeLimit=15, persistPolicy="OnUpdate", persistPeriod=200)
public class LDAPService implements IJMXService<LDAPType> {

    private final static Logger LOGGER = Logger.getLogger(LDAPService.class);

    @Autowired
    private LDAPUserDetailsContextMapper ldapUserDetailsContextMapper;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CryptoService cryptoService;

    @Override
    @PostConstruct
    public void init() {

        String dn = null;
        String searchFilter = null;
        String url = null;
        String managerUser = null;
        String managerPassword = null;

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
                    default:
                        break;
                }
            }
            init(dn, searchFilter, url, managerUser, managerPassword);
        } catch(Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    @ManagedOperation(description="Change Ldap initialization")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "dn", description = "Ldap dn"),
            @ManagedOperationParameter(name = "searchFilter", description = "Ldap search filter"),
            @ManagedOperationParameter(name = "url", description = "Ldap url"),
            @ManagedOperationParameter(name = "managerUser", description = "Ldap manager user"),
            @ManagedOperationParameter(name = "managerPassword", description = "Ldap manager password")})
    public void init(String dn, String searchFilter, String url, String managerUser, String managerPassword){
        try
        {
            putType(LDAP, new LDAPType(dn, searchFilter, url, managerUser, managerPassword, this.ldapUserDetailsContextMapper));
        } catch (Exception e)
        {
            LOGGER.error("Unable to initialize Ldap integration: " + e.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        boolean result = false;
        try
        {
            getLdapContextSource().getContext(getLdapContextSource().getUserDn(), getLdapContextSource().getPassword());
            result = true;
        } catch(Exception e)
        {
            LOGGER.error(e);
        }
        return result;
    }

    @ManagedAttribute(description="Get ldap context source")
    public LdapContextSource getLdapContextSource()
    {
        return getType().getLdapContextSource();
    }

    @ManagedAttribute(description="Get ldap authentication provider")
    @Bean
    @Scope(value = "prototype")
    public LdapAuthenticationProvider getLdapAuthenticationProvider()
    {
        return getType() != null ? getType().getLdapAuthenticationProvider() : null;
    }

    public LDAPType getType()
    {
        return getType(LDAP);
    }
}
