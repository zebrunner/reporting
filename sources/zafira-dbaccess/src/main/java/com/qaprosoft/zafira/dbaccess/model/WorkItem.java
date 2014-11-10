package com.qaprosoft.zafira.dbaccess.model;

public class WorkItem extends AbstractEntity 
{
	private static final long serialVersionUID = 5440580857483390564L;
	
	private String jiraId;

	public String getJiraId()
	{
		return jiraId;
	}

	public void setJiraId(String jiraId)
	{
		this.jiraId = jiraId;
	}
}
