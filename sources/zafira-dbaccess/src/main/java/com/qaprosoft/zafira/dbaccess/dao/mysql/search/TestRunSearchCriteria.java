package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

import java.util.Date;

public class TestRunSearchCriteria extends SearchCriteria
{
	private Long id;
	private String testSuite;
	private String executionURL;
	private String environment;
	private String platform;
	private Date date;
	
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
}
