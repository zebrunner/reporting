package com.qaprosoft.zafira.tests.services.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.qaprosoft.zafira.models.db.application.Status;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.tests.services.api.builders.TestRunTypeBuilder;
import com.qaprosoft.zafira.tests.services.api.builders.TestTypeBuilder;

public class TestAPIService extends AbstractAPIService
{

	private TestType testType;

	public TestAPIService()
	{
	}

	public TestAPIService(TestType testType)
	{
		this.testType = testType;
	}

	public List<TestType> createTests(TestRunTypeBuilder testRunTypeBuilder, int testCount, Status finishStatus, int failedMessageLenght)
	{
		List<TestType> testTypes = new ArrayList<>();
		if (testCount > 0)
		{
			IntStream.range(0, testCount).forEach(index -> {
				TestTypeBuilder testTypeBuilder = new TestTypeBuilder(testRunTypeBuilder, this.testType);
				testTypes.add(createTest(testTypeBuilder, finishStatus, failedMessageLenght));
			});
		}
		return testTypes;
	}

	public TestType createTest(TestTypeBuilder testTypeBuilder, Status finishStatus, int failedMessageLength)
	{
		TestType testType = testTypeBuilder.getCurrentInstance();
		if(finishStatus.equals(Status.FAILED) || finishStatus.equals(Status.SKIPPED))
			testType.setMessage(testTypeBuilder.getNextRandomString(failedMessageLength));
		if (! finishStatus.equals(Status.IN_PROGRESS))
		{
			testType.setStatus(finishStatus);
			testType.setFinishTime(System.currentTimeMillis() + random.nextInt(1000000));
			testType = ZAFIRA_CLIENT.finishTest(testType).getObject();
		}
		return testType;
	}


}
