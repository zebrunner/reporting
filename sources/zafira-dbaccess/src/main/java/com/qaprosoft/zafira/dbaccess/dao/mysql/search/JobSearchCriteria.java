package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

public class JobSearchCriteria
{

	private String env;
	private String platform;
	private String owner;
	private String scmURL;
	private Boolean notExecuted;

	private Integer failurePercent;

	public String getScmURL()
	{
		return scmURL;
	}

	public void setScmURL(String scmURL)
	{
		this.scmURL = scmURL;
	}

	public String getEnv()
	{
		return env;
	}

	public void setEnv(String env)
	{
		this.env = env;
	}

	public Boolean getNotExecuted()
	{
		return notExecuted;
	}

	public void setNotExecuted(Boolean notExecuted)
	{
		this.notExecuted = notExecuted;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public String getPlatform()
	{
		return platform;
	}

	public void setPlatform(String platform)
	{
		this.platform = platform;
	}

	public Integer getFailurePercent()
	{
		return failurePercent;
	}

	public void setFailurePercent(Integer failurePercent)
	{
		this.failurePercent = failurePercent;
	}
}
