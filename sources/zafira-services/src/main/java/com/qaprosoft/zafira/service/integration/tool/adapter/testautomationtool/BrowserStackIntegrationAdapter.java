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
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;

public class BrowserStackIntegrationAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private final String url;
    private final String username;
    private final String accessKey;

    private final UnirestInstance restClient = initClient();

    public BrowserStackIntegrationAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, BrowserStackParam.BROWSER_STACK_URL);
        this.username = getAttributeValue(integration, BrowserStackParam.BROWSER_STACK_USER);
        this.accessKey = getAttributeValue(integration, BrowserStackParam.BROWSER_STACK_ACCESS_KEY);
    }

    private UnirestInstance initClient() {
        Config config = new Config();
        config.connectTimeout(5000);
        return new UnirestInstance(config);
    }

    @Getter
    @AllArgsConstructor
    private enum BrowserStackParam implements AdapterParam {
        BROWSER_STACK_URL("BROWSER_STACK_URL"),
        BROWSER_STACK_USER("BROWSER_STACK_USER"),
        BROWSER_STACK_ACCESS_KEY("BROWSER_STACK_ACCESS_KEY");

        private final String name;
    }

    @Override
    public String buildUrl() {
        String result = null;
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(accessKey)) {
            String[] urlSlices = url.split("//");
            result = String.format("%s//%s:%s@%s", urlSlices[0], username, accessKey, urlSlices[1]);
        }
        return result != null ? result : url;
    }

    @Override
    public boolean isConnected() {
        try {
            HttpResponse response = restClient.get(url).asEmpty();
            return response.getStatus() == 200;
        } catch (UnirestException e) {
            LOGGER.error("Unable to check BrowserStack connectivity", e);
            return false;
        }
    }

    @PreDestroy
    private void close() {
        restClient.shutDown();
    }
}
