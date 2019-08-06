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
package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class URLResolver {

    private static final String SIGNUP_PATH_PATTERN = "%s/signup?token=%s";

    @Value("${zafira.multitenant}")
    private boolean isMultitenant;

    @Value("${zafira.web.url}")
    private String webURL;

    @Value("${zafira.webservice.url}")
    private String webserviceURL;

    /**
     * In case if multitenancy will resolve current tenancy id into the URL pattern: http://demo.qaprosoft.com/zafira.
     *
     * @return Zafira web URL
     */
    public String buildWebURL() {
        return isMultitenant ? String.format(webURL, TenancyContext.getTenantName()) : webURL;
    }

    public String buildWebserviceUrl() {
        return isMultitenant ? webserviceURL.replace("api", TenancyContext.getTenantName()) : webserviceURL;
    }

    public String getServiceURL() {
        return getUrlFromWebUrl(buildWebURL());
    }

    public String buildInvitationUrl(String token) {
        return String.format(SIGNUP_PATH_PATTERN, buildWebURL(), token);
    }

    private static String getUrlFromWebUrl(String webUrl) {
        String result = null;
        Matcher matcher = Pattern.compile("^.+(?=/)").matcher(webUrl);
        while (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }


}
