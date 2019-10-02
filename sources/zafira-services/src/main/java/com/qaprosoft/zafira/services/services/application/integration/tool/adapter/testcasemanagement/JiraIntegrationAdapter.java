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

import com.qaprosoft.zafira.models.dto.TestCaseManagementIssueType;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.services.exceptions.ExternalSystemException;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.AdapterParam;
import kong.unirest.Config;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.UnirestInstance;
import kong.unirest.json.JSONObject;

public class JiraIntegrationAdapter extends AbstractIntegrationAdapter implements TestCaseManagementAdapter {

    private static final String REST_URL = "%s/rest/api/latest/%s";

    private final String url;
    private final String username;
    private final String password;
    private final String closedStatus;
    private final UnirestInstance httpClient;

    public JiraIntegrationAdapter(Integration integration) {
        super(integration);

        this.url = getAttributeValue(integration, JiraParam.JIRA_URL);
        this.username = getAttributeValue(integration, JiraParam.JIRA_USERNAME);
        this.password = getAttributeValue(integration, JiraParam.JIRA_PASSWORD);
        this.closedStatus = getAttributeValue(integration, JiraParam.JIRA_CLOSED_STATUS);
        this.httpClient = initClient(username, password);
    }

    private UnirestInstance initClient(String username, String password) {
        Config config = new Config();
        config.setDefaultBasicAuth(username, password);
        config.addDefaultHeader("Accept", "application/json");
        return new UnirestInstance(config);
    }

    private enum JiraParam implements AdapterParam {
        JIRA_URL("JIRA_URL"),
        JIRA_USERNAME("JIRA_USER"),
        JIRA_PASSWORD("JIRA_PASSWORD"),
        JIRA_CLOSED_STATUS("JIRA_CLOSED_STATUS");

        private final String name;

        JiraParam(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        String requestUrl = String.format(REST_URL, url, "serverInfo");
        HttpResponse response = httpClient.get(requestUrl)
                                          .queryString("doHealthCheck", "true")
                                          .asEmpty();
        return response.getStatus() == 200;
    }

    @Override
    public TestCaseManagementIssueType getIssue(String ticket) {
        try {
            String requestUrl = String.format(REST_URL, url, "issue/" + ticket);
            HttpResponse<JsonNode> response = httpClient.get(requestUrl).asJson();
            JSONObject issue = response.getBody().getObject();
            return getTestCaseManagementIssueType(issue);
        } catch (Exception e) {
            throw new ExternalSystemException("Unable to find Jira issue: " + ticket, e);
        }
    }

    private TestCaseManagementIssueType getTestCaseManagementIssueType(JSONObject issue) {
        String assigneeName = issue.getJSONObject("assignee").getString("name");
        String reporterName = issue.getJSONObject("reporter").getString("name");
        String summary = issue.getString("summary");
        String status = issue.getJSONObject("status").getString("name");
        return new TestCaseManagementIssueType(assigneeName, reporterName, summary, status);
    }

    @Override
    public boolean isIssueClosed(String ticket) {
        boolean isIssueClosed = false;
        String[] closeStatuses = closedStatus.split(";");
        for (String closeStatus : closeStatuses) {
            if (getIssue(ticket).getStatus().equalsIgnoreCase(closeStatus)) {
                isIssueClosed = true;
            }
        }
        return isIssueClosed;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClosedStatus() {
        return closedStatus;
    }
}
