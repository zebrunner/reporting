package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueueTestRunParamsType implements Serializable
{
	private static final long serialVersionUID = 5893913105698710480L;

	private String jobName;

	private String branch;

	private String env;

	private String ciRunId;

	private String ciParentUrl;

	private String ciParentBuild;

	public String getJobName()
	{
		return jobName;
	}

	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}

	public String getBranch()
	{
		return branch;
	}

	public void setBranch(String branch)
	{
		this.branch = branch;
	}

	public String getEnv()
	{
		return env;
	}

	public void setEnv(String env)
	{
		this.env = env;
	}

	public String getCiRunId()
	{
		return ciRunId;
	}

	public void setCiRunId(String ciRunId)
	{
		this.ciRunId = ciRunId;
	}

	public String getCiParentUrl()
	{
		return ciParentUrl;
	}

	public void setCiParentUrl(String ciParentUrl)
	{
		this.ciParentUrl = ciParentUrl;
	}

	public String getCiParentBuild()
	{
		return ciParentBuild;
	}

	public void setCiParentBuild(String ciParentBuild)
	{
		this.ciParentBuild = ciParentBuild;
	}
}
