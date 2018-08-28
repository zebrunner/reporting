package com.qaprosoft.zafira.tests.models;

import java.util.List;

import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestType;

public class TestRunViewType
{

	private TestRunType testRunType;
	private List<TestType> testTypes;

	public TestRunViewType(TestRunType testRunType, List<TestType> testTypes)
	{
		this.testRunType = testRunType;
		this.testTypes = testTypes;
	}

	public TestRunType getTestRunType()
	{
		return testRunType;
	}

	public void setTestRunType(TestRunType testRunType)
	{
		this.testRunType = testRunType;
	}

	public List<TestType> getTestTypes()
	{
		return testTypes;
	}

	public void setTestTypes(List<TestType> testTypes)
	{
		this.testTypes = testTypes;
	}
}
