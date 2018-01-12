package com.qaprosoft.zafira.tests.services.api;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.tests.services.api.builders.TestRunTypeBuilder;
import com.qaprosoft.zafira.tests.services.api.builders.TestTypeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TestAPIService extends AbstractAPIService
{

	public List<TestType> createTests(TestRunTypeBuilder testRunTypeBuilder, int testCount, Status finishStatus)
	{
		List<TestType> testTypes = new ArrayList<>();
		if (testCount > 0)
		{
			IntStream.iterate(0, i -> i++).limit(testCount).forEach(index -> {
				TestTypeBuilder testTypeBuilder = new TestTypeBuilder(testRunTypeBuilder);
				testTypes.add(createTest(testTypeBuilder, finishStatus));
			});
		}
		return testTypes;
	}

	public TestType createTest(TestTypeBuilder testTypeBuilder, Status finishStatus)
	{
		TestType testType = testTypeBuilder.getCurrentInstance();
		if (!finishStatus.equals(Status.IN_PROGRESS))
		{
			testType.setStatus(finishStatus);
			testType.setFinishTime(System.currentTimeMillis() + random.nextInt(1000000));
			testType = ZAFIRA_CLIENT.finishTest(testType).getObject();
		}
		return testType;
	}
}
