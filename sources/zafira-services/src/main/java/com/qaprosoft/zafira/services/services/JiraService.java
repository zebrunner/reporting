package com.qaprosoft.zafira.services.services;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;

@Service
public class JiraService
{
	private static final Logger LOGGER = Logger.getLogger(JiraService.class);

	private BasicCredentials credentials;
	
	private JiraClient jiraClient;
	
	public JiraService(String url, String username, String password)
	{
		try
		{
			if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(username) && !StringUtils.isEmpty(password))
			{
				this.credentials = new BasicCredentials(username, password);
				this.jiraClient = new JiraClient(url, credentials);
			}
		} catch (Exception e)
		{
			LOGGER.error("Unable to initialize Jira integration: " + e.getMessage());
		}
	}

	public boolean isConnected()
	{
		boolean connected = false;
		try
		{
			connected = this.jiraClient != null && this.jiraClient.getProjects() != null;
		}
		catch(Exception e)
		{
			LOGGER.error("Unable to connect to JIRA", e);
		}
		return connected;
	}

	public Issue getIssue(String ticket)
	{
		Issue issue = null;
		try
		{
			issue = jiraClient.getIssue(ticket);
		} catch (Exception e)
		{
			LOGGER.error("Unable to find Jira issue: " + ticket, e);
		}
		return issue;
	}
}