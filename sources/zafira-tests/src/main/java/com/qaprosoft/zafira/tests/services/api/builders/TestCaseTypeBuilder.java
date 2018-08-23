package com.qaprosoft.zafira.tests.services.api.builders;

import com.qaprosoft.zafira.models.dto.application.TestCaseType;
import com.qaprosoft.zafira.models.dto.application.TestSuiteType;

public class TestCaseTypeBuilder extends AbstractTypeBuilder<TestCaseType>
{

	private IModelBuilder<TestSuiteType> testSuiteTypeBuilder;

	private TestCaseType testCaseType = new TestCaseType()
	{
		private static final long serialVersionUID = -2942249203637741424L;
		{
			setTestClass("com.qaprosoft.com.testClass");
			setTestMethod("test" + getNextRandomInt());
			setInfo("");
			setPrimaryOwnerId(userId);
		}
	};

	public TestCaseTypeBuilder(IModelBuilder<TestSuiteType> testSuiteTypeBuilder)
	{
		this.testSuiteTypeBuilder = testSuiteTypeBuilder;
	}

	public TestCaseTypeBuilder()
	{
		this.testSuiteTypeBuilder = new TestSuiteTypeBuilder();
	}

	@Override
	public TestCaseType getInstance()
	{
		return this.testCaseType;
	}

	@Override
	public TestCaseType register()
	{
		this.testCaseType.setTestSuiteId(testSuiteTypeBuilder.getCurrentInstance().getId());
		this.testCaseType = zafiraClient.createTestCase(this.testCaseType).getObject();
		return this.testCaseType;
	}

	public TestCaseType getTestCaseType()
	{
		return testCaseType;
	}

	public void setTestCaseType(TestCaseType testCaseType)
	{
		this.testCaseType = testCaseType;
	}
}
