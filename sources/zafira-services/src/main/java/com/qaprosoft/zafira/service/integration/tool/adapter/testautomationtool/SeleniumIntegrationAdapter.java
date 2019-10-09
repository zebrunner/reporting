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
import kong.unirest.UnirestInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PreDestroy;

public class SeleniumIntegrationAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private final String url;
    private final String username;
    private final String password;

    private final UnirestInstance restClient = initClient();

    public SeleniumIntegrationAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, SeleniumParam.SELENIUM_URL);
        this.username = getAttributeValue(integration, SeleniumParam.SELENIUM_USERNAME);
        this.password = getAttributeValue(integration, SeleniumParam.SELENIUM_PASSWORD);
    }

    private UnirestInstance initClient() {
        Config config = new Config();
        config.connectTimeout(5000);
        return new UnirestInstance(config);
    }

    @Getter
    @AllArgsConstructor
    private enum SeleniumParam implements AdapterParam {
        SELENIUM_URL("SELENIUM_URL"),
        SELENIUM_USERNAME("SELENIUM_USER"),
        SELENIUM_PASSWORD("SELENIUM_PASSWORD");

        private final String name;
    }

    @Override
    public boolean isConnected() {
        HttpResponse response = restClient.get(url).asEmpty();
        return response.getStatus() == 200;
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
    private void close() {
        restClient.shutDown();
    }
}
