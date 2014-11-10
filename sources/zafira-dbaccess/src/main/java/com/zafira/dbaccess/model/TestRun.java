package com.zafira.dbaccess.model;

public class TestRun extends AbstractEntity
{
	private static final long serialVersionUID = -1847933012610222160L;

	public enum Status
	{
		IN_PROGRESS, PASSED, FAILED;
	}

	public enum Initiator
	{
		SCHEDULER, UPSTREAM_JOB, HUMAN;
	}

	private Long userId;
	private Long testSuiteId;
	private Status status;
	private String scmURL;
	private String scmBranch;
	private String scmRevision;
	private String configXML;
	private Long workItemId;
	private Long jobId;
	private String buildURL;
	private Initiator startedBy;

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
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

	public String getBuildURL()
	{
		return buildURL;
	}

	public void setBuildURL(String buildURL)
	{
		this.buildURL = buildURL;
	}

	public Initiator getStartedBy()
	{
		return startedBy;
	}

	public void setStartedBy(Initiator startedBy)
	{
		this.startedBy = startedBy;
	}
}
