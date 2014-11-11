package com.qaprosoft.zafira.ws.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class JobType extends AbstractType
{
	private String name;
	@NotNull
	private String jobURL;
	private String jenkinsHost;
	@NotNull
	private String userName;

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

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}
}
