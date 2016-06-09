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
	
	private String ciRunId;
	private Long testSuiteId;
	private Status status;
	private String scmURL;
	private String scmBranch;
	private String scmCommit;
	private String configXML;
	private String workItem;
	private Long jobId;
	private Long upstreamJobId;
	private Integer upstreamJobBuildNumber;
	private Integer buildNumber;
	private Initiator startedBy;
	private Long userId;
	
	public TestRunType(String ciRunId,Long testSuiteId, Long userId, String scmURL, String scmBranch, String scmCommit,
			String configXML, Long jobId, Integer buildNumber, Initiator startedBy, String workItem)
	{
		this.ciRunId = ciRunId;
		this.testSuiteId = testSuiteId;
		this.userId = userId;
		this.scmURL = scmURL;
		this.scmBranch = scmBranch;
		this.scmCommit = scmCommit;
		this.configXML = configXML;
		this.jobId = jobId;
		this.buildNumber = buildNumber;
		this.startedBy = startedBy;
		this.workItem = workItem;
	}

	public TestRunType(String ciRunId, Long testSuiteId, String scmURL, String scmBranch, String scmCommit, String configXML,
			Long jobId, Long upstreamJobId, Integer upstreamJobBuildNumber, Integer buildNumber, Initiator startedBy, String workItem)
	{
		this.ciRunId = ciRunId;
		this.testSuiteId = testSuiteId;
		this.scmURL = scmURL;
		this.scmBranch = scmBranch;
		this.scmCommit = scmCommit;
		this.configXML = configXML;
		this.jobId = jobId;
		this.upstreamJobId = upstreamJobId;
		this.upstreamJobBuildNumber = upstreamJobBuildNumber;
		this.buildNumber = buildNumber;
		this.startedBy = startedBy;
		this.workItem = workItem;
	}
	
	public TestRunType(String ciRunId, Long testSuiteId, String scmURL, String scmBranch, String scmCommit, String configXML,
			Long jobId, Integer buildNumber, Initiator startedBy, String workItem)
	{
		this.ciRunId = ciRunId;
		this.testSuiteId = testSuiteId;
		this.scmURL = scmURL;
		this.scmBranch = scmBranch;
		this.scmCommit = scmCommit;
		this.configXML = configXML;
		this.jobId = jobId;
		this.buildNumber = buildNumber;
		this.startedBy = startedBy;
		this.workItem = workItem;
	}

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

	public String getWorkItem()
	{
		return workItem;
	}

	public void setWorkItem(String workItem)
	{
		this.workItem = workItem;
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
}	

