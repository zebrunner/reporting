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
import com.qaprosoft.zafira.service.util.UrlUtils;
import kong.unirest.Config;
import kong.unirest.UnirestException;
import kong.unirest.UnirestInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.MalformedURLException;

public class BrowserStackAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private static final String HEALTH_CHECK_PATH = "https://api.browserstack.com/automate/plan.json/";

    private final String url;
    private final String username;
    private final String accessKey;

    public BrowserStackAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, Parameter.URL);
        this.username = getAttributeValue(integration, Parameter.USERNAME);
        this.accessKey = getAttributeValue(integration, Parameter.ACCESS_KEY);
    }

    private UnirestInstance initClient() {
        Config config = new Config();
        config.connectTimeout(5000);
        return new UnirestInstance(config);
    }

    @Override
    public String buildUrl() {
        return UrlUtils.buildBasicAuthUrl(url, username, accessKey);
    }

    @Override
    public boolean isConnected() {
        try {
            return UrlUtils.verifyStatusByPath(HEALTH_CHECK_PATH, username, accessKey, "", false) &&
                    UrlUtils.verifyStatusByPath(url, username, accessKey, "/status", false);
        } catch (UnirestException | MalformedURLException e) {
            LOGGER.error("Unable to check BrowserStack connectivity", e);
            return false;
        }
    }

    @Getter
    @AllArgsConstructor
    private enum Parameter implements AdapterParam {
        URL("BROWSERSTACK_URL"),
        USERNAME("BROWSERSTACK_USER"),
        ACCESS_KEY("BROWSERSTACK_ACCESS_KEY");

        private final String name;
    }
}
