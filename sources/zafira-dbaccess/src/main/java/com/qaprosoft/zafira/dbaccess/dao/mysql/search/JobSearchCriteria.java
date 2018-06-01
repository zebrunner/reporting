package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

public class JobSearchCriteria
{
	private String owner;
	private Long upstreamJobId;
	private String upstreamJobUrl;
	private Integer upstreamJobBuildNumber;
	private String scmURL;
	private Integer hashcode;
	private Integer failurePercent;

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public Long getUpstreamJobId()
	{
		return upstreamJobId;
	}

	public void setUpstreamJobId(Long upstreamJobId)
	{
		this.upstreamJobId = upstreamJobId;
	}

	public String getUpstreamJobUrl()
	{
		return upstreamJobUrl;
	}

	public void setUpstreamJobUrl(String upstreamJobUrl)
	{
		this.upstreamJobUrl = upstreamJobUrl;
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
