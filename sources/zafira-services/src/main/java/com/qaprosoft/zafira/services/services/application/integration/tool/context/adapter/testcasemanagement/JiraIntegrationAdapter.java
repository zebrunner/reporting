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
package com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.testcasemanagement;

import com.qaprosoft.zafira.models.db.integration.Integration;
import com.qaprosoft.zafira.models.dto.TestCaseManagementIssueType;
import com.qaprosoft.zafira.services.exceptions.ExternalSystemException;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AdapterParam;
import net.rcarz.jiraclient.Issue;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;

@SuppressWarnings("deprecation")
public class JiraIntegrationAdapter extends AbstractIntegrationAdapter implements TestCaseManagementAdapter {

    private final String url;
    private final String username;
    private final String password;
    private final String closedStatus;

    private final BasicCredentials credentials;
    private final JiraClient jiraClient;

    public JiraIntegrationAdapter(Integration integration) {
        super("JIRA", integration);

        this.url = getAttributeValue(JiraParam.JIRA_URL);
        this.username = getAttributeValue(JiraParam.JIRA_USERNAME);
        this.password = getAttributeValue(JiraParam.JIRA_PASSWORD);
        this.closedStatus = getAttributeValue(JiraParam.JIRA_CLOSED_STATUS);

        this.credentials = new BasicCredentials(username, password);
        this.jiraClient = new JiraClient(url, credentials);
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        ((DefaultHttpClient) getJiraClient().getRestClient().getHttpClient()).setParams(httpParams);
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
        try {
            return jiraClient.getProjects() != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public TestCaseManagementIssueType getIssue(String ticket) {
        Issue issue;
        try {
            issue = jiraClient.getIssue(ticket);
        } catch (Exception e) {
            throw new ExternalSystemException("Unable to find Jira issue: " + ticket, e);
        }
        return new TestCaseManagementIssueType(issue.getAssignee().getName(), issue.getReporter().getName(), issue.getSummary(), issue.getStatus().getName());
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

    public BasicCredentials getCredentials() {
        return credentials;
    }

    public JiraClient getJiraClient() {
        return jiraClient;
    }

    public String getClosedStatus() {
        return closedStatus;
    }
}
