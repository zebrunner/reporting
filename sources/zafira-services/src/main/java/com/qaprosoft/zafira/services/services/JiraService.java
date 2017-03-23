package com.qaprosoft.zafira.services.services;

import net.rcarz.jiraclient.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    public Issue getIssue(String ticket) {
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
        String status = getIssue(ticket).getStatus().getName();
        LOGGER.info("Status for ticket '" + ticket + "' is '" + status + "'");
        return status;
    }

    public boolean isIssueClosed(String ticket) {
        return getStatusName(ticket).equals("Closed");
    }

    public Map<String, Boolean> checkIssue(String ticket) {
        Map<String, Boolean> errorsMap = new HashMap<>();
        Boolean isExists = false;
        if((isExists = isIssueExists(ticket))) {
            errorsMap.put("Closed", isIssueClosed(ticket));
        } else {
            errorsMap.put("IsExists", isExists);
        }
        return errorsMap;
    }

    /*public static void main(String[] args) {
        BasicCredentials credentials = new BasicCredentials("", "");
        JiraClient jiraClient = new JiraClient("https://jira.tlcinternal.com", credentials);
        Issue issue = null;
        Issue issue2 = null;
        try {
            issue = getI(jiraClient, "AUTO-2194");
            issue2 = jiraClient.getIssue("LMS-78110");
            System.out.println(*//*issue.getAssignee().getName() + " " + projectList.get(0) + " " + issueTypeList.get(0).getName() + " " + issue2.getSummary() + " " + *//*issue.getStatus().getName().equals("Closed"));
        } catch (JiraException e) {
            System.out.println("Unable to initialize Jira issue " + e.getMessage());
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
