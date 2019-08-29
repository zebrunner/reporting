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
package com.qaprosoft.zafira.services.services.application.integration.tool.impl;

import com.qaprosoft.zafira.models.dto.TestCaseManagementIssueType;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.testcasemanagement.TestCaseManagementAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.proxy.TestCaseManagementProxy;
import org.springframework.stereotype.Component;

@Component
public class TestCaseManagementService extends AbstractIntegrationService<TestCaseManagementAdapter> {

    public TestCaseManagementService(IntegrationService integrationService, TestCaseManagementProxy testCaseManagementProxy) {
        super(integrationService, testCaseManagementProxy, "JIRA");
    }

    public TestCaseManagementIssueType getIssue(String ticket) {
        TestCaseManagementAdapter adapter = getAdapterForIntegration(null);
        return adapter.getIssue(ticket);
    }

    public String getUrl() {
        TestCaseManagementAdapter adapter = getAdapterForIntegration(null);
        return adapter.getUrl();
    }

    public boolean isIssueClosed(String ticket) {
        TestCaseManagementAdapter adapter = getAdapterForIntegration(null);
        return adapter.isIssueClosed(ticket);
    }

}
