package com.qaprosoft.zafira.tests.services.api.builders;

import com.qaprosoft.zafira.models.dto.TestSuiteType;

public class TestSuiteTypeBuilder extends AbstractTypeBuilder<TestSuiteType>
{

	private TestSuiteType testSuiteType = new TestSuiteType()
	{
		private static final long serialVersionUID = -2942249203637741424L;
		{
			setName("Test suite " + random.nextInt(10000));
			setFileName("suite" + random.nextInt(10000) + ".xml");
			setUserId(userId);
		}
	};

	@Override
	public TestSuiteType getInstance()
	{
		return this.testSuiteType;
	}

	@Override
	public TestSuiteType register()
	{
		this.testSuiteType = zafiraClient.createTestSuite(this.testSuiteType).getObject();
		return this.testSuiteType;
	}

	public TestSuiteType getTestSuiteType()
	{
		return testSuiteType;
	}

	public void setTestSuiteType(TestSuiteType testSuiteType)
	{
		this.testSuiteType = testSuiteType;
	}
}
