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
package com.qaprosoft.zafira.services.services.application.integration.tool.adapter.testautomationtool;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.AdapterParam;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SeleniumIntegrationAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private static final int TIMEOUT = 5000;
    private static final HttpClient HTTP_CLIENT;

    static {
        HTTP_CLIENT = HttpClient.newBuilder()
                                .connectTimeout(Duration.ofMillis(TIMEOUT))
                                .build();
    }

    private final String url;
    private final String username;
    private final String password;

    public SeleniumIntegrationAdapter(Integration integration) {
        super(integration);

        this.url = getAttributeValue(integration, SeleniumParam.SELENIUM_URL);
        this.username = getAttributeValue(integration, SeleniumParam.SELENIUM_USERNAME);
        this.password = getAttributeValue(integration, SeleniumParam.SELENIUM_PASSWORD);
    }

    private enum SeleniumParam implements AdapterParam {
        SELENIUM_URL("SELENIUM_URL"),
        SELENIUM_USERNAME("SELENIUM_USER"),
        SELENIUM_PASSWORD("SELENIUM_PASSWORD");

        private final String name;

        SeleniumParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        boolean result;
        HttpRequest request;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                                 .uri(URI.create(url))
                                 .timeout(Duration.ofMillis(TIMEOUT))
                                 .GET()
                                 .build();
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.statusCode() == 200;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    public String buildUrl() {
        String result = null;
        if(StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            result = String.format("%s//%s:%s@%s", url.split("//")[0], username, password, url.split("//")[1]);
        }
        return result != null ? result : url;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
