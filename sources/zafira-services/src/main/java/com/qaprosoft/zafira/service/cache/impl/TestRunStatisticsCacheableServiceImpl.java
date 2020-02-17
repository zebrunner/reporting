/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.service.cache.impl;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestRunMapper;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.service.cache.TestRunStatisticsCacheableService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service uses for test runs caching
 * <h1>(isolation need for Spring target proxy objects)</h1>
 */
@Component
public class TestRunStatisticsCacheableServiceImpl implements TestRunStatisticsCacheableService {

    private static final String TEST_RUN_STATISTICS_CACHE_NAME = "testRunStatistics";

    private final TestRunMapper testRunMapper;

    public TestRunStatisticsCacheableServiceImpl(TestRunMapper testRunMapper) {
        this.testRunMapper = testRunMapper;
    }

    /**
     * Get and put (unique) into cache test run statistic by {@link TestRun} id key
     * 
     * @param testRunId - to get statistic for
     * @return test run statistics
     */
    @Cacheable(value = TEST_RUN_STATISTICS_CACHE_NAME, key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #testRunId")
    @Transactional(readOnly = true)
    @Override
    public TestRunStatistics getTestRunStatistic(Long testRunId) {
        return testRunMapper.getTestRunStatistics(testRunId);
    }

    @CachePut(value = TEST_RUN_STATISTICS_CACHE_NAME, key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #statistic.testRunId")
    @Override
    public TestRunStatistics setTestRunStatistic(TestRunStatistics statistic) {
        return statistic;
    }

    /**
     * Evict all entries from cache in several hours after start crone expression
     * <h2>0 0 0/4 ? * * *</h2> - every 4 hours
     */
    @CacheEvict(value = TEST_RUN_STATISTICS_CACHE_NAME, allEntries = true)
    @Scheduled(cron = "0 0 0/4 ? * * *")
    @Override
    public void cacheEvict() {
    }
}
