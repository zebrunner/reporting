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

public class ZebrunnerIntegrationAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private final String url;
    private final String username;
    private final String password;

    private final UnirestInstance restClient = initClient();

    public ZebrunnerIntegrationAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, ZebrunnerParam.ZEBRUNNER_URL);
        this.username = getAttributeValue(integration, ZebrunnerParam.ZEBRUNNER_USER);
        this.password = getAttributeValue(integration, ZebrunnerParam.ZEBRUNNER_PASSWORD);
    }

    private UnirestInstance initClient() {
        Config config = new Config();
        config.connectTimeout(5000);
        return new UnirestInstance(config);
    }

    @Getter
    @AllArgsConstructor
    private enum ZebrunnerParam implements AdapterParam {
        ZEBRUNNER_URL("ZEBRUNNER_URL"),
        ZEBRUNNER_USER("ZEBRUNNER_USER"),
        ZEBRUNNER_PASSWORD("ZEBRUNNER_PASSWORD");

        private final String name;
    }

    @Override
    public String buildUrl() {
        String result = null;
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            String[] urlSlices = url.split("//");
            result = String.format("%s//%s:%s@%s", urlSlices[0], username, password, urlSlices[1]);
        }
        return result != null ? result : url;
    }

    @Override
    public boolean isConnected() {
        try {
            HttpResponse response = restClient.get(url).asEmpty();
            return response.getStatus() == 200;
        } catch (UnirestException e) {
            return false;
        }
    }

    @PreDestroy
    private void close() {
        restClient.shutDown();
    }
}
