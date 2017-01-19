package com.qaprosoft.zafira.client.model;

public class JobType extends AbstractType
{
	private String name;
	private String jobURL;
	private String jenkinsHost;
	private Long userId;

	public JobType(String name, String jobURL, String jenkinsHost, Long userId)
	{
		this.name = name;
		this.jobURL = jobURL;
		this.jenkinsHost = jenkinsHost;
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

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}
}
