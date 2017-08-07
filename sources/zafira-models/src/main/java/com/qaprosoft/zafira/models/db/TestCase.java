package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TestCase extends AbstractEntity
{
	private static final long serialVersionUID = 4877029098773384360L;

	private String testClass;
	private String testMethod;
	private Status status;
	private String info;
	private Long testSuiteId;
	private User primaryOwner = new User();
	private User secondaryOwner = new User();
	private TestSuite testSuite = new TestSuite();
	private Project project;

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

	public User getPrimaryOwner()
	{
		return primaryOwner;
	}

	public void setPrimaryOwner(User primaryOwner)
	{
		this.primaryOwner = primaryOwner;
	}

	public User getSecondaryOwner()
	{
		return secondaryOwner;
	}

	public void setSecondaryOwner(User secondaryOwner)
	{
		this.secondaryOwner = secondaryOwner;
	}

	public TestSuite getTestSuite()
	{
		return testSuite;
	}

	public void setTestSuite(TestSuite testSuite)
	{
		this.testSuite = testSuite;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Project getProject()
	{
		return project;
	}

	public void setProject(Project project)
	{
		this.project = project;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj != null && obj instanceof TestCase && this.hashCode() == ((TestCase) obj).hashCode());
	}

	@Override
	public int hashCode()
	{
		return (testClass + testMethod + testSuiteId + info + primaryOwner.getId() + secondaryOwner.getId()).hashCode();
	}
}
