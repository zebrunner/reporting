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
package com.zebrunner.reporting.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.dto.TestRunStatistics;
import com.zebrunner.reporting.service.cache.TestRunStatisticsCacheableService;
import com.zebrunner.reporting.domain.db.TestRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@Component
public class TestRunStatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunStatisticsService.class);

    private static final LoadingCache<Long, Lock> updateLocks;
    private final TestRunStatisticsCacheableService statisticsService;

    public TestRunStatisticsService(TestRunStatisticsCacheableService statisticsService) {
        this.statisticsService = statisticsService;
    }

    static {
        CacheLoader<Long, Lock> loader = new CacheLoader<>() {
            public Lock load(Long key) {
                return new ReentrantLock();
            }
        };
        updateLocks = CacheBuilder.newBuilder()
                                  .maximumSize(100000)
                                  .expireAfterWrite(150, TimeUnit.MILLISECONDS)
                                  .build(loader);
    }

    /**
     * Method contains a container needed to do a work safe with Test run statistics cache thread
     *
     * @param testRunId - test run id
     * @param statsFunction - action with cached values
     * @return testRunStatistics with value incremented
     */
    private TestRunStatistics updateStatisticsSafe(Long testRunId, Function<TestRunStatistics, TestRunStatistics> statsFunction) {
        TestRunStatistics stats = null;
        Lock lock = null;
        try {
            lock = updateLocks.get(testRunId);
            lock.lock();
            stats = statisticsService.getTestRunStatistic(testRunId);
            stats = statsFunction.apply(stats);
            stats = statisticsService.setTestRunStatistic(stats);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
        return stats;
    }

    /**
     * Increment value to statistics
     *
     * @param stats - cached test run statistic
     * @param status - test status
     * @param increment - integer (to increment - positive number, to decrement - negative number)
     * @return testRunStatistics with value incremented
     */
    private TestRunStatistics updateStatistics(TestRunStatistics stats, Status status, int increment) {
        switch (status) {
            case PASSED:
                stats.setPassed(stats.getPassed() + increment);
                break;
            case FAILED:
                stats.setFailed(stats.getFailed() + increment);
                break;
            case SKIPPED:
                stats.setSkipped(stats.getSkipped() + increment);
                break;
            case ABORTED:
                stats.setAborted(stats.getAborted() + increment);
                break;
            case IN_PROGRESS:
                stats.setInProgress(stats.getInProgress() + increment);
                break;
            default:
                break;
        }
        return stats;
    }

    /**
     * Update statistic by {@link TestRunStatistics.Action}
     *
     * @param testRunId - test run id
     * @return new statistics
     */
    public TestRunStatistics updateStatistics(Long testRunId, TestRunStatistics.Action action) {
        return updateStatisticsSafe(testRunId, stats -> {
            switch (action) {
                case MARK_AS_KNOWN_ISSUE:
                    stats.setFailedAsKnown(stats.getFailedAsKnown() + 1);
                    break;
                case REMOVE_KNOWN_ISSUE:
                    stats.setFailedAsKnown(stats.getFailedAsKnown() - 1);
                    break;
                case MARK_AS_BLOCKER:
                    stats.setFailedAsBlocker(stats.getFailedAsBlocker() + 1);
                    break;
                case REMOVE_BLOCKER:
                    stats.setFailedAsBlocker(stats.getFailedAsBlocker() - 1);
                    break;
                case MARK_AS_REVIEWED:
                    stats.setReviewed(true);
                    break;
                case MARK_AS_NOT_REVIEWED:
                    stats.setReviewed(false);
                    break;
                default:
                    break;
            }
            return stats;
        });
    }

    /**
     * Calculate new statistic by {@link TestRun getStatus}
     *
     * @param testRunId - test run id
     * @param status - new status
     * @return new statistics
     */
    public TestRunStatistics updateStatistics(Long testRunId, Status status, boolean isRerun) {
        int increment = isRerun ? -1 : 1;

        Function<TestRunStatistics, TestRunStatistics> updateFunction = testRunStatistics -> {
            boolean inProgressOrRerun = !status.equals(Status.IN_PROGRESS) && (isRerun || testRunStatistics.getInProgress() > 0);
            return inProgressOrRerun ? updateStatistics(testRunStatistics, Status.IN_PROGRESS, -increment) : testRunStatistics;
        };

        TestRunStatistics stats = updateStatisticsSafe(testRunId, updateFunction);

        if (stats != null && stats.getQueued() > 0 && (status.equals(Status.IN_PROGRESS) || status.equals(Status.ABORTED))) {
            updateStatisticsSafe(testRunId, testRunStatistics -> {
                testRunStatistics.setQueued(testRunStatistics.getQueued() - 1);
                return testRunStatistics;
            });
        }
        return updateStatisticsSafe(testRunId, statistics -> updateStatistics(statistics, status, increment));
    }

    /**
     * Calculate new statistic by {@link TestRun getStatus}
     *
     * @param testRunId - test run id
     * @param newStatus - new test status
     * @param currentStatus - current test status
     * @return new statistics
     */
    public TestRunStatistics updateStatistics(Long testRunId, Status newStatus, Status currentStatus) {
        return updateStatisticsSafe(testRunId, testRunStatistics -> {
            TestRunStatistics decrementedStats = updateStatistics(testRunStatistics, currentStatus, -1);
            return updateStatistics(decrementedStats, newStatus, 1);
        });
    }

    public TestRunStatistics updateStatistics(Long testRunId, Status status) {
        return updateStatistics(testRunId, status, false);
    }
}
