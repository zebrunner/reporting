package com.qaprosoft.zafira.ws.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.dbaccess.model.TestRun.Initiator;

@JsonInclude(Include.NON_NULL)
public class TestRunType extends AbstractType
{
	@NotNull
	private Long testSuiteId;
	private String userName;
	private String scmURL;
	private String scmBranch;
	private String scmRevision;
	private String configXML;
	private Long workItemId;
	@NotNull
	private Long jobId;
	private Long upstreamJobId;
	private Integer upstreamJobBuildNumber;
	@NotNull
	private String buildNumber;
	@NotNull
	private Initiator startedBy;

	public Long getTestSuiteId()
	{
		return testSuiteId;
	}

	public void setTestSuiteId(Long testSuiteId)
	{
		this.testSuiteId = testSuiteId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getScmURL()
	{
		return scmURL;
	}

	public void setScmURL(String scmURL)
	{
		this.scmURL = scmURL;
	}

	public String getScmBranch()
	{
		return scmBranch;
	}

	public void setScmBranch(String scmBranch)
	{
		this.scmBranch = scmBranch;
	}

	public String getScmRevision()
	{
		return scmRevision;
	}

	public void setScmRevision(String scmRevision)
	{
		this.scmRevision = scmRevision;
	}

	public String getConfigXML()
	{
		return configXML;
	}

	public void setConfigXML(String configXML)
	{
		this.configXML = configXML;
	}

	public Long getWorkItemId()
	{
		return workItemId;
	}

	public void setWorkItemId(Long workItemId)
	{
		this.workItemId = workItemId;
	}

	public Long getJobId()
	{
		return jobId;
	}

	public void setJobId(Long jobId)
	{
		this.jobId = jobId;
	}

	public String getBuildNumber()
	{
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber)
	{
		this.buildNumber = buildNumber;
	}

	public Initiator getStartedBy()
	{
		return startedBy;
	}

	public void setStartedBy(Initiator startedBy)
	{
		this.startedBy = startedBy;
	}

	public Long getUpstreamJobId()
	{
		return upstreamJobId;
	}

	public void setUpstreamJobId(Long upstreamJobId)
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
}
