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
package com.qaprosoft.zafira.service.integration.tool.impl;

import com.qaprosoft.zafira.models.dto.IssueDTO;
import com.qaprosoft.zafira.service.integration.IntegrationService;
import com.qaprosoft.zafira.service.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.service.integration.tool.adapter.testcasemanagement.TestCaseManagementAdapter;
import com.qaprosoft.zafira.service.integration.tool.proxy.TestCaseManagementProxy;
import org.springframework.stereotype.Component;

@Component
public class TestCaseManagementService extends AbstractIntegrationService<TestCaseManagementAdapter> {

    public TestCaseManagementService(IntegrationService integrationService, TestCaseManagementProxy testCaseManagementProxy) {
        super(integrationService, testCaseManagementProxy, "JIRA");
    }

    public IssueDTO getIssue(String ticket) {
        TestCaseManagementAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.getIssue(ticket);
    }

    // TODO: 10/2/19 url should not be obrained from adapter; use integration settings instead
    public String getUrl() {
        TestCaseManagementAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.getUrl();
    }

    public boolean isIssueClosed(String ticket) {
        TestCaseManagementAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.isIssueClosed(ticket);
    }

}
