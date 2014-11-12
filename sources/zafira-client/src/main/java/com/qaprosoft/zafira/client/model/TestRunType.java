package com.qaprosoft.zafira.client.model;


public class TestRunType extends AbstractType
{
	public enum Status
	{
		IN_PROGRESS, PASSED, FAILED;
	}

	public enum Initiator
	{
		SCHEDULER, UPSTREAM_JOB, HUMAN;
	}
	
	public TestRunType(Long testSuiteId, String userName, String scmURL, String scmBranch, String scmRevision,
			String configXML, Long jobId, Integer buildNumber, Initiator startedBy)
	{
		this.testSuiteId = testSuiteId;
		this.userName = userName;
		this.scmURL = scmURL;
		this.scmBranch = scmBranch;
		this.scmRevision = scmRevision;
		this.configXML = configXML;
		this.jobId = jobId;
		this.buildNumber = buildNumber;
		this.startedBy = startedBy;
	}

	public TestRunType(Long testSuiteId, String scmURL, String scmBranch, String scmRevision, String configXML,
			Long jobId, Long upstreamJobId, Integer upstreamJobBuildNumber, Integer buildNumber, Initiator startedBy)
	{
		this.testSuiteId = testSuiteId;
		this.scmURL = scmURL;
		this.scmBranch = scmBranch;
		this.scmRevision = scmRevision;
		this.configXML = configXML;
		this.jobId = jobId;
		this.upstreamJobId = upstreamJobId;
		this.upstreamJobBuildNumber = upstreamJobBuildNumber;
		this.buildNumber = buildNumber;
		this.startedBy = startedBy;
	}
	
	public TestRunType(Long testSuiteId, String scmURL, String scmBranch, String scmRevision, String configXML,
			Long jobId, Integer buildNumber, Initiator startedBy)
	{
		super();
		this.testSuiteId = testSuiteId;
		this.scmURL = scmURL;
		this.scmBranch = scmBranch;
		this.scmRevision = scmRevision;
		this.configXML = configXML;
		this.jobId = jobId;
		this.buildNumber = buildNumber;
		this.startedBy = startedBy;
	}

	private Long testSuiteId;
	private Status status;
	private String userName;
	private String scmURL;
	private String scmBranch;
	private String scmRevision;
	private String configXML;
	private Long workItemId;
	private Long jobId;
	private Long upstreamJobId;
	private Integer upstreamJobBuildNumber;
	private Integer buildNumber;
	private Initiator startedBy;

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
}
