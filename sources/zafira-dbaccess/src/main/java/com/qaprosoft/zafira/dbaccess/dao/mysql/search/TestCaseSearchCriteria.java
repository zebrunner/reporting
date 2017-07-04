package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestCaseSearchCriteria extends SearchCriteria implements DateSearchCriteria
{
	private Long id;
	private List<Long> ids;
	private String testClass;
	private String testMethod;
	private String testSuiteName;
	private String testSuiteFile;
	private String username;
	private Date date;
	private Date fromDate;
	private Date toDate;
	private String period;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}
	
	public List<Long> getIds()
	{
		return ids;
	}

	public void setIds(List<Long> ids)
	{
		this.ids = ids;
	}
	
	public void addId(Long id)
	{
		if(this.ids == null)
		{
			this.ids = new ArrayList<>();
		}
		this.ids.add(id);
	}

	public String getTestClass()
	{
		return testClass;
	}

	public void setTestClass(String testClass)
	{
		this.testClass = testClass;
	}

	public String getTestMethod()
	{
		return testMethod;
	}

	public void setTestMethod(String testMethod)
	{
		this.testMethod = testMethod;
	}

	public String getTestSuiteName()
	{
		return testSuiteName;
	}

	public void setTestSuiteName(String testSuiteName)
	{
		this.testSuiteName = testSuiteName;
	}

	public String getTestSuiteFile()
	{
		return testSuiteFile;
	}

	public void setTestSuiteFile(String testSuiteFile)
	{
		this.testSuiteFile = testSuiteFile;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
}
