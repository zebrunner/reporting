package com.qaprosoft.zafira.dbaccess.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Date;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class Test extends AbstractEntity
{
	private static final long serialVersionUID = -915700504693067056L;

	private String name;
	private Status status;
	private String testArgs;
	private Long testRunId;
	private Long testCaseId;
	private String message;
	private Date startTime;
	private Date finishTime;
	private String demoURL;
	private String logURL;
	private int retry;
	private TestConfig testConfig;
	private List<WorkItem> workItems;
	private boolean knownIssue;
	private String owner;

	public Test()
	{
		testConfig = new TestConfig();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public String getTestArgs()
	{
		return testArgs;
	}

	public void setTestArgs(String testArgs)
	{
		this.testArgs = testArgs;
	}

	public Long getTestRunId()
	{
		return testRunId;
	}

	public void setTestRunId(Long testRunId)
	{
		this.testRunId = testRunId;
	}

	public Long getTestCaseId()
	{
		return testCaseId;
	}

	public void setTestCaseId(Long testCaseId)
	{
		this.testCaseId = testCaseId;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public Date getFinishTime()
	{
		return finishTime;
	}

	public void setFinishTime(Date finishTime)
	{
		this.finishTime = finishTime;
	}

	public String getDemoURL()
	{
		return demoURL;
	}

	public void setDemoURL(String demoURL)
	{
		this.demoURL = demoURL;
	}

	public String getLogURL()
	{
		return logURL;
	}

	public void setLogURL(String logURL)
	{
		this.logURL = logURL;
	}

	public int getRetry()
	{
		return retry;
	}

	public void setRetry(int retry)
	{
		this.retry = retry;
	}

	public TestConfig getTestConfig()
	{
		return testConfig;
	}

	public void setTestConfig(TestConfig testConfig)
	{
		this.testConfig = testConfig;
	}

	public List<WorkItem> getWorkItems()
	{
		return workItems;
	}

	public void setWorkItems(List<WorkItem> workItems)
	{
		this.workItems = workItems;
	}

	public boolean isKnownIssue()
	{
		return knownIssue;
	}

	public void setKnownIssue(boolean knownIssue)
	{
		this.knownIssue = knownIssue;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}
}
