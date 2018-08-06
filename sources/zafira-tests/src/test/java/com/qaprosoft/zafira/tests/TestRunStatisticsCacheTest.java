package com.qaprosoft.zafira.tests;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.tests.services.TestRunMockCacheableService;

@SuppressWarnings({"rawtypes", "unused"})
public class TestRunStatisticsCacheTest extends AbstractServiceTest<TestRunStatistics>
{

	@Autowired
	private TestRunService testRunService;

	@Autowired
	private TestRunMockCacheableService cacheableService;

	private String countType;
	private String runType;
	private Object[] testRunIds;

	@Factory(dataProvider = "getFlows")
	public TestRunStatisticsCacheTest(String countType, String runType)
	{
		super(TestRunStatistics.class);
		this.countType = countType;
		this.runType = runType;

	}

	@DataProvider(name = "getFlows")
	public static Object[][] getFlows()
	{
		return new Object[][]
				{
						{
								"ONE", "PARALLEL"
						},
						{
								"ONE", "SEQUENT"
						},
						{
								"MANY", "PARALLEL"
						},
						{
								"MANY", "SEQUENT"
						}
				};
	}

	@DataProvider(name = "getStatisticsMethods")
	public Object[][] getUpdateStatisticsMethods()
	{
		return new Object[][]
				{
						{
								(Function<Object[], TestRunStatistics>) functionParams -> testRunService.updateStatistics((Long) functionParams[0], (Status) functionParams[1], (Boolean) functionParams[2]), new Class[] { Long.class, Status.class, Boolean.class }
						},
						{
								(Function<Object[], TestRunStatistics>) functionParams -> testRunService.updateStatistics((Long) functionParams[0], (Status) functionParams[1]), new Class[] { Long.class, Status.class }
						},
						{
								(Function<Object[], TestRunStatistics>) functionParams -> testRunService.updateStatistics((Long) functionParams[0], (Status) functionParams[1], (Status) functionParams[2]), new Class[] { Long.class, Status.class, Status.class }
						},
						{
								(Function<Object[], TestRunStatistics>) functionParams -> testRunService.updateStatistics((Long) functionParams[0], (TestRunStatistics.Action) functionParams[1]), new Class[] { Long.class, TestRunStatistics.Action.class }
						}
				};
	}

	@BeforeMethod
	public void setup()
	{
		Assert.assertNotNull(getCache(), "Test run statistics cache does not exist");
		Object[] testRunIds = cacheableService.getStatistics().entrySet().stream().map(Map.Entry::getKey).toArray();
		this.testRunIds = this.countType.equals("ONE") ? new Object[] { testRunIds[RandomUtils.nextInt(0, testRunIds.length)] } : testRunIds;
	}

	@Test(groups = {"acceptance", "cache"}, dataProvider = "getStatisticsMethods")
	public void verifyTestRunStatisticsCacheUpdateTest(Function<Object[], TestRunStatistics> updateTestRunStatisticsFunction, Class[] paramClasses)
	{

		LOGGER.info("Test was started with count type: " + countType + " and run type: " + runType);
		int threadCount = RandomUtils.nextInt(2, 15);
		LOGGER.info("Update test run statistics function with updateTestRunStatistics(" + getMethodPlaceholder(paramClasses) + ")");
		LOGGER.info("Available values of test run id are: " + Arrays.toString(testRunIds));
		LOGGER.info("Update statistics cache method will be called " + threadCount + " times.");

		// Requests to update statistics method
		TestRunStatistics[] testRunStatistics = generateTestRunUpdateStatisticsSuppliers(threadCount, testRunIds, paramClasses, updateTestRunStatisticsFunction);
		runUpdateStatisticsParallelRunnables(this.runType.equals("PARALLEL"), testRunStatistics);
	}

	/**
	 * Build placeholder for logger like Class1, Class2....
	 * @param parameters - class values of function parameters
	 * @return - built placeholder
	 */
	private String getMethodPlaceholder(Class<?>... parameters)
	{
		return Arrays.stream(parameters).map(Class::getSimpleName).collect(Collectors.toList()).toString();
	}

	/**
	 * Runs of update test run statistics function
	 * @param parallel - parallel runs if true
	 * @param testRunStatistics - requests of runs
	 */
	private void runUpdateStatisticsParallelRunnables(boolean parallel, TestRunStatistics... testRunStatistics)
	{
		IntStream run = parallel ? IntStream.range(0, testRunStatistics.length).parallel() : IntStream.range(0, testRunStatistics.length);
		try {
			//run.forEach(i -> runUpdateStatisticsRunnable(i, testRunStatistics[i]));
			runUpdateStatisticsRunnable(0, testRunStatistics[testRunStatistics.length - 1]);
		}
		finally {
			run.close();
		}
	}

	/**
	 * Run item of update test run statistics function
	 * @param index - index of run
	 * @param testRunStatistics - run item
	 */
	private void runUpdateStatisticsRunnable(int index, TestRunStatistics testRunStatistics)
	{
		LOGGER.info("Runnable " + index + " is prepared to run.");
		Long testRunId = testRunStatistics.getTestRunId();
		verifyCacheValues(testRunStatistics);
		LOGGER.info("Runnable " + index + " was ran.");
	}

	private void verifyCacheValues(TestRunStatistics trs)
	{
		TestRunStatistics testRunStatistics = getCachedValue(trs.getTestRunId());
		Assert.assertNotNull(testRunStatistics, "Test run statistics cache does not exist");

		Assert.assertEquals(testRunStatistics.getPassed(), trs.getPassed(), "Passed count is not actual");
		Assert.assertEquals(testRunStatistics.getFailed(), trs.getFailed(), "Failed count is not actual");
		Assert.assertEquals(testRunStatistics.getSkipped(), trs.getSkipped(), "Skipped count is not actual");
		Assert.assertEquals(testRunStatistics.getFailedAsKnown(), trs.getFailedAsKnown(), "Failed as known issue count is not actual");
		Assert.assertEquals(testRunStatistics.getFailedAsBlocker(), trs.getFailedAsBlocker(), "Failed as blocker count is not actual");
		Assert.assertEquals(testRunStatistics.getAborted(), trs.getAborted(), "Aborted count is not actual");
		Assert.assertEquals(testRunStatistics.getInProgress(), trs.getInProgress(), "In progress count is not actual");
		Assert.assertEquals(testRunStatistics.getQueued(), trs.getQueued(), "Queued count is not actual");
	}

	/**
	 * Method generates random parameters
	 * @param count - count of suppliers - threads
	 * @param testRunIds - test run ids from db
	 * @param paramClasses - update statistics method parameter classes
	 * @param updateTestRunStatisticsFunction - update statistics function
	 * @return suppliers of generated parameters
	 */
	private TestRunStatistics[] generateTestRunUpdateStatisticsSuppliers(int count, Object[] testRunIds, Class[] paramClasses, Function<Object[], TestRunStatistics> updateTestRunStatisticsFunction)
	{
		TestRunStatistics[] result = new TestRunStatistics[count];
		IntStream.range(0, count).forEach(index -> {
			Object[] params = new Object[paramClasses.length];
			IntStream.range(0, paramClasses.length).forEach(i ->
			{
				Class<?> clazz = paramClasses[i];
				params[i] = getRandomClassValue(clazz, testRunIds);
				LOGGER.info("Parameter " + i + " was generated: " + params[i] + " for class " + clazz.getSimpleName());
			});
			Supplier<TestRunStatistics> supplier = () -> updateTestRunStatisticsFunction.apply(params);
			result[index] = supplier.get();
		});
		return result;
	}
}
