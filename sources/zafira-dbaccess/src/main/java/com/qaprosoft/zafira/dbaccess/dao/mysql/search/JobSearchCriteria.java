package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

public class JobSearchCriteria
{
	private Integer upstreamJobId;
	private Integer upstreamJobBuildNumber;
	private String scmURL;
	private Boolean notExecuted;
	private String owner;
	private Integer hashcode;
	private Integer failurePercent;

	public Integer getUpstreamJobId()
	{
		return upstreamJobId;
	}

	public void setUpstreamJobId(Integer upstreamJobId)
	{
		this.upstreamJobId = upstreamJobId;
	}

	public Integer getUpstreamJobBuildNumber()
	{
		return upstreamJobBuildNumber;
	}

	public void setUpstreamJobBuildNumber(Integer upstreamJobBuildNumber)
	{
		this.upstreamJobBuildNumber = upstreamJobBuildNumber;
	}

	public String getScmURL()
	{
		return scmURL;
	}

	public void setScmURL(String scmURL)
	{
		this.scmURL = scmURL;
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

	public Integer getHashcode()
	{
		return hashcode;
	}

	public void setHashcode(Integer hashcode)
	{
		this.hashcode = hashcode;
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
