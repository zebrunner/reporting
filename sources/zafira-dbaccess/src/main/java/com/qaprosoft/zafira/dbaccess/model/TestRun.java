package com.qaprosoft.zafira.dbaccess.model;

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

	private User user;
	private Long testSuiteId;
	private Status status;
	private String scmURL;
	private String scmBranch;
	private String scmRevision;
	private String configXML;
	private Long workItemId;
	private Job job;
	private Integer buildNumber;
	private Job upstreamJob;
	private Integer upstreamJobBuildNumber;
	private Initiator startedBy;

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
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

	public Job getJob()
	{
		return job;
	}

	public void setJob(Job job)
	{
		this.job = job;
	}
	
	public Integer getBuildNumber()
	{
		return buildNumber;
	}

	public void setBuildNumber(Integer buildNumber)
	{
		this.buildNumber = buildNumber;
	}

	public Job getUpstreamJob()
	{
		return upstreamJob;
	}

	public void setUpstreamJob(Job upstreamJob)
	{
		this.upstreamJob = upstreamJob;
	}

	public Integer getUpstreamJobBuildNumber()
	{
		return upstreamJobBuildNumber;
	}

	public void setUpstreamJobBuildNumber(Integer upstreamJobBuildNumber)
	{
		this.upstreamJobBuildNumber = upstreamJobBuildNumber;
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
