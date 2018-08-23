package com.qaprosoft.zafira.tests.services.api.builders;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import com.qaprosoft.zafira.models.db.application.Status;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestType;

public class TestTypeBuilder extends AbstractTypeBuilder<TestType>
{

	private IModelBuilder<TestRunType> testRunTypeBuilder;
	private IModelBuilder<TestCaseType> testCaseTypeBuilder;

	private TestType testType = new TestType()
	{
		private static final long serialVersionUID = 4768791357944856470L;
		{
			try
			{
				setConfigXML(FileUtils.readFileToString(new File(this.getClass().getResource("/api_data/testruns/config.xml").getPath())));
			} catch (IOException e)
			{
				LOGGER.error(e.getMessage());
			}
			setName("testMethod");
			setStatus(Status.IN_PROGRESS);
			setTestArgs(this.toString());
			setTestGroup("com.qaprosoft.com.testgroup");
			setStartTime(System.currentTimeMillis());
			setDependsOnMethods("");
			setWorkItems(Arrays.asList("TEST#" + getNextRandomInt()));
		}
	};

	public TestTypeBuilder(IModelBuilder<TestRunType> testRunTypeBuilder, IModelBuilder<TestCaseType> testCaseTypeBuilder)
	{
		this.testRunTypeBuilder = testRunTypeBuilder;
		this.testCaseTypeBuilder = testCaseTypeBuilder;
	}

	public TestTypeBuilder(IModelBuilder<TestRunType> testRunTypeBuilder)
	{
		this.testRunTypeBuilder = testRunTypeBuilder;
		this.testCaseTypeBuilder = new TestCaseTypeBuilder();
	}

	public TestTypeBuilder(IModelBuilder<TestRunType> testRunTypeBuilder, TestType testType)
	{
		this.testRunTypeBuilder = testRunTypeBuilder;
		this.testCaseTypeBuilder = new TestCaseTypeBuilder();
		if(testType != null)
		{
			this.testType.setConfigXML(testType.getConfigXML());
			this.testType.setName(testType.getName());
			this.testType.setStatus(testType.getStatus());
			this.testType.setTestArgs(testType.getTestArgs());
			this.testType.setTestGroup(testType.getTestGroup());
			this.testType.setDependsOnMethods(testType.getDependsOnMethods());
			this.testType.setWorkItems(testType.getWorkItems());
		}
	}

	@Override
	public TestType getInstance()
	{
		return this.testType;
	}

	@Override
	public TestType register()
	{
		this.testType.setTestRunId(this.testRunTypeBuilder.getCurrentInstance().getId());
		this.testType.setTestCaseId(this.testCaseTypeBuilder.getCurrentInstance().getId());
		this.testType = zafiraClient.startTest(this.testType).getObject();
		return this.testType;
	}

	public TestType getTestType()
	{
		return testType;
	}

	public void setTestType(TestType testType)
	{
		this.testType = testType;
	}
}
