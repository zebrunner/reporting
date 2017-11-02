package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.qaprosoft.zafira.models.db.Status;

public class TestRunSearchCriteria extends SearchCriteria implements DateSearchCriteria
{
	private Long id;
	private Long testSuiteId;
	private String testSuite;
	private String executionURL;
	private String environment;
	private String platform;
	private String appVersion;
	private Date date;
	private Date fromDate;
	private Date toDate;
	private Status status;
	private String period;
	private Boolean reviewed;
	
	public TestRunSearchCriteria()
	{
		super.setSortOrder(SortOrder.DESC);
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getTestSuiteId()
	{
		return testSuiteId;
	}

	public void setTestSuiteId(Long testSuiteId)
	{
		this.testSuiteId = testSuiteId;
	}

	public String getTestSuite()
	{
		return testSuite;
	}

	public void setTestSuite(String testSuite)
	{
		this.testSuite = testSuite;
	}

	public String getExecutionURL()
	{
		return executionURL;
	}

	public void setExecutionURL(String executionURL)
	{
		this.executionURL = executionURL;
	}

	public String getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}

	public String getPlatform()
	{
		return platform;
	}

	public void setPlatform(String platform)
	{
		this.platform = platform;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public Date getFromDate()
	{
		return fromDate;
	}

	public void setFromDate(Date fromDate)
	{
		this.fromDate = fromDate;
	}
	
	public void setFromDateString(String fromDate) throws ParseException
	{
		this.fromDate = new SimpleDateFormat("MM-dd-yyyy").parse(fromDate);
	}

	public Date getToDate()
	{
		return toDate;
	}

	public void setToDate(Date toDate)
	{
		this.toDate = toDate;
	}
	
	public void setToDateString(String toDate) throws ParseException
	{
		this.toDate = new SimpleDateFormat("MM-dd-yyyy").parse(toDate);
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getAppVersion()
	{
		return appVersion;
	}

	public void setAppVersion(String appVersion)
	{
		this.appVersion = appVersion;
	}

	public Boolean getReviewed() 
	{
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) 
	{
		this.reviewed = reviewed;
	}
}