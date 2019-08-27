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
package com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.testautomationtool;

import com.qaprosoft.zafira.models.db.integration.Integration;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AdapterParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.annotation.PreDestroy;
import java.io.IOException;

public class SeleniumIntegrationAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private static final int TIMEOUT = 5000;
    private static final CloseableHttpClient HTTP_CLIENT;
    private static final RequestConfig REQUEST_CONFIG;

    static {
        REQUEST_CONFIG = RequestConfig.custom()
                                      .setConnectTimeout(TIMEOUT)
                                      .setConnectionRequestTimeout(TIMEOUT)
                                      .build();
        HTTP_CLIENT = HttpClientBuilder.create().build();
    }

    private final String url;
    private final String username;
    private final String password;

    public SeleniumIntegrationAdapter(Integration integration) {
        super("SELENIUM", integration);

        this.url = getAttributeValue(SeleniumParam.SELENIUM_URL);
        this.username = getAttributeValue(SeleniumParam.SELENIUM_USERNAME);
        this.password = getAttributeValue(SeleniumParam.SELENIUM_PASSWORD);
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
        HttpGet request = null;
        CloseableHttpResponse response = null;
        try {
            request = new HttpGet(url);
            request.setConfig(REQUEST_CONFIG);
            response = HTTP_CLIENT.execute(request);

            result = response.getStatusLine().getStatusCode() == 200;
        } catch (Exception e) {
            result = false;
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
            if(response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
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

    @PreDestroy
    public void close() {
        try {
            if (HTTP_CLIENT != null) {
                HTTP_CLIENT.close();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
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
