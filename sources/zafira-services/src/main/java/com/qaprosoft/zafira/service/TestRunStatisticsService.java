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
package com.qaprosoft.zafira.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.service.cache.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import static com.qaprosoft.zafira.models.db.Status.ABORTED;
import static com.qaprosoft.zafira.models.db.Status.IN_PROGRESS;

@Component
public class TestRunStatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunStatisticsService.class);

    private static final LoadingCache<Long, Lock> updateLocks;
    private final StatisticsService statisticsService;

    public TestRunStatisticsService(StatisticsService statisticsService) {
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
     * @param statisticsFunction - action with cached values
     * @return testRunStatistics with value incremented
     */
    private TestRunStatistics updateStatisticsSafe(Long testRunId, Function<TestRunStatistics, TestRunStatistics> statisticsFunction) {
        TestRunStatistics testRunStatistics = null;
        Lock lock = null;
        try {
            lock = updateLocks.get(testRunId);
            lock.lock();
            testRunStatistics = statisticsService.getTestRunStatistic(testRunId);
            testRunStatistics = statisticsFunction.apply(testRunStatistics);
            testRunStatistics = statisticsService.setTestRunStatistic(testRunStatistics);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
        return testRunStatistics;
    }

    /**
     * Increment value to statistics
     *
     * @param testRunStatistics - cached test run statistic
     * @param status - test status
     * @param increment - integer (to increment - positive number, to decrement - negative number)
     * @return testRunStatistics with value incremented
     */
    private TestRunStatistics updateStatistics(TestRunStatistics testRunStatistics, Status status, int increment) {
        switch (status) {
            case PASSED:
                testRunStatistics.setPassed(testRunStatistics.getPassed() + increment);
                break;
            case FAILED:
                testRunStatistics.setFailed(testRunStatistics.getFailed() + increment);
                break;
            case SKIPPED:
                testRunStatistics.setSkipped(testRunStatistics.getSkipped() + increment);
                break;
            case ABORTED:
                testRunStatistics.setAborted(testRunStatistics.getAborted() + increment);
                break;
            case IN_PROGRESS:
                testRunStatistics.setInProgress(testRunStatistics.getInProgress() + increment);
                break;
            default:
                break;
        }
        return testRunStatistics;
    }

    /**
     * Update statistic by {@link com.qaprosoft.zafira.models.dto.TestRunStatistics.Action}
     *
     * @param testRunId - test run id
     * @return new statistics
     */
    public TestRunStatistics updateStatistics(Long testRunId, TestRunStatistics.Action action) {
        return updateStatisticsSafe(testRunId, testRunStatistics -> {
            switch (action) {
                case MARK_AS_KNOWN_ISSUE:
                    testRunStatistics.setFailedAsKnown(testRunStatistics.getFailedAsKnown() + 1);
                    break;
                case REMOVE_KNOWN_ISSUE:
                    testRunStatistics.setFailedAsKnown(testRunStatistics.getFailedAsKnown() - 1);
                    break;
                case MARK_AS_BLOCKER:
                    testRunStatistics.setFailedAsBlocker(testRunStatistics.getFailedAsBlocker() + 1);
                    break;
                case REMOVE_BLOCKER:
                    testRunStatistics.setFailedAsBlocker(testRunStatistics.getFailedAsBlocker() - 1);
                    break;
                case MARK_AS_REVIEWED:
                    testRunStatistics.setReviewed(true);
                    break;
                case MARK_AS_NOT_REVIEWED:
                    testRunStatistics.setReviewed(false);
                    break;
                default:
                    break;
            }
            return testRunStatistics;
        });
    }

    /**
     * Calculate new statistic by {@link com.qaprosoft.zafira.models.db.TestRun getStatus}
     *
     * @param testRunId - test run id
     * @param status - new status
     * @return new statistics
     */
    public TestRunStatistics updateStatistics(Long testRunId, Status status, boolean isRerun) {
        int increment = isRerun ? -1 : 1;

        Function<TestRunStatistics, TestRunStatistics> updateFunction = testRunStatistics -> {
            boolean inProgressOrRerun = !status.equals(IN_PROGRESS) && (isRerun || testRunStatistics.getInProgress() > 0);
            return inProgressOrRerun ? updateStatistics(testRunStatistics, IN_PROGRESS, -increment) : testRunStatistics;
        };

        TestRunStatistics trs = updateStatisticsSafe(testRunId, updateFunction);

        if (trs != null && trs.getQueued() > 0 && (status.equals(IN_PROGRESS) || status.equals(ABORTED))) {
            updateStatisticsSafe(testRunId, testRunStatistics -> {
                testRunStatistics.setQueued(testRunStatistics.getQueued() - 1);
                return testRunStatistics;
            });
        }
        return updateStatisticsSafe(testRunId, testRunStatistics -> updateStatistics(testRunStatistics, status, increment));
    }

    /**
     * Calculate new statistic by {@link com.qaprosoft.zafira.models.db.TestRun getStatus}
     *
     * @param testRunId - test run id
     * @param newStatus - new test status
     * @param currentStatus - current test status
     * @return new statistics
     */
    public TestRunStatistics updateStatistics(Long testRunId, Status newStatus, Status currentStatus) {
        return updateStatisticsSafe(testRunId, testRunStatistics -> updateStatistics(
                updateStatistics(testRunStatistics, currentStatus, -1), newStatus, 1));
    }

    public TestRunStatistics updateStatistics(Long testRunId, Status status) {
        return updateStatistics(testRunId, status, false);
    }
}
