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
package com.qaprosoft.zafira.service.integration.tool.adapter.testcasemanagement;

import com.qaprosoft.zafira.models.dto.IssueDTO;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.service.exception.ExternalSystemException;
import com.qaprosoft.zafira.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.service.integration.tool.adapter.AdapterParam;
import kong.unirest.Config;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.UnirestException;
import kong.unirest.UnirestInstance;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

import static com.qaprosoft.zafira.service.exception.ExternalSystemException.ExternalSystemErrorDetail.JIRA_ISSUE_CAN_NOT_BE_FOUND;

public class JiraIntegrationAdapter extends AbstractIntegrationAdapter implements TestCaseManagementAdapter {

    private static final String ERR_MSG_ISSUE_NOT_FOUND = "Requested JIRA issue can not be found";

    private final String url;
    private final Set<String> closedStatuses;
    private final UnirestInstance restClient;

    public JiraIntegrationAdapter(Integration integration) {
        super(integration);

        String url = getAttributeValue(integration, JiraParam.URL);
        this.url = !url.endsWith("/") ? url : url.substring(0, url.length() - 1); // trim tailing / if it's there

        String username = getAttributeValue(integration, JiraParam.USERNAME);
        String password = getAttributeValue(integration, JiraParam.PASSWORD);

        this.closedStatuses = Set.of(getAttributeValue(integration, JiraParam.CLOSED_STATUSES).split(";"));
        this.restClient = initClient(username, password);
    }

    private UnirestInstance initClient(String username, String password) {
        Config config = new Config();
        config.setDefaultBasicAuth(username, password);
        config.addDefaultHeader("Accept", "application/json");
        return new UnirestInstance(config);
    }

    @Getter
    @AllArgsConstructor
    private enum JiraParam implements AdapterParam {
        URL("JIRA_URL"),
        USERNAME("JIRA_USER"),
        PASSWORD("JIRA_PASSWORD"),
        CLOSED_STATUSES("JIRA_CLOSED_STATUS");

        private final String name;
    }

    @Override
    public boolean isConnected() {
        try {
            HttpResponse response = restClient.get(url + "/rest/api/latest/serverInfo")
                                              .queryString("doHealthCheck", "true")
                                              .asEmpty();
            return response.getStatus() == 200;
        } catch (UnirestException e) {
            return false;
        }
    }

    @Override
    public IssueDTO getIssue(String issueId) {
        HttpResponse<JsonNode> response = restClient.get(url + "/rest/api/latest/issue/" + issueId).asJson();
        if (response.getStatus() == 200) {
            JSONObject responseBody = response.getBody().getObject();
            return buildIssue(responseBody);
        } else {
            throw new ExternalSystemException(JIRA_ISSUE_CAN_NOT_BE_FOUND, ERR_MSG_ISSUE_NOT_FOUND);
        }
    }

    private IssueDTO buildIssue(JSONObject issue) {
        JSONObject fields = issue.getJSONObject("fields");
        String assigneeName = getObjectValue(fields, "assignee", "name");
        String reporterName =  getObjectValue(fields, "reporter", "name");
        String status = getObjectValue(fields, "status", "name");
        String summary = fields.optString("summary");
        return new IssueDTO(assigneeName, reporterName, summary, status);
    }

    private String getObjectValue(JSONObject fields, String objectKey, String objectValue) {
        JSONObject jsonObject = fields.optJSONObject(objectKey);
        return jsonObject != null ? jsonObject.getString(objectValue) : null;
    }

    @Override
    public boolean isIssueClosed(String issueId) {
        boolean closed;
        try {
            IssueDTO issueDTO = getIssue(issueId);
            closed = closedStatuses.stream()
                                   .anyMatch(closedStatus -> issueDTO.getStatus().equalsIgnoreCase(closedStatus));
        } catch (ExternalSystemException e) {
            closed = false;
        }
        return closed;
    }

    @Override
    public String getUrl() {
        return url;
    }

}
