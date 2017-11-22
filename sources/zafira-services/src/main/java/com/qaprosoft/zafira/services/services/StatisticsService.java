package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service uses for test runs caching <h1>(isolation need for Spring target proxy objects)</h1>
 */
@Service
public class StatisticsService
{

	@Autowired
	private TestRunMapper testRunMapper;

	/**
	 * Get and put (unique) into cache test run statistic by {@link com.qaprosoft.zafira.models.db.TestRun} id key
	 * @param testRunId
	 * @return
	 */
	@Cacheable(value = "testRunStatistics", key = "#testRunId")
	@Transactional(readOnly = true)
	public TestRunStatistics getTestRunStatistic(Long testRunId)
	{
		return testRunMapper.getTestRunStatistics(testRunId);
	}
}
