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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestSearchCriteria;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.Tag;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestArtifact;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.models.db.TestConfig;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.db.WorkItem.Type;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import com.qaprosoft.zafira.service.integration.tool.impl.TestCaseManagementService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.models.db.Status.QUEUED;
import static com.qaprosoft.zafira.models.dto.TestRunStatistics.Action.MARK_AS_BLOCKER;
import static com.qaprosoft.zafira.models.dto.TestRunStatistics.Action.MARK_AS_KNOWN_ISSUE;
import static com.qaprosoft.zafira.models.dto.TestRunStatistics.Action.REMOVE_BLOCKER;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_NOT_FOUND;

@Service
public class TestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

    private static final String ERR_MSG_TEST_NOT_FOUND = "Test with id %s can not be found";
    private static final String INV_COUNT = "InvCount";
    private static final List<String> SELENIUM_ERRORS = List.of(
            "org.openqa.selenium.remote.UnreachableBrowserException",
            "org.openqa.selenium.TimeoutException",
            "Session"
    );

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private WorkItemService workItemService;

    @Autowired
    private TestConfigService testConfigService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private TestCaseManagementService testCaseManagementService;

    @Autowired
    private TestArtifactService testArtifactService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TestRunStatisticsService testRunStatisticsService;

    @Transactional(rollbackFor = Exception.class)
    public Test startTest(Test test, List<String> jiraIds, String configXML) {

        Test existingTest = getTestById(test.getId());
        boolean rerun = existingTest != null && !Status.QUEUED.equals(test.getStatus());

        test.setStatus(Status.IN_PROGRESS);

        if (rerun) {
            test.setMessage(null);
            test.setFinishTime(null);
            test.setKnownIssue(false);
            test.setBlocker(false);
            updateTest(test);

            workItemService.deleteKnownIssuesByTestId(test.getId());
            testArtifactService.deleteTestArtifactsByTestId(test.getId());
        } else {
            TestConfig config = testConfigService.createTestConfigForTest(test, configXML);
            test.setTestConfig(config);

            boolean isNew = existingTest == null;
            if (isNew) {
                createTest(test);
                createWorkItems(test, jiraIds);
            } else {
                updateTest(test);
            }
        }

        Set<Tag> tags = saveTags(test.getId(), test.getTags());
        test.setTags(tags);

        Status statisticsStatusToUpdate = rerun ? existingTest.getStatus() : test.getStatus();
        testRunStatisticsService.updateStatistics(test.getTestRunId(), statisticsStatusToUpdate, rerun);

        return test;
    }

    private void createWorkItems(Test test, List<String> jiraIds) {
        if (jiraIds != null) {
            jiraIds.stream()
                   .filter(StringUtils::isNotEmpty)
                   .forEach(jiraId -> createWorkItem(test, jiraId));
        }
    }

    private void createWorkItem(Test test, String jiraId) {
        WorkItem workItem = new WorkItem(jiraId);
        workItem = workItemService.createOrGetWorkItem(workItem);
        testMapper.createTestWorkItem(test, workItem);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createTest(Test test) {
        validateTestFieldsLength(test);
        testMapper.createTest(test);
    }

    private void validateTestFieldsLength(Test test) {
        List<String> messageParts = new ArrayList<>();
        if (is255SymbolsLengthExceeded(test.getName())) {
            String message = String.format("name(%d)", test.getName().length());
            messageParts.add(message);
        }
        if (is255SymbolsLengthExceeded(test.getTestGroup())){
            String message = String.format("testGroup(%d)", test.getTestGroup().length());
            messageParts.add(message);
        }
        if (is255SymbolsLengthExceeded(test.getDependsOnMethods())){
            String message = String.format("dependsOnMethods(%d)", test.getDependsOnMethods().length());
            messageParts.add(message);
        }
        String builtMessage = String.join(", ", messageParts);
        if(!builtMessage.isBlank()){
            LOGGER.error(String.format("Test ID: %d, Test name: %s\nFields exceeding 255 symbols restriction: %s", test.getId(), test.getName(), builtMessage));
        }
    }

    private boolean is255SymbolsLengthExceeded(String value) {
        return value != null && value.length() > 255;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createQueuedTest(Test test, long testRunId) {
        test.setId(null);
        test.setTestRunId(testRunId);
        test.setStatus(QUEUED);
        test.setMessage(null);
        test.setKnownIssue(false);
        test.setBlocker(false);
        test.setDependsOnMethods(null);
        test.setTestConfig(null);
        test.setNeedRerun(true);
        test.setCiTestId(null);
        test.setTags(null);

        createTest(test);
    }

    @Transactional(rollbackFor = Exception.class)
    public Test finishTest(Test test, String configXML) {
        Test existingTest = getNotNullTestById(test.getId());

        Long testCaseId = existingTest.getTestCaseId();

        // Wrap all additional test finalization logic to make sure status saved
        try {
            String message = test.getMessage();
            if (message != null) {
                int messageHashcode = getMessageHashcode(message);
                existingTest.setMessageHashCode(messageHashcode);
                existingTest.setMessage(message);
            }

            // Resolve known issues
            if (Status.FAILED.equals(test.getStatus())) {
                int testMessageHashCode = getTestMessageHashCode(test.getMessage());

                WorkItem knownIssue = workItemService.getWorkItemByTestCaseIdAndHashCode(testCaseId, testMessageHashCode);
                if (knownIssue != null) {
                    boolean closed = testCaseManagementService.isEnabledAndConnected(null) && testCaseManagementService.isIssueClosed(knownIssue.getJiraId());
                    if (!closed) {
                        existingTest.setKnownIssue(true);
                        existingTest.setBlocker(knownIssue.isBlocker());
                        testMapper.createTestWorkItem(existingTest, knownIssue);

                        if (existingTest.getWorkItems() == null) {
                            existingTest.setWorkItems(new ArrayList<>());
                        }
                        existingTest.getWorkItems().add(knownIssue);

                        testRunStatisticsService.updateStatistics(test.getTestRunId(), MARK_AS_KNOWN_ISSUE);
                        if (existingTest.isBlocker()) {
                            testRunStatisticsService.updateStatistics(test.getTestRunId(), MARK_AS_BLOCKER);
                        }
                    }
                }
            }

            // Save artifacts
            Set<TestArtifact> testArtifacts = test.getArtifacts();
            if (testArtifacts != null && !testArtifacts.isEmpty()) {
                Set<TestArtifact> createdArtifacts = createTestArtifacts(testArtifacts, existingTest.getId());
                existingTest.setArtifacts(createdArtifacts);
            }

            updateTestCaseStatus(testCaseId, test.getStatus());

            Set<Tag> tags = saveTags(test.getId(), test.getTags());
            existingTest.setTags(tags);

            TestConfig config = testConfigService.createTestConfigForTest(test, configXML);
            existingTest.setTestConfig(config);

        } catch (Exception e) {
            LOGGER.error("Test finalization error: " + e.getMessage());
        } finally {
            existingTest.setFinishTime(test.getFinishTime());
            existingTest.setStatus(test.getStatus());
            existingTest.setRetry(test.getRetry());

            testMapper.updateTest(existingTest);
            testRunStatisticsService.updateStatistics(existingTest.getTestRunId(), existingTest.getStatus());
        }
        return existingTest;
    }

    private int getMessageHashcode(final String message) {
        // Handling of known Selenium errors
        String resultMessage = SELENIUM_ERRORS.stream()
                                              .filter(message::startsWith)
                                              .findFirst()
                                              .orElse(message);
        return getTestMessageHashCode(resultMessage);
    }

    private Set<TestArtifact> createTestArtifacts(Set<TestArtifact> testArtifacts, Long testId) {
        return testArtifacts.stream()
                            .filter(TestArtifact::isValid)
                            .map(artifact -> {
                                artifact.setTestId(testId);
                                return testArtifactService.createOrUpdateTestArtifact(artifact);
                            }).collect(Collectors.toSet());
    }

    @Transactional(rollbackFor = Exception.class)
    public Test skipTest(Test test) {
        test.setStatus(Status.SKIPPED);
        updateTest(test);

        testRunStatisticsService.updateStatistics(test.getTestRunId(), Status.SKIPPED);
        return test;
    }

    @Transactional(rollbackFor = Exception.class)
    public Test abortTest(Test test, String abortCause) {
        test.setStatus(Status.ABORTED);
        test.setMessage(abortCause);
        updateTest(test);

        testRunStatisticsService.updateStatistics(test.getTestRunId(), Status.ABORTED);
        return test;
    }

    @Transactional(rollbackFor = Exception.class)
    public Test changeTestStatus(long id, Status newStatus) {
        Test test = getTestById(id);
        if (test == null) {
            throw new ResourceNotFoundException(TEST_NOT_FOUND, String.format(ERR_MSG_TEST_NOT_FOUND, id));
        }
        Status oldStatus = test.getStatus();

        test.setStatus(newStatus);
        updateTest(test);
        updateTestCaseStatus(test.getTestCaseId(), test.getStatus());
        testRunService.calculateTestRunResult(test.getTestRunId(), false);

        testRunStatisticsService.updateStatistics(test.getTestRunId(), newStatus, oldStatus);
        return test;
    }

    private void updateTestCaseStatus(Long testCaseId, Status status)  {
        TestCase testCase = testCaseService.getTestCaseById(testCaseId);
        if (testCase != null) {
            testCase.setStatus(status);
            testCaseService.updateTestCase(testCase);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Test createTestWorkItems(long id, List<String> jiraIds) {
        Test test = getTestById(id);
        if (test == null) {
            throw new ResourceNotFoundException(TEST_NOT_FOUND, ERR_MSG_TEST_NOT_FOUND, id);
        }
        createWorkItems(test, jiraIds);
        return test;
    }

    @Transactional(readOnly = true)
    public Test getTestById(long id) {
        return testMapper.getTestById(id);
    }

    @Transactional(readOnly = true)
    public Test getNotNullTestById(long id) {
        Test test = getTestById(id);
        if (test == null) {
            throw new ResourceNotFoundException(TEST_NOT_FOUND, ERR_MSG_TEST_NOT_FOUND, id);
        }
        return test;
    }

    @Transactional(readOnly = true)
    public List<Test> getTestsByTestRunId(long testRunId) {
        return testMapper.getTestsByTestRunId(testRunId);
    }

    @Transactional(readOnly = true)
    public List<Test> getTestsByTestRunId(String testRunId) {
        return testRunId.matches("\\d+") ?
                testMapper.getTestsByTestRunId(Long.parseLong(testRunId)) :
                testMapper.getTestsByTestRunCiRunId(testRunId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Test updateTest(Test test) {
        validateTestFieldsLength(test);
        testMapper.updateTest(test);
        return test;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTestById(long id) {
        testMapper.deleteTestById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteQueuedTest(Test test) {
        testMapper.deleteTestByTestRunIdAndNameAndStatus(test.getTestRunId(), test.getName(), Status.QUEUED);
    }

    @Transactional(readOnly = true)
    public SearchResult<Test> searchTests(TestSearchCriteria sc) {
        List<Test> tests = testMapper.searchTests(sc);
        tests.forEach(test -> test.setArtifacts(new TreeSet<>(test.getArtifacts())));
        int count = testMapper.getTestsSearchCount(sc);

        return SearchResult.<Test>builder()
                .page(sc.getPage())
                .pageSize(sc.getPageSize())
                .sortOrder(sc.getSortOrder())
                .results(tests)
                .totalResults(count)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public WorkItem createOrUpdateTestWorkItem(long testId, WorkItem workItem) {
        Test test = getNotNullTestById(testId);

        if (Type.BUG.equals(workItem.getType())) {
            updateStatisticsOnWorkItemCreate(test, workItem);

            int messageHashCode = getTestMessageHashCode(test.getMessage());
            workItem.setHashCode(messageHashCode);
            test.setKnownIssue(true);
            test.setBlocker(workItem.isBlocker());
            updateTest(test);
        }

        linkWorkItem(test, workItem);

        testRunService.calculateTestRunResult(test.getTestRunId(), false);
        workItem = workItemService.getWorkItemById(workItem.getId());
        return workItem;
    }

    private void updateStatisticsOnWorkItemCreate(Test test, WorkItem workItem) {
        List<TestRunStatistics.Action> actions = new ArrayList<>();
        if (!test.isKnownIssue()) {
            actions.add(MARK_AS_KNOWN_ISSUE);
        }
        if (!test.isBlocker() && workItem.isBlocker()) {
            actions.add(MARK_AS_BLOCKER);
        } else if (test.isBlocker() && !workItem.isBlocker()) {
            actions.add(REMOVE_BLOCKER);
        }

        actions.forEach(action -> testRunStatisticsService.updateStatistics(test.getTestRunId(), action));
    }

    private void linkWorkItem(Test test, WorkItem workItem) {
        Type workItemType = workItem.getType();
        WorkItem attachedWorkItem = test.getWorkItemByType(workItemType);
        if (workItem.getId() != null) {
            if (attachedWorkItem != null) {
                unlinkWorkItem(attachedWorkItem);
                deleteTestWorkItemByTestIdAndWorkItemType(test.getId(), workItemType);
            } else {
                workItemService.updateWorkItem(workItem);
            }
        } else {
            workItemService.createWorkItem(workItem);
            deleteTestWorkItemByTestIdAndWorkItemType(test.getId(), workItemType);
        }

        testMapper.createTestWorkItem(test, workItem);
    }

    private void unlinkWorkItem(WorkItem workItem) {
        // Generate random hashcode to unlink known issue
        if (Type.BUG.equals(workItem.getType())) {
            workItem.setHashCode(RandomUtils.nextInt());
            workItemService.updateWorkItem(workItem);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public WorkItem createWorkItem(long testId, WorkItem workItem) {
        Test test = getNotNullTestById(testId);
        workItemService.createWorkItem(workItem);
        testMapper.createTestWorkItem(test, workItem);
        return workItemService.getWorkItemByJiraIdAndType(workItem.getJiraId(), workItem.getType());
    }

    @Transactional(rollbackFor = Exception.class)
    public TestRun deleteTestWorkItemByWorkItemIdAndTest(long workItemId, Test test) {
        test.setKnownIssue(false);
        test.setBlocker(false);
        updateTest(test);

        WorkItem workItem = workItemService.getWorkItemById(workItemId);
        unlinkWorkItem(workItem);
        deleteTestWorkItemByWorkItemIdAndTestId(workItemId, test.getId());

        testRunService.calculateTestRunResult(test.getTestRunId(), false);

        return testRunService.getTestRunById(test.getTestRunId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTestWorkItemByWorkItemIdAndTestId(long workItemId, long testId) {
        testMapper.deleteTestWorkItemByWorkItemIdAndTestId(workItemId, testId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTestWorkItemByTestIdAndWorkItemType(long testId, Type type) {
        testMapper.deleteTestWorkItemByTestIdAndWorkItemType(testId, type);
    }

    @Transactional
    // TODO: 10/22/19 refactor it
    public void updateTestRerunFlags(List<Test> tests) {
        List<Long> testIds = tests.stream()
                                  .map(Test::getId)
                                  .collect(Collectors.toList());
        testMapper.updateTestsNeedRerun(testIds, false);

        try {
            // Look #Test implements comparable so that all QUEUED, ABORTED, SKIPPED and FAILED tests go first
            Collections.sort(tests);

            List<Long> testCaseIds = tests.stream()
                                          .map(Test::getTestCaseId)
                                          .collect(Collectors.toList());

            TestCaseSearchCriteria sc = new TestCaseSearchCriteria();
            sc.setPageSize(Integer.MAX_VALUE);
            sc.setIds(testCaseIds);

            Map<Long, TestCase> testCasesById = new HashMap<>();
            Map<String, List<Long>> testCasesByMethod = new HashMap<>();

            List<TestCase> testCases = testCaseService.searchTestCases(sc).getResults();



            for (TestCase testCase : testCases) {
                testCasesByMethod.putIfAbsent(testCase.getTestMethod(), new ArrayList<>());

                testCasesById.put(testCase.getId(), testCase);
                testCasesByMethod.get(testCase.getTestMethod()).add(testCase.getId());
            }

//            List<TestCase> testCases = testCaseService.searchTestCases(sc).getResults();
//
//            Map<Long, TestCase> testCasesById = testCases.stream()
//                                                         .collect(Collectors.toMap(AbstractEntity::getId, testCase -> testCase));
//            Map<String, List<Long>> testCasesByMethod = testCases.stream()
//                                                                 .collect(Collectors.groupingBy(TestCase::getTestMethod, Collectors.mapping(AbstractEntity::getId, Collectors.toList())));


            Set<Long> testCasesToRerun = new HashSet<>();
            for (Test test : tests) {
                boolean isTestFailed = Arrays.asList(Status.FAILED, Status.SKIPPED).contains(test.getStatus()) && !test.isKnownIssue();
                boolean isTestAborted = test.getStatus().equals(Status.ABORTED);
                boolean isTestQueued = test.getStatus().equals(Status.QUEUED);
                if (isTestFailed || isTestAborted || isTestQueued) {
                    String methodName = testCasesById.get(test.getTestCaseId()).getTestMethod();

                    if (test.getName().contains(INV_COUNT)) {
                        testCasesToRerun.addAll(testCasesByMethod.get(methodName));
                    } else {
                        testMapper.updateTestsNeedRerun(Collections.singletonList(test.getId()), true);
                    }

                    String dependsOnMethods = test.getDependsOnMethods();
                    if (StringUtils.isNotEmpty(dependsOnMethods)) {
                        for (String method : dependsOnMethods.split(" ")) {
                            testCasesToRerun.addAll(testCasesByMethod.get(method));
                        }
                    }

                }
            }

            testIds = tests.stream()
                           .filter(test -> testCasesToRerun.contains(test.getTestCaseId()))
                           .map(Test::getId)
                           .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Unable to calculate rurun flags", e);
        } finally {
            testMapper.updateTestsNeedRerun(testIds, true);
        }
    }

    /**
     * Adds set of tags to specified test.
     * 
     * @param testId - test ID for tags
     * @param tags - set of tags
     */
    @Transactional(rollbackFor = Exception.class)
    public Set<Tag> saveTags(Long testId, Set<Tag> tags) {
        if (!CollectionUtils.isEmpty(tags)) {
            tags = tagService.createTags(tags);
            Set<Tag> tagsToAdd = tags.stream()
                                     .filter(tag -> tag.getId() != null && tag.getId() != 0)
                                     .collect(Collectors.toSet());

            if (!tagsToAdd.isEmpty()) {
                deleteTags(testId);
                testMapper.addTags(testId, tagsToAdd);
            }
        }
        return getNotNullTestById(testId).getTags();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTags(Long testId) {
        testMapper.deleteTags(testId);
    }

    public int getTestMessageHashCode(String message) {
        int hashCode = 0;
        if (message != null) {
            message = message.replaceAll("\\d+", "*")
                             .replaceAll("\\[.*]", "*");
            hashCode = message.hashCode();
        }
        return hashCode;
    }
}
