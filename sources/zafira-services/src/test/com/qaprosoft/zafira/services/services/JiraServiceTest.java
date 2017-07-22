package com.qaprosoft.zafira.services.services;


import org.junit.Test;

/**
 * Created by irina on 20.7.17.
 */
public class JiraServiceTest {

    @Test
    public void testGetJiraInfo() throws Exception {
        JiraService jiraService = new JiraService();
        jiraService.getJiraInfo();

    }

}