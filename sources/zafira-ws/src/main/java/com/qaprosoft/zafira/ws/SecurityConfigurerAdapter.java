package com.qaprosoft.zafira.ws;

import com.qaprosoft.zafira.ws.security.UserPassAuthService;
import com.qaprosoft.zafira.ws.security.filter.CORSFilter;
import com.qaprosoft.zafira.ws.security.filter.JwtTokenAuthenticationFilter;
import com.qaprosoft.zafira.ws.security.filter.RestAccessDeniedHandler;
import com.qaprosoft.zafira.ws.security.filter.SecurityAuthenticationEntryPoint;
import com.qaprosoft.zafira.ws.security.filter.TenancyFilter;
import com.qaprosoft.zafira.ws.security.ldap.LDAPAuthenticationProvider;
import org.jasypt.springsecurity3.authentication.encoding.PasswordEncoder;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import java.util.Collections;

@EnableWebSecurity
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private static final String[] PUBLIC_API_PATTERNS = new String[] {
            "/api/auth/login",
            "/api/config/**",
            "/api/auth/refresh",
            "/api/status/**",
            "/api/dashboards/email",
            "/api/settings/companyLogo",
            "/api/websockets/**",
            "/api/auth/tenant/**",
            "/api/invitations/info",
            "/api/auth/signup",
            "/api/auth/password/**"

    };

    private static final String[] AUTHENTICATED_API_PATTERNS = new String[] {
            "/api/auth/access",
            "/api/users/**",
            "/api/filters/**",
            "/api/profiles/**",
            "/api/tests/**",
            "/api/dashboards/**",
            "/api/widgets/**",
            "/api/projects/**",
            "/api/groups/**",
            "/api/permissions/**",
            "/api/devices/**",
            "/api/settings/**",
            "/api/jobs/**",
            "/api/certification/**",
            "/api/events/**",
            "/api/projects/**",
            "/api/slack/**",
            "/api/views/**",
            "/api/invitations/**",
            "/api/scm/**",
            "/api/launchers/**",
    };

    private final UserPassAuthService userPassAuthService;
    private final LDAPAuthenticationProvider ldapProvider;
    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;
    private final CORSFilter corsFilter;
    private final SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final TenancyFilter tenancyFilter;

    public SecurityConfigurerAdapter(UserPassAuthService userPassAuthService,
                                     LDAPAuthenticationProvider ldapProvider,
                                     JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter,
                                     CORSFilter corsFilter,
                                     SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint,
                                     RestAccessDeniedHandler restAccessDeniedHandler,
                                     TenancyFilter tenancyFilter
    ) {
        this.userPassAuthService = userPassAuthService;
        this.ldapProvider = ldapProvider;
        this.jwtTokenAuthenticationFilter = jwtTokenAuthenticationFilter;
        this.corsFilter = corsFilter;
        this.securityAuthenticationEntryPoint = securityAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
        this.tenancyFilter = tenancyFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(securityAuthenticationEntryPoint)
                .and()
                .exceptionHandling().accessDeniedHandler(restAccessDeniedHandler)
                .and()
                .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
                .addFilterBefore(tenancyFilter, CORSFilter.class)
                .addFilterAfter(jwtTokenAuthenticationFilter, ExceptionTranslationFilter.class)
                .authorizeRequests()
                .antMatchers(PUBLIC_API_PATTERNS).permitAll()
                .antMatchers(AUTHENTICATED_API_PATTERNS).authenticated();
    }

    @Bean
    @DependsOn("authenticationLdapManager")
    public AuthenticationManager authenticationInternalManager() throws Exception {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userPassAuthService);
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        passwordEncoder.setPasswordEncryptor(new BasicPasswordEncryptor());
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }

    @Bean
    public AuthenticationManager authenticationLdapManager() {
        return new ProviderManager(Collections.singletonList(ldapProvider));
    }

}
