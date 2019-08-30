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
package com.qaprosoft.zafira.services.services.application.integration.tool.adapter.testcasemanagement;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.dto.TestCaseManagementIssueType;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.AdapterParam;

public class TestRailIntegrationAdapter extends AbstractIntegrationAdapter implements TestCaseManagementAdapter {

    private final String url;

    public TestRailIntegrationAdapter(Integration integration) {
        super(integration);

        this.url = getAttributeValue(integration, TestRailParam.TESTRAIL_URL);
    }

    private enum TestRailParam implements AdapterParam {
        TESTRAIL_URL("TESTRAIL_URL");

        private final String name;

        TestRailParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        return url != null && !url.isBlank();
    }

    @Override
    public TestCaseManagementIssueType getIssue(String ticket) {
        return null;
    }

    @Override
    public boolean isIssueClosed(String ticket) {
        return false;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
