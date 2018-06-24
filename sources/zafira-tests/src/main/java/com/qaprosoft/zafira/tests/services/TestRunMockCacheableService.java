package com.qaprosoft.zafira.tests.services;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.services.services.cache.ICacheableService;

@Component(value = "testRunMockCacheableService")
public class TestRunMockCacheableService implements ICacheableService<Long, TestRunStatistics>
{

	private Map<Long, TestRunStatistics> statistics;

	public TestRunMockCacheableService()
	{
		final int count = RandomUtils.nextInt(1, 50);
		statistics = new HashMap<>();
		IntStream.range(1, count).forEach(i -> {
			final long testRunId = RandomUtils.nextInt(1, 10000);
			statistics.put(testRunId, new TestRunStatistics() {
				{
					setTestRunId(testRunId);
				}
			});
		});
	}

	@Override
	public Function<Long, TestRunStatistics> getValue()
	{
		return testRunId -> statistics.get(testRunId);
	}

	public Map<Long, TestRunStatistics> getStatistics()
	{
		return statistics;
	}
}
