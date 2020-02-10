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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestSearchCriteria;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestArtifact;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.db.WorkItem.Type;
import com.qaprosoft.zafira.models.dto.IssueDTO;
import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.models.push.TestRunStatisticPush;
import com.qaprosoft.zafira.service.TestArtifactService;
import com.qaprosoft.zafira.service.TestRunService;
import com.qaprosoft.zafira.service.TestService;
import com.qaprosoft.zafira.service.WorkItemService;
import com.qaprosoft.zafira.service.cache.StatisticsService;
import com.qaprosoft.zafira.service.integration.tool.impl.TestCaseManagementService;
import com.qaprosoft.zafira.web.documented.TestDocumentedController;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(path = "api/tests", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestController extends AbstractController implements TestDocumentedController {

    @Autowired
    private Mapper mapper;

    @Autowired
    private TestService testService;

    @Autowired
    private TestArtifactService testArtifactService;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private WorkItemService workItemService;

    @Autowired
    private TestCaseManagementService testCaseManagementService;

    @Autowired
    private SimpMessagingTemplate websocketTemplate;

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping()
    @Override
    public TestType startTest(@Valid @RequestBody TestType t) {
        Test test = testService.startTest(mapper.map(t, Test.class), t.getWorkItems(), t.getConfigXML());
        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
        return mapper.map(test, TestType.class);
    }

    @PostMapping("/{id}/finish")
    @Override
    public TestType finishTest(@PathVariable("id") long id, @RequestBody TestType t) {
        Test test = mapper.map(t, Test.class);
        test.setId(id);
        test = testService.finishTest(test, t.getConfigXML(), t.getTestMetrics());

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
        return mapper.map(test, TestType.class);
    }

    @PreAuthorize("hasPermission('MODIFY_TESTS')")
    @PutMapping()
    @Override
    public Test updateTest(@RequestBody Test test) {
        Test updatedTest = testService.changeTestStatus(test.getId(), test.getStatus());

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(updatedTest.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(updatedTest.getTestRunId()), new TestPush(updatedTest));

        TestRun testRun = testRunService.getTestRunById(updatedTest.getTestRunId());
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));

        return updatedTest;
    }

    @PostMapping("/{id}/workitems")
    @Override
    public TestType createTestWorkItems(
            @PathVariable("id") long id,
            @RequestBody List<String> workItems
    ) {
        Test test = testService.createTestWorkItems(id, workItems);
        return mapper.map(test, TestType.class);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteTest(@PathVariable("id") long id) {
        testService.deleteTestById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/search")
    @Override
    public SearchResult<Test> searchTests(@RequestBody TestSearchCriteria sc) {
        return testService.searchTests(sc);
    }

    @GetMapping("/{id}/workitem/{type}")
    @Override
    public List<WorkItem> getTestCaseWorkItemsByType(
            @PathVariable("id") long id,
            @PathVariable("type") Type type
    ) {
        return testService.getTestCaseWorkItems(id, type);
    }

    @PostMapping("/{id}/workitem")
    @Override
    public WorkItem linkWorkItem(@PathVariable("id") long id, @RequestBody WorkItem workItem) {
        if (getPrincipalId() > 0) {
            workItem.setUser(new User(getPrincipalId()));
        }

        workItem = testService.linkWorkItem(id, workItem);

        Test test = testService.getTestById(id);
        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

        TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));

        return workItem;
    }

    @PutMapping("/{id}/issues")
    @Override
    public WorkItem updateTestKnownIssue(
            @PathVariable("id") long id,
            @RequestBody WorkItem workItem
    ) {
        return testService.updateTestWorkItem(id, workItem);
    }

    @DeleteMapping("/{testId}/workitem/{workItemId}")
    @Override
    public void deleteTestWorkItem(@PathVariable("workItemId") long workItemId, @PathVariable("testId") long testId) {
        Test test = testService.getTestById(testId);
        WorkItem workItem = workItemService.getWorkItemById(workItemId);
        testService.deleteTestWorkItem(testId, workItemId);

        if (Type.BUG.equals(workItem.getType())) {
            TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
            websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
            websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

            TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
            websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));
        }
    }

    // // TODO: 11/1/19 get rid of jira endpoints
    @GetMapping("/jira/{issue}")
    @Override
    public IssueDTO getJiraIssue(@PathVariable("issue") String issue) {
        return testCaseManagementService.getIssue(issue);
    }

    @GetMapping("/jira/connect")
    @Override
    public boolean getConnectionToJira() {
        return testCaseManagementService.isEnabledAndConnected(null);
    }

    @PostMapping("/{id}/artifacts")
    @Override
    public void addTestArtifact(
            @PathVariable("id") long id,
            @RequestBody TestArtifactType artifact
    ) {
        artifact.setTestId(id);
        testArtifactService.createOrUpdateTestArtifact(mapper.map(artifact, TestArtifact.class));
        // Updating web client with latest artifacts
        Test test = testService.getTestById(id);
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
    }

}
