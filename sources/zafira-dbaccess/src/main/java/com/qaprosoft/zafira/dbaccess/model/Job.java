package com.qaprosoft.zafira.dbaccess.model;

public class Job extends AbstractEntity
{
	private static final long serialVersionUID = -7136622077881406856L;

	private Long userId;
	private String name;
	private String jobURL;
	private String jenkinsHost;

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getJobURL()
	{
		return jobURL;
	}

	public void setJobURL(String jobURL)
	{
		this.jobURL = jobURL;
	}

	public String getJenkinsHost()
	{
		return jenkinsHost;
	}

	public void setJenkinsHost(String jenkinsHost)
	{
		this.jenkinsHost = jenkinsHost;
	}
}
