package com.qaprosoft.zafira.models.push;

import com.qaprosoft.zafira.models.dto.TestRunStatistics;

public class TestRunStatisticPush extends AbstractPush
{
	private TestRunStatistics testRunStatistics;

	public TestRunStatisticPush(TestRunStatistics testRunStatistics)
	{
		super(Type.TEST_RUN_STATISTICS);
		this.testRunStatistics = testRunStatistics;
	}

	public TestRunStatistics getTestRunStatistics()
	{
		return testRunStatistics;
	}

	public void setTestRunStatistics(TestRunStatistics testRunStatistics)
	{
		this.testRunStatistics = testRunStatistics;
	}
}
