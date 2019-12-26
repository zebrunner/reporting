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
import com.qaprosoft.zafira.service.util.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ZebrunnerAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private final String url;
    private final String username;
    private final String password;

    public ZebrunnerAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, Parameter.URL);
        this.username = getAttributeValue(integration, Parameter.USERNAME);
        this.password = getAttributeValue(integration, Parameter.PASSWORD);
    }

    @Override
    public String buildUrl() {
        return HttpUtils.buildBasicAuthUrl(url, username, password);
    }

    @Override
    public boolean isConnected() {
        return HttpUtils.isReachable(url, username, password, "", false) &&
                "/wd/hub".equals(HttpUtils.retrievePath(url));
    }

    @Getter
    @AllArgsConstructor
    private enum Parameter implements AdapterParam {
        URL("ZEBRUNNER_URL"),
        USERNAME("ZEBRUNNER_USER"),
        PASSWORD("ZEBRUNNER_PASSWORD");

        private final String name;
    }
}
