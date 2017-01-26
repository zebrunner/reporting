package com.qaprosoft.zafira.models.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.Project;

@JsonInclude(Include.NON_NULL)
public class TestCaseType extends AbstractType
{
	private static final long serialVersionUID = 4361075320159665047L;
	@NotNull
	private String testClass;
	@NotNull
	private String testMethod;
	private String info;
	@NotNull
	private Long testSuiteId;
	@NotNull
	private Long userId;
	private Project project;

	public TestCaseType() {
		
	}
	
	public TestCaseType(String testClass, String testMethod, String info, Long testSuiteId, Long userId)
	{
		this.testClass = testClass;
		this.testMethod = testMethod;
		this.info = info;
		this.testSuiteId = testSuiteId;
		this.userId = userId;
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

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public Project getProject()
	{
		return project;
	}

	public void setProject(Project project)
	{
		this.project = project;
	}
}
