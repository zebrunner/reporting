package com.qaprosoft.zafira.client.model;


public class TestCaseType extends AbstractType
{
	private String testClass;
	private String testMethod;
	private String info;
	private Long testSuiteId;
	private String userName;

	public TestCaseType(String testClass, String testMethod, String info, Long testSuiteId, String userName)
	{
		this.testClass = testClass;
		this.testMethod = testMethod;
		this.info = info;
		this.testSuiteId = testSuiteId;
		this.userName = userName;
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

	public String getInfo()
	{
		return info;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public Long getTestSuiteId()
	{
		return testSuiteId;
	}

	public void setTestSuiteId(Long testSuiteId)
	{
		this.testSuiteId = testSuiteId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}
}
