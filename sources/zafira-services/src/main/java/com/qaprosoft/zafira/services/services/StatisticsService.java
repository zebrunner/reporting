/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
	 * @param testRunId - to get statistic for
	 * @return test run statistics
	 */
	@Cacheable(value = "testRunStatistics", key = "#testRunId")
	@Transactional(readOnly = true)
	public TestRunStatistics getTestRunStatistic(Long testRunId)
	{
		return testRunMapper.getTestRunStatistics(testRunId);
	}
}
