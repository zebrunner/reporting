package com.qaprosoft.zafira.client.model;

import java.util.List;


public class TestType extends AbstractType
{
	public enum Status
	{
		PASSED, FAILED, SKIPPED;
	}
	
	private String name;
	private Status status;
	private String testArgs;
	private Long testRunId;
	private Long testCaseId;
	private String message;
	private Long startTime;
	private Long finishTime;
	private String demoURL;
	private String logURL;
	private List<String> workItems;
	
	public TestType(String name, Status status, String testArgs, Long testRunId, Long testCaseId, String message,
			Long startTime, Long finishTime, String demoURL, String logURL, List<String> workItems)
	{
		this.name = name;
		this.status = status;
		this.testArgs = testArgs;
		this.testRunId = testRunId;
		this.testCaseId = testCaseId;
		this.message = message;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.demoURL = demoURL;
		this.logURL = logURL;
		this.workItems = workItems;
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

	public Long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Long startTime)
	{
		this.startTime = startTime;
	}

	public Long getFinishTime()
	{
		return finishTime;
	}

	public void setFinishTime(Long finishTime)
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

	public List<String> getWorkItems()
	{
		return workItems;
	}

	public void setWorkItems(List<String> workItems)
	{
		this.workItems = workItems;
	}
}
