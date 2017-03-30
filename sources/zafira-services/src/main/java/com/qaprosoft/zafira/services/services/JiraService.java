package com.qaprosoft.zafira.services.services;

import net.rcarz.jiraclient.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JiraService {

    private static final Logger LOGGER = Logger.getLogger(JiraService.class);

    private BasicCredentials credentials;
    private JiraClient jiraClient;

    public JiraService(String url, String username, String password) {
        try
        {
            if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(username) && !StringUtils.isEmpty(password))
            {
                this.credentials = new BasicCredentials(username, password);
                this.jiraClient = new JiraClient(url, credentials);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Unable to initialize Jira integration: " + e.getMessage());
        }
    }

    public boolean isConnectedToJira() {
        try {
            jiraClient.getProjects().isEmpty();
        } catch (JiraException e) {
            return false;
        }
        return true;
    }

    public Issue getIssue(String ticket) {
        Issue issue = null;
        try {
            issue = jiraClient.getIssue(ticket);
        } catch (JiraException e) {
            LOGGER.error("Unable to initialize Jira issue '" + ticket + "' " + e.getMessage());
        } finally {
            return issue;
        }
    }

    public Issue getIssueWithCheck(String ticket) {
        Issue issue = null;
        try {
            issue = jiraClient.getIssue(ticket);
        } catch (JiraException e) {
            LOGGER.error("Unable to initialize Jira issue '" + ticket + "' " + e.getMessage());
        }
        return issue;
    }

    public boolean isIssueExists(String ticket) {
        boolean isExists = false;
        try {
            jiraClient.getIssue(ticket);
            isExists = true;
        } catch (JiraException e) {
            LOGGER.error("Issue '" + ticket + "' does not exists " + e.getMessage());
        } finally {
            return isExists;
        }
    }

    public String getStatusName(String ticket) {
        String status = getIssueWithCheck(ticket).getStatus().getName();
        LOGGER.info("Status for ticket '" + ticket + "' is '" + status + "'");
        return status;
    }

    public boolean isIssueClosed(String ticket) {
        return getStatusName(ticket).equals("Closed");
    }

    /*public static void main(String[] args) {
        BasicCredentials credentials = new BasicCredentials("", "");
        JiraClient jiraClient = new JiraClient("", credentials);
        Issue issue = null;
        Issue issue2 = null;
        try {
            issue = getI(jiraClient, "AUTO-2194");
            issue2 = jiraClient.getIssue("LMS-78110");
            List<Project> projectList = jiraClient.getProjects();
            System.out.println(issue.getAssignee().getName() + " " + issue2.getSummary() + " " + issue.getStatus().getName().equals("Closed") + " " + projectList.size());
        } catch (JiraException e) {
            String s = e.getCause().toString();
            if(s.matches("^java.net.UnknownHostException.*"))
                System.out.println();
            if(s.matches("^net.rcarz.jiraclient.RestException:\\s+401\\s+Unauthorized.*"))
                System.out.println("Unable to initialize Jira issue " + e.getMessage() + s);
        }
    }

    public static Issue getI(JiraClient jiraClient, String s) {
        Issue issue = null;
        try {
            issue = jiraClient.getIssue(s);
        } catch (JiraException e) {
            System.out.println("Unable to initialize Jira issue " + e.getMessage());
        } finally {
            return issue;
        }
    }*/

}
