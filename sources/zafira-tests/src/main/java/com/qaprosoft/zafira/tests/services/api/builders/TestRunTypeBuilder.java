package com.qaprosoft.zafira.tests.services.api.builders;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestSuiteType;

public class TestRunTypeBuilder extends AbstractTypeBuilder<TestRunType>
{

	private IModelBuilder<TestSuiteType> testSuiteTypeBuilder;
	private IModelBuilder<JobType> jobTypeBuilder;

	private TestRunType testRunType = new TestRunType()
	{
		private static final long serialVersionUID = -2942249203637741424L;
		{
			try
			{
				setConfigXML(FileUtils.readFileToString(new File(this.getClass().getResource("/api_data/testruns/config.xml").getPath())));
			} catch (IOException e)
			{
				LOGGER.error(e.getMessage());
			}
			setCiRunId(UUID.randomUUID().toString());
			setStartedBy(TestRun.Initiator.HUMAN);
			setUserId(userId);
			setBuildNumber(0);
		}
	};

	public TestRunTypeBuilder(IModelBuilder<TestSuiteType> testSuiteTypeBuilder, IModelBuilder<JobType> jobTypeBuilder)
	{
		this.testSuiteTypeBuilder = testSuiteTypeBuilder;
		this.jobTypeBuilder = jobTypeBuilder;
	}

	public TestRunTypeBuilder()
	{
		this.testSuiteTypeBuilder = new TestSuiteTypeBuilder();
		this.jobTypeBuilder = new JobTypeBuilder();
	}

	@Override
	public TestRunType getInstance()
	{
		return this.testRunType;
	}

	@Override
	public TestRunType register()
	{
		this.testRunType.setTestSuiteId(this.testSuiteTypeBuilder.getCurrentInstance().getId());
		this.testRunType.setJobId(this.jobTypeBuilder.getCurrentInstance().getId());
		this.testRunType = zafiraClient.startTestRun(this.testRunType).getObject();
		return this.testRunType;
	}

	public TestRunType getTestRunType()
	{
		return testRunType;
	}

	public void setTestRunType(TestRunType testRunType)
	{
		this.testRunType = testRunType;
	}
}
