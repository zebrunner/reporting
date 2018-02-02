package com.qaprosoft.zafira.tests.services.api;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.tests.models.TestRunViewType;
import com.qaprosoft.zafira.tests.services.api.builders.TestRunTypeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class TestRunAPIService extends AbstractAPIService
{

	private TestAPIService testAPIService = new TestAPIService();

	public TestRunViewType createTestRun(TestRunTypeBuilder testRunTypeBuilder, Supplier<List<TestType>> testTypeSupplier)
	{
		List<TestType> testTypes = testTypeSupplier.get();
		return new TestRunViewType(finishTestRun(testRunTypeBuilder), testTypes);
	}

	public TestRunViewType createTestRun(Integer passedCount, Integer failedCount, Integer inProgressCount, Integer skippedCount,
			Integer abortedCount)
	{
		TestRunTypeBuilder testRunTypeBuilder = new TestRunTypeBuilder();
		List<TestType> testTypes = new ArrayList<>();
		return createTestRun(testRunTypeBuilder, () -> {
			testTypes.addAll(testAPIService.createTests(testRunTypeBuilder, passedCount, Status.PASSED));
			testTypes.addAll(testAPIService.createTests(testRunTypeBuilder, failedCount, Status.FAILED));
			testTypes.addAll(testAPIService.createTests(testRunTypeBuilder, skippedCount, Status.SKIPPED));
			testTypes.addAll(testAPIService.createTests(testRunTypeBuilder, abortedCount, Status.ABORTED));
			testTypes.addAll(testAPIService.createTests(testRunTypeBuilder, inProgressCount, Status.IN_PROGRESS));
			return testTypes;
		});
	}

	public List<TestRunViewType> createTestRunsWithBounds(Integer count, Integer boundPassedCount, Integer boundFailedCount, Integer boundInProgressCount,
			Integer boundSkippedCount, Integer boundAbortedCount)
	{
		return createTestRuns(count, random.nextInt(boundPassedCount), random.nextInt(boundFailedCount),
					random.nextInt(boundInProgressCount), random.nextInt(boundSkippedCount), random.nextInt(boundAbortedCount));
	}

	public List<TestRunViewType> createTestRuns(Integer count, Integer passedCount, Integer failedCount, Integer inProgressCount,
			Integer skippedCount, Integer abortedCount)
	{
		List<TestRunViewType> result = new ArrayList<>();
		IntStream.iterate(0, i -> i++).limit(count).parallel().forEach(index -> {
			result.add(createTestRun(passedCount, failedCount, inProgressCount, skippedCount, abortedCount));
		});
		return result;
	}

	private TestRunType finishTestRun(TestRunTypeBuilder testRunTypeBuilder)
	{
		TestRunType testRunType = testRunTypeBuilder.getTestRunType();
		testRunType = ZAFIRA_CLIENT.updateTestRun(testRunType).getObject();
		testRunType = ZAFIRA_CLIENT.finishTestRun(testRunType.getId()).getObject();
		ZAFIRA_CLIENT.sendTestRunReport(testRunType.getId(), USER.getEmail(), false, false);
		return testRunType;
	}
}
