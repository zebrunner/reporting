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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestRunMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.JobSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestConfig;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.models.dto.QueueTestRunParamsType;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.service.email.TestRunResultsEmail;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.IntegrationException;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import com.qaprosoft.zafira.service.integration.IntegrationService;
import com.qaprosoft.zafira.service.integration.tool.impl.AutomationServerService;
import com.qaprosoft.zafira.service.util.DateTimeUtil;
import com.qaprosoft.zafira.service.util.FreemarkerUtil;
import com.qaprosoft.zafira.service.util.URLResolver;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.models.db.Status.ABORTED;
import static com.qaprosoft.zafira.models.db.Status.FAILED;
import static com.qaprosoft.zafira.models.db.Status.IN_PROGRESS;
import static com.qaprosoft.zafira.models.db.Status.PASSED;
import static com.qaprosoft.zafira.models.db.Status.QUEUED;
import static com.qaprosoft.zafira.models.db.Status.SKIPPED;
import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.TEST_RUN_CAN_NOT_BE_STARTED;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_RUN_NOT_FOUND;
import static com.qaprosoft.zafira.service.util.XmlConfigurationUtil.readArguments;

@Service
public class TestRunService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunService.class);

    private static final String DEFAULT_PROJECT = "UNKNOWN";
    private static final String UNDEFINED_FAILURE_COMMENT = "undefined failure";

    private static final String ERR_MSG_TEST_RUN_NOT_FOUND = "Test run with id %s can not be found";
    private static final String ERR_MSG_INVALID_TEST_RUN_INITIATED_BY_HUMAN = "Username is not specified for test run initiated by HUMAN";
    private static final String ERR_MSG_INVALID_TEST_RUN_INITIATED_BY_UPSTREAM_JOB = "Upstream job id and upstream build number are not specified for test run initiated by UPSTREAM_JOB";

    @Autowired
    private TestRunMapper testRunMapper;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private URLResolver urlResolver;

    @Autowired
    private TestService testService;

    @Autowired
    private AutomationServerService automationServerService;

    @Autowired
    private FreemarkerUtil freemarkerUtil;

    @Autowired
    private TestConfigService testConfigService;

    @Autowired
    private WorkItemService workItemService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JobsService jobsService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TestRunStatisticsService testRunStatisticsService;

    public enum FailureCause {
        UNRECOGNIZED_FAILURE("UNRECOGNIZED FAILURE"),
        COMPILATION_FAILURE("COMPILATION FAILURE"),
        TIMED_OUT("TIMED OUT"),
        BUILD_FAILURE("BUILD FAILURE"),
        ABORTED("ABORTED");

        private String cause;

        public String getCause() {
            return this.cause;
        }

        FailureCause(String cause) {
            this.cause = cause;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public TestRun createTestRun(TestRun testRun) {
        testRunMapper.createTestRun(testRun);
        return testRun;
    }

    @Transactional(readOnly = true)
    public TestRun getTestRunById(long id) {
        return testRunMapper.getTestRunById(id);
    }

    @Transactional(readOnly = true)
    public TestRun getNotNullTestRunById(long id) {
        TestRun testRun = getTestRunById(id);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }
        return testRun;
    }

    @Transactional(readOnly = true)
    public SearchResult<TestRun> searchTestRuns(TestRunSearchCriteria sc) {
        DateTimeUtil.actualizeSearchCriteriaDate(sc);
        SearchResult<TestRun> result = new SearchResult<>();
        result.setPage(sc.getPage());
        result.setPageSize(sc.getPageSize());
        result.setSortOrder(sc.getSortOrder());

        List<TestRun> testRuns = testRunMapper.searchTestRuns(sc);
        hideJobUrlsIfNeed(testRuns);
        result.setResults(testRuns);
        result.setTotalResults(testRunMapper.getTestRunsSearchCount(sc));
        return result;
    }

    @Transactional(readOnly = true)
    public TestRun getTestRunByCiRunId(String ciRunId) {
        return StringUtils.isNotEmpty(ciRunId) ? testRunMapper.getTestRunByCiRunId(ciRunId) : null;
    }

    @Transactional(readOnly = true)
    public TestRun getTestRunByIdFull(long id) {
        return testRunMapper.getTestRunByIdFull(id);
    }

    @Transactional(readOnly = true)
    public TestRun getTestRunByIdFull(String id) {
        return id.matches("\\d+") ? testRunMapper.getTestRunByIdFull(Long.parseLong(id)) : getTestRunByCiRunIdFull(id);
    }

    @Transactional(readOnly = true)
    public TestRun getTestRunByCiRunIdFull(String ciRunId) {
        return testRunMapper.getTestRunByCiRunIdFull(ciRunId);
    }

    @Transactional(readOnly = true)
    public List<TestRun> getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(Long jobId, Integer buildNumber) {
        return testRunMapper.getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(jobId, buildNumber);
    }

    @Transactional(readOnly = true)
    public Map<Long, TestRun> getLatestJobTestRuns(String env, List<Long> jobIds) {
        List<TestRun> testRuns = testRunMapper.getLatestJobTestRuns(env, jobIds);
        return testRuns.stream()
                       .collect(Collectors.toMap(run -> run.getJob().getId(), run -> run));
    }

    @Transactional(readOnly = true)
    public TestRun getLatestJobTestRunByBranchAndJobURL(String branch, String jobURL) {
        Job job = jobsService.getJobByJobURL(jobURL);
        return testRunMapper.getLatestJobTestRunByBranch(branch, job.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public TestRun updateTestRun(TestRun testRun) {
        testRunMapper.updateTestRun(testRun);
        return testRun;
    }

    @CacheEvict(value = "environments", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteTestRunById(Long id) {
        testRunMapper.deleteTestRunById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public TestRun queueTestRun(QueueTestRunParamsType testRunParams, User user) {
        // Check if testRun with provided ci_run_id exists in DB (mostly for queued and aborted without execution)
        String ciRunId = testRunParams.getCiRunId();
        TestRun testRun = getTestRunByCiRunId(ciRunId);

        if (testRun == null || QUEUED.equals(testRun.getStatus())) {
            testRun = getLatestJobTestRunByBranchAndJobURL(testRunParams.getBranch(), testRunParams.getJobUrl());
            if (testRun != null) {
                Long latestTestRunId = testRun.getId();
                testRun = createQueuedTestRun(testRun, testRunParams, user);
                final long testRunId = testRun.getId();
                List<Test> tests = testService.getTestsByTestRunId(latestTestRunId);
                tests.stream()
                     .filter(test -> !QUEUED.equals(test.getStatus()))
                     .forEach(test -> testService.createQueuedTest(test, testRunId));
            }
        } else {
            setMinimalQueuedTestRunData(testRun, testRunParams);
            updateTestRun(testRun);
        }
        return testRun;
    }

    private TestRun createQueuedTestRun(TestRun testRun, QueueTestRunParamsType testRunParams, User user) {
        String ciParentUrl = testRunParams.getCiParentUrl();
        String ciParentBuild = testRunParams.getCiParentBuild();
        String projectName = testRunParams.getProject();

        setMinimalQueuedTestRunData(testRun, testRunParams);

        if (StringUtils.isNotBlank(ciParentUrl)) {
            Job job = jobsService.createOrUpdateJobByURL(ciParentUrl, user);
            testRun.setUpstreamJob(job);
        }
        if (StringUtils.isNotBlank(ciParentBuild)) {
            testRun.setUpstreamJobBuildNumber(Integer.valueOf(ciParentBuild));
        }
        if (StringUtils.isNotBlank(projectName)) {
            Project project = projectService.getProjectByName(projectName);
            if (project == null) {
                project = projectService.getProjectByName(DEFAULT_PROJECT);
            }
            testRun.setProject(project);
        }
        testRun.setEnv(testRunParams.getEnv());
        testRun.setCiRunId(testRunParams.getCiRunId());
        testRun.setElapsed(null);
        testRun.setPlatform(null);
        testRun.setConfigXML(null);
        testRun.setConfig(null);
        testRun.setComments(null);
        testRun.setAppVersion(null);
        testRun.setReviewed(false);
        testRun.setKnownIssue(false);
        testRun.setBlocker(false);

        return createTestRun(testRun);
    }

    private void setMinimalQueuedTestRunData(TestRun testRun, QueueTestRunParamsType testRunParams) {
        // make sure to reset below fields for existing run as well
        testRun.setStatus(QUEUED);
        testRun.setStartedAt(Calendar.getInstance().getTime());
        testRun.setBuildNumber(Integer.valueOf(testRunParams.getBuildNumber()));
    }

    @Transactional(rollbackFor = Exception.class)
    public TestRun startTestRun(TestRun testRun) {
        String ciRunId = testRun.getCiRunId();
        TestRun existingTestRun = null;
        boolean isNew = true;

        if (StringUtils.isNotBlank(ciRunId)) {
            existingTestRun = testRunMapper.getTestRunByCiRunId(ciRunId);
            isNew = existingTestRun == null;
            LOGGER.debug("Looking for test run with CI ID: " + ciRunId);
            LOGGER.debug("Test run found: " + !isNew);
        } else {
            ciRunId = UUID.randomUUID().toString();
            LOGGER.debug("Generating new test run CI ID: " + ciRunId);
        }

        if (isNew) {
            testRun.setCiRunId(ciRunId);
            processStartedBy(testRun);

            if (testRun.getWorkItem() != null && !StringUtils.isEmpty(testRun.getWorkItem().getJiraId())) {
                WorkItem workItem = workItemService.createOrGetWorkItem(testRun.getWorkItem());
                testRun.setWorkItem(workItem);
            }

            setStartTestRunData(testRun);
            createTestRun(testRun);
        } else {
            existingTestRun.setBuildNumber(testRun.getBuildNumber());
            existingTestRun.setConfigXML(testRun.getConfigXML());
            existingTestRun.setTestSuite(testRun.getTestSuite());
            testRun = existingTestRun;
            // TODO: investigate if startedBy should be also copied

            setStartTestRunData(testRun);
            updateTestRun(testRun);
        }
        return testRun;
    }

    private void setStartTestRunData(TestRun testRun) {
        initTestRunWithXml(testRun);

        Integer eta = testRunMapper.getTestRunEtaByTestSuiteId(testRun.getTestSuite().getId());
        // Initialize starting time
        testRun.setStartedAt(Calendar.getInstance().getTime());
        testRun.setReviewed(false);
        testRun.setEta(eta);
        testRun.setStatus(IN_PROGRESS);
    }

    private void processStartedBy(TestRun testRun) {
        switch (testRun.getStartedBy()) {
            case HUMAN:
                if (testRun.getUser() == null) {
                    throw new IllegalOperationException(TEST_RUN_CAN_NOT_BE_STARTED, ERR_MSG_INVALID_TEST_RUN_INITIATED_BY_HUMAN);
                }
                break;
            case SCHEDULER:
                testRun.setUpstreamJobBuildNumber(null);
                testRun.setUpstreamJob(null);
                testRun.setUser(null);
                break;
            case UPSTREAM_JOB:
                if (testRun.getUpstreamJob() == null || testRun.getUpstreamJobBuildNumber() == null) {
                    throw new IllegalOperationException(TEST_RUN_CAN_NOT_BE_STARTED, ERR_MSG_INVALID_TEST_RUN_INITIATED_BY_UPSTREAM_JOB);
                }
                break;
        }
    }

    public void initTestRunWithXml(TestRun testRun) {
        if (StringUtils.isNotBlank(testRun.getConfigXML())) {
            TestConfig config = testConfigService.createTestConfigForTestRun(testRun.getConfigXML());
            String browser = config.getBrowser();
            boolean isBrowserExists = StringUtils.isNotBlank(browser) && !"*".equals(browser);
            String platform = isBrowserExists ? browser : config.getPlatform();

            testRun.setConfig(config);
            testRun.setEnv(config.getEnv());
            testRun.setAppVersion(config.getAppVersion());
            testRun.setPlatform(platform);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public TestRun abortTestRun(TestRun testRun, String abortCause) {
        if (testRun != null) {
            if (hasValidTestRunStatusToAbort(testRun.getStatus(), abortCause)) {
                List<Test> tests = testService.getTestsByTestRunId(testRun.getId());
                tests.stream()
                     .filter(test -> hasValidTestStatusToAbort(test.getStatus()))
                     .forEach(test -> testService.abortTest(test, abortCause));
            }

            testRun.setComments(abortCause);
            testRun.setStatus(Status.ABORTED);
            updateTestRun(testRun);
            calculateTestRunResult(testRun.getId(), true);
        }
        return testRun;
    }

    private boolean hasValidTestRunStatusToAbort(Status status, String abortCause) {
        return IN_PROGRESS.equals(status) || QUEUED.equals(status) && isBuildFailure(abortCause);
    }

    private boolean hasValidTestStatusToAbort(Status status) {
        return IN_PROGRESS.equals(status) || QUEUED.equals(status);
    }

    private boolean isBuildFailure(String comments) {
        boolean isCommentExists = StringUtils.isNotBlank(comments);
        boolean isCommentContainsFailure = comments.contains(FailureCause.BUILD_FAILURE.getCause()) ||
                comments.contains(FailureCause.COMPILATION_FAILURE.getCause()) ||
                comments.contains(FailureCause.UNRECOGNIZED_FAILURE.getCause());
        return isCommentExists && isCommentContainsFailure;
    }

    @Transactional(rollbackFor = Exception.class)
    public TestRun markAsReviewed(Long id, String comment) {
        TestRun testRun = getTestRunById(id);
        boolean undefinedFailureComment = UNDEFINED_FAILURE_COMMENT.equalsIgnoreCase(comment);

        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }

        testRun.setComments(comment);
        if (!undefinedFailureComment) {
            testRun.setReviewed(true);
        }

        testRun = updateTestRun(testRun);
        updateReviewedStatistics(testRun);
        return testRun;
    }

    private void updateReviewedStatistics(TestRun testRun) {
        TestRunStatistics.Action action = testRun.isReviewed() ?
                TestRunStatistics.Action.MARK_AS_REVIEWED :
                TestRunStatistics.Action.MARK_AS_NOT_REVIEWED;
        testRunStatisticsService.updateStatistics(testRun.getId(), action);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<TestRun> getTestRunsForSmartRerun(JobSearchCriteria sc) {
        return testRunMapper.getTestRunsForSmartRerun(sc);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<TestRun> executeSmartRerun(JobSearchCriteria sc, boolean rerunRequired, boolean rerunFailures) {
        if (rerunFailures && sc.getFailurePercent() == null) {
            sc.setFailurePercent(0);
        }
        List<TestRun> testRuns = getTestRunsForSmartRerun(sc);
        testRuns.forEach(testRun -> rerun(testRun, rerunRequired, rerunFailures));
        return testRuns;
    }

    private void rerun(TestRun testRun, boolean rerunRequired, boolean rerunFailures) {
        resetTestRunComments(testRun);
        if (rerunRequired) {
            automationServerService.rerunJob(testRun.getJob(), testRun.getBuildNumber(), rerunFailures);
        }
    }

    private void resetTestRunComments(TestRun testRun){
        TestRun testRunFull = getTestRunByIdFull(testRun.getId());
        if (StringUtils.isNotBlank(testRunFull.getComments())) {
            testRunFull.setComments(null);
            updateTestRun(testRunFull);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public TestRun calculateTestRunResult(long id, boolean finishTestRun) {
        TestRun testRun = getNotNullTestRunById(id);
        List<Test> tests = testService.getTestsByTestRunId(id);

        // Aborted testruns don't need status recalculation (already recalculated on abort end-point)
        if (!ABORTED.equals(testRun.getStatus())) {
            // Do not update test run status if tests are running and one clicks mark as passed or mark as known issue
            // (https://github.com/qaprosoft/zafira/issues/34)
            boolean onTestRunFinish = finishTestRun || !IN_PROGRESS.equals(testRun.getStatus());
            if (onTestRunFinish) {
                tests.stream()
                     .filter(test -> IN_PROGRESS.equals(test.getStatus()))
                     .forEach(test -> testService.skipTest(test));

                Status status = tests.size() > 0 ? PASSED : SKIPPED;
                testRun.setStatus(status);
                testRun.setKnownIssue(false);
                testRun.setBlocker(false);
                setTestRunFailureStatus(testRun, tests);
            }
        }
        if (finishTestRun && testRun.getStartedAt() != null) {
            setTestRunElapsedTime(testRun);
        }

        updateTestRun(testRun);
        testService.updateTestRerunFlags(tests);
        return testRun;
    }

    private void setTestRunElapsedTime(TestRun testRun) {
        Integer elapsed = ((Long) DateTimeUtil.toSecondsSinceDateToNow(testRun.getStartedAt())).intValue();
        // according to https://github.com/qaprosoft/zafira/issues/748
        Integer elapsedToInsert = testRun.getElapsed() != null ? testRun.getElapsed() + elapsed : elapsed;
        testRun.setElapsed(elapsedToInsert);
    }

    private void setTestRunFailureStatus(TestRun testRun, List<Test> tests) {
        boolean hasTestRunKnowIssues = tests.stream()
                                            .anyMatch(Test::isKnownIssue);
        boolean hasTestRunBlockers = tests.stream()
                                          .anyMatch(Test::isBlocker);

        testRun.setKnownIssue(hasTestRunKnowIssues);
        testRun.setBlocker(hasTestRunBlockers);

        tests.stream()
             .filter(test -> {
                 boolean isFailed = Arrays.asList(FAILED, SKIPPED).contains(test.getStatus());
                 return  (isFailed && !test.isKnownIssue()) || (isFailed && test.isBlocker());
             })
             .findFirst()
             .ifPresent(test -> testRun.setStatus(FAILED));
    }

    @Transactional(readOnly = true)
    public Map<Long, Map<String, Test>> createCompareMatrix(List<Long> testRunIds) {
        Map<Long, Map<String, Test>> testNamesWithTests = new HashMap<>();
        Set<String> testNames = new HashSet<>();

        // Store  all tests by test runs into map
        for (Long id : testRunIds) {
            List<Test> tests = testService.getTestsByTestRunId(id);
            Set<String> testRunTestNames = tests.stream()
                                                .map(Test::getName)
                                                .collect(Collectors.toSet());
            Map<String, Test> testsMap = tests.stream()
                                              .collect(Collectors.toMap(Test::getName, test -> test));

            testNames.addAll(testRunTestNames);
            testNamesWithTests.put(id, testsMap);
        }

        // Go trough map and add tests from other test runs
        for (Long id : testRunIds) {
            testNames.forEach(testName -> testNamesWithTests.get(id).putIfAbsent(testName, null));
        }
        return testNamesWithTests;
    }

    @Transactional(readOnly = true)
    public String sendTestRunResultsEmail(final String testRunId,
                                          boolean showOnlyFailures,
                                          boolean showStacktrace,
                                          final String... recipients) {
        TestRun testRun = getTestRunByIdFull(testRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, testRunId);
        }
        List<Test> tests = testService.getTestsByTestRunId(testRunId);
        return sendTestRunResultsNotification(testRun, tests, showOnlyFailures, showStacktrace, recipients);
    }

    private String sendTestRunResultsNotification(final TestRun testRun,
                                                  final List<Test> tests,
                                                  boolean showOnlyFailures,
                                                  boolean showStacktrace,
                                                  final String... recipients) {

        TestRunResultsEmail email = buildTestRunResultEmail(testRun, tests);

        String jiraUrl = getJiraUrl();
        email.setJiraURL(jiraUrl);
        email.setShowOnlyFailures(showOnlyFailures);
        email.setShowStacktrace(showStacktrace);
        email.setSuccessRate(calculateSuccessRate(testRun));
        String emailContent = null;
        try {
            emailContent = emailService.sendEmail(email, recipients);
        } catch (IntegrationException e) {
            LOGGER.error("Unable to send results email " + e);
        }
        return emailContent;
    }

    /**
     * Generates a string with test run html report
     * @param id - test run id or test run ciRunId to find
     * @return built test run report or null if test run is not found
     */
    @Transactional(readOnly = true)
    public String exportTestRunHTML(final String id) {
        String result = null;
        TestRun testRun = getTestRunByIdFull(id);
        if (testRun != null) {
            List<Test> tests = testService.getTestsByTestRunId(id);

            TestRunResultsEmail email = buildTestRunResultEmail(testRun, tests);

            String jiraUrl = getJiraUrl();
            email.setJiraURL(jiraUrl);
            email.setSuccessRate(calculateSuccessRate(testRun));
            result = freemarkerUtil.getFreeMarkerTemplateContent(email.getType().getTemplateName(), email);
        } else {
            LOGGER.error(String.format(ERR_MSG_TEST_RUN_NOT_FOUND, id));
        }
        return result;
    }

    private TestRunResultsEmail buildTestRunResultEmail(TestRun testRun, List<Test> tests) {
        Configuration configuration = readArguments(testRun.getConfigXML());
        // Forward from API to Web
        Argument zafiraServiceUrlArgument = new Argument("zafira_service_url", urlResolver.buildWebURL());
        configuration.getArg().add(zafiraServiceUrlArgument);

        tests.forEach(test -> test.setArtifacts(new TreeSet<>(test.getArtifacts())));

        return new TestRunResultsEmail(configuration, testRun, tests);
    }

    private String getJiraUrl() {
        // THIS IS VERY BAD AND NEEDS TO BE FIXED IN FUTURE
        // this approach ignores if JIRA enabled at all
        Integration jira = integrationService.retrieveDefaultByIntegrationTypeName("JIRA");
        return jira.getAttributeValue("JIRA_URL").orElse("");
    }

    public static int calculateSuccessRate(TestRun testRun) {
        int total = testRun.getPassed() + testRun.getFailed() + testRun.getSkipped();
        double rate = (double) testRun.getPassed() / (double) total;
        return total > 0 ? (new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(100))).intValue() : 0;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "environments", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #result", condition = "#result != null && #result.size() != 0")
    public List<String> getEnvironments() {
        return testRunMapper.getEnvironments();
    }

    @Transactional(readOnly = true)
    public List<String> getPlatforms() {
        return testRunMapper.getPlatforms();
    }

    private void hideJobUrlsIfNeed(List<TestRun> testRuns) {
        testRuns.stream()
                .filter(testRun -> !automationServerService.isUrlVisibilityEnabled(testRun.getJob().getAutomationServerId()))
                .forEach(testRun -> testRun.getJob().setJobURL(null));
    }
}
