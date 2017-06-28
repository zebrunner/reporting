package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Job extends AbstractEntity
{
	private static final long serialVersionUID = -7136622077881406856L;

	private String name;
	private String jobURL;
	private String jenkinsHost;
	private User user = new User();
	
	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
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
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj != null && obj instanceof Job && this.hashCode() == ((Job)obj).hashCode());
	}
	
	@Override
	public int hashCode()
	{
		return (jobURL + user.getUsername()).hashCode();
	}
}
