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
 *******************************************************************************/
package com.qaprosoft.zafira.ws.security.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.google.common.net.InternetDomainName;
import com.mchange.v2.lang.StringUtils;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;

/**
 * TenancyFilter - retrieves tenant by subdomain.
 * 
 * @author akhursevich
 */
@Component
public class TenancyFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenancyFilter.class);

    private static final String[] EXCLUSIONS = {"api/status"};

    @Value("${zafira.multitenant}")
    private boolean isMultitenant;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        if (Arrays.stream(EXCLUSIONS).noneMatch(path -> servletRequest.getRequestURI().contains(path))) {

            if (isMultitenant) {
                String host = servletRequest.getServerName(); // API clients without Origin
                String origin = servletRequest.getHeader("Origin"); // Web clients has Origin header
                if (StringUtils.nonEmptyString(origin)) {
                    host = origin.split("//")[1].split(":")[0];
                }
                try {
                    InternetDomainName domain = InternetDomainName.from(host.replaceFirst("www.", ""));
                    if (!domain.isTopPrivateDomain()) {
                        String topDomain = domain.topPrivateDomain().toString();
                        String subDomain = domain.toString().replaceAll("." + topDomain, "");
                        TenancyContext.setTenantName(subDomain);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
