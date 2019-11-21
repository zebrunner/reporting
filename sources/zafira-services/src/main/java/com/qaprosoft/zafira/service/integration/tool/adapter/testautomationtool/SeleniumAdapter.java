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
package com.qaprosoft.zafira.service.integration.tool.adapter.testautomationtool;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.service.integration.tool.adapter.AdapterParam;
import kong.unirest.Config;
import kong.unirest.HttpResponse;
import kong.unirest.UnirestException;
import kong.unirest.UnirestInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PreDestroy;

public class SeleniumAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private final String url;
    private final String username;
    private final String password;

    private final UnirestInstance restClient = initClient();

    public SeleniumAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, Parameter.URL);
        this.username = getAttributeValue(integration, Parameter.USERNAME);
        this.password = getAttributeValue(integration, Parameter.PASSWORD);
    }

    private UnirestInstance initClient() {
        Config config = new Config();
        config.connectTimeout(5000);
        return new UnirestInstance(config);
    }

    @Override
    public boolean isConnected() {
        try {
            HttpResponse response = restClient.get(url).asEmpty();
            return response.getStatus() == 200;
        } catch (UnirestException e) {
            LOGGER.error("Unable to check Selenium connectivity", e);
            return false;
        }
    }

    @Override
    public String buildUrl() {
        String result = null;
        if(StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            String[] urlSlices = url.split("//");
            result = String.format("%s//%s:%s@%s", urlSlices[0], username, password, urlSlices[1]);
        }
        return result != null ? result : url;
    }

    @PreDestroy
    private void close() {
        restClient.shutDown();
    }

    @Getter
    @AllArgsConstructor
    private enum Parameter implements AdapterParam {
        URL("SELENIUM_URL"),
        USERNAME("SELENIUM_USER"),
        PASSWORD("SELENIUM_PASSWORD");

        private final String name;
    }

}
