package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestRunSearchCriteria extends SearchCriteria
{
	private Long id;
	private Long testSuiteId;
	private String testSuite;
	private String executionURL;
	private String environment;
	private String platform;
	private Date date;
	private Date fromDate;
	private Date toDate;
	
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

}
