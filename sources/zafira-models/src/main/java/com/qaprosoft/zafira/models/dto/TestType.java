package com.qaprosoft.zafira.models.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.Status;

@JsonInclude(Include.NON_NULL)
public class TestType extends AbstractType
{	
	private static final long serialVersionUID = 7777895715362820880L;
	@NotNull
	private String name;
	private Status status;
	private String testArgs;
	@NotNull
	private Long testRunId;
	@NotNull
	private Long testCaseId;
	private String testGroup;
	private String message;
	private Long startTime;
	private Long finishTime;
	private String demoURL;
	private String logURL;
	private List<String> workItems;
	private int retry;
	private String configXML;
	private Map<String, Long> testMetrics;
	private boolean knownIssue;
	private boolean needRerun;

	public TestType() {
		
	}
	
	public TestType(String name, Status status, String testArgs, Long testRunId, Long testCaseId, Long startTime,
			String demoURL,
			String logURL, List<String> workItems, int retry, String configXML)
	{
		this.name = name;
		this.status = status;
		this.testArgs = testArgs;
		this.testRunId = testRunId;
		this.testCaseId = testCaseId;
		this.startTime = startTime;
		this.demoURL = demoURL;
		this.logURL = logURL;
		this.workItems = workItems;
		this.retry = retry;
		this.configXML = configXML;
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

	public int getRetry()
	{
		return retry;
	}

	public void setRetry(int retry)
	{
		this.retry = retry;
	}

	public String getConfigXML()
	{
		return configXML;
	}

	public void setConfigXML(String configXML)
	{
		this.configXML = configXML;
	}

	public Map<String, Long> getTestMetrics()
	{
		return testMetrics;
	}

	public void setTestMetrics(Map<String, Long> testMetrics)
	{
		this.testMetrics = testMetrics;
	}

	public boolean isKnownIssue()
	{
		return knownIssue;
	}

	public void setKnownIssue(boolean knownIssue)
	{
		this.knownIssue = knownIssue;
	}

	public String getTestGroup()
	{
		return testGroup;
	}

	public void setTestGroup(String testGroup)
	{
		this.testGroup = testGroup;
	}

	public boolean isNeedRerun()
	{
		return needRerun;
	}

	public void setNeedRerun(boolean needRerun)
	{
		this.needRerun = needRerun;
	}
}