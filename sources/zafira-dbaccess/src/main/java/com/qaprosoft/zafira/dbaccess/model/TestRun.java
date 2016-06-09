package com.qaprosoft.zafira.dbaccess.model;

public class TestRun extends AbstractEntity
{
	private static final long serialVersionUID = -1847933012610222160L;

	public enum Status
	{
		IN_PROGRESS, PASSED, FAILED, ABORTED;
	}

	public enum Initiator
	{
		SCHEDULER, UPSTREAM_JOB, HUMAN;
	}

	private String ciRunId;
	private User user;
	private TestSuite testSuite;
	private Status status;
	private String scmURL;
	private String scmBranch;
	private String scmCommit;
	private String configXML;
	private WorkItem workItem;
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

	public TestSuite getTestSuite() {
		return testSuite;
	}

	public void setTestSuite(TestSuite testSuite) {
		this.testSuite = testSuite;
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

	public WorkItem getWorkItem()
	{
		return workItem;
	}

	public void setWorkItem(WorkItem workItem)
	{
		this.workItem = workItem;
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

	public String getCiRunId()
	{
		return ciRunId;
	}

	public void setCiRunId(String ciRunId)
	{
		this.ciRunId = ciRunId;
	}
}
