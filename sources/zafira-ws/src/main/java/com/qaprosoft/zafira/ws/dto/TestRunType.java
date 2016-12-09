package com.qaprosoft.zafira.ws.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.dbaccess.model.Project;
import com.qaprosoft.zafira.dbaccess.model.Status;
import com.qaprosoft.zafira.dbaccess.model.TestRun.Initiator;

@JsonInclude(Include.NON_NULL)
public class TestRunType extends AbstractType
{
	private String ciRunId;
	@NotNull
	private Long testSuiteId;
	private Status status;
	private String scmURL;
	private String scmBranch;
	private String scmCommit;
	private String configXML;
	@NotNull
	private Long jobId;
	private Long upstreamJobId;
	private Integer upstreamJobBuildNumber;
	@NotNull
	private Integer buildNumber;
	@NotNull
	private Initiator startedBy;
	private Long userId;
	private String workItem;
	private Project project;
	private boolean knownIssue;
	private Integer elapsed;
	private Integer eta;

	public String getCiRunId()
	{
		return ciRunId;
	}

	public void setCiRunId(String ciRunId)
	{
		this.ciRunId = ciRunId;
	}

	public Long getTestSuiteId()
	{
		return testSuiteId;
	}

	public void setTestSuiteId(Long testSuiteId)
	{
		this.testSuiteId = testSuiteId;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
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

	public String getScmCommit()
	{
		return scmCommit;
	}

	public void setScmCommit(String scmCommit)
	{
		this.scmCommit = scmCommit;
	}

	public String getConfigXML()
	{
		return configXML;
	}

	public void setConfigXML(String configXML)
	{
		this.configXML = configXML;
	}

	public Long getJobId()
	{
		return jobId;
	}

	public void setJobId(Long jobId)
	{
		this.jobId = jobId;
	}

	public Integer getBuildNumber()
	{
		return buildNumber;
	}

	public void setBuildNumber(Integer buildNumber)
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

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public String getWorkItem()
	{
		return workItem;
	}

	public void setWorkItem(String workItem)
	{
		this.workItem = workItem;
	}

	public Project getProject()
	{
		return project;
	}

	public void setProject(Project project)
	{
		this.project = project;
	}

	public boolean isKnownIssue()
	{
		return knownIssue;
	}

	public void setKnownIssue(boolean knownIssue)
	{
		this.knownIssue = knownIssue;
	}

	public Integer getElapsed()
	{
		return elapsed;
	}

	public void setElapsed(Integer elapsed)
	{
		this.elapsed = elapsed;
	}

	public Integer getEta()
	{
		return eta;
	}

	public void setEta(Integer eta)
	{
		this.eta = eta;
	}
}
