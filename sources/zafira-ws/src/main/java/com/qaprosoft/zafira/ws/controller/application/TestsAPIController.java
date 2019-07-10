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
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestSearchCriteria;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestArtifact;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.db.WorkItem.Type;
import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.models.push.TestRunStatisticPush;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.services.application.TestArtifactService;
import com.qaprosoft.zafira.services.services.application.TestMetricService;
import com.qaprosoft.zafira.services.services.application.TestRunService;
import com.qaprosoft.zafira.services.services.application.TestService;
import com.qaprosoft.zafira.services.services.application.WorkItemService;
import com.qaprosoft.zafira.services.services.application.cache.StatisticsService;
import com.qaprosoft.zafira.services.services.application.integration.impl.JiraService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.rcarz.jiraclient.Issue;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Api("Tests API")
@RequestMapping(path = "api/tests", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestsAPIController extends AbstractController {

    @Autowired
    private Mapper mapper;

    @Autowired
    private TestService testService;

    @Autowired
    private TestArtifactService testArtifactService;

    @Autowired
    private TestMetricService testMetricService;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private WorkItemService workItemService;

    @Autowired
    private JiraService jiraService;

    @Autowired
    private SimpMessagingTemplate websocketTemplate;

    @Autowired
    private StatisticsService statisticsService;

    @ResponseStatusDetails
    @ApiOperation(value = "Start test", nickname = "startTest", httpMethod = "POST", response = TestType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping()
    public TestType startTest(@Valid @RequestBody TestType t)
            {
        Test test = testService.startTest(mapper.map(t, Test.class), t.getWorkItems(), t.getConfigXML());
        websocketTemplate.convertAndSend(
                getStatisticsWebsocketPath(),
                new TestRunStatisticPush(statisticsService.getTestRunStatistic(test.getTestRunId())));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
        return mapper.map(test, TestType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Finish test", nickname = "finishTest", httpMethod = "POST", response = TestType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/{id}/finish")
    public TestType finishTest(@ApiParam(value = "Test ID", required = true) @PathVariable("id") long id, @RequestBody TestType t)
            {
        t.setId(id);
        Test test = testService.finishTest(mapper.map(t, Test.class), t.getConfigXML());
        testService.deleteQueuedTest(test);
        testMetricService.createTestMetrics(t.getId(), t.getTestMetrics());
        websocketTemplate.convertAndSend(
                getStatisticsWebsocketPath(),
                new TestRunStatisticPush(statisticsService.getTestRunStatistic(test.getTestRunId())));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
        return mapper.map(test, TestType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update test", nickname = "updateTest", httpMethod = "PUT", response = Test.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_TESTS')")
    @PutMapping()
    public Test updateTest(@RequestBody Test test) {
        Test updatedTest = testService.changeTestStatus(test.getId(), test.getStatus());

        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService
                .getTestRunStatistic(updatedTest.getTestRunId())));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(updatedTest.getTestRunId()), new TestPush(updatedTest));
        TestRun testRun = testRunService.getTestRunById(updatedTest.getTestRunId());
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));
        return updatedTest;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create test work items", nickname = "createTestWorkItems", httpMethod = "POST", response = TestType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/{id}/workitems")
    public TestType createTestWorkItems(
            @ApiParam(value = "Work item ID", required = true) @PathVariable("id") long id,
            @RequestBody List<String> workItems) {
        return mapper.map(testService.createTestWorkItems(id, workItems), TestType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete test by id", nickname = "deleteTest", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @DeleteMapping("/{id}")
    public void deleteTest(@ApiParam(value = "Test ID", required = true) @PathVariable("id") long id) {
        testService.deleteTestById(id);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Search tests", nickname = "searchTests", httpMethod = "POST", response = SearchResult.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/search")
    public SearchResult<Test> searchTests(@RequestBody TestSearchCriteria sc) {
        return testService.searchTests(sc);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get test case work items by type", nickname = "getTestCaseWorkItemsByType", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/{id}/workitem/{type}")
    public List<WorkItem> getTestCaseWorkItemsByType(
            @ApiParam(value = "Test ID", required = true) @PathVariable("id") long id,
            @PathVariable("type") String type) {
        List<WorkItem> workItems = new ArrayList<>();
        Test test = testService.getTestById(id);
        if (test != null) {
            workItems = workItemService.getWorkItemsByTestCaseIdAndType(test.getTestCaseId(), Type.valueOf(type));
        }
        return workItems;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create or update test work item", nickname = "createOrUpdateTestWorkItem", httpMethod = "POST", response = WorkItem.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/{id}/workitem")
    public WorkItem createOrUpdateTestWorkItem(
            @ApiParam(value = "Test ID", required = true) @PathVariable("id") long id,
            @RequestBody WorkItem workItem) {
        if (getPrincipalId() > 0) {
            workItem.setUser(new User(getPrincipalId()));
        }
        if (workItem.getType() == Type.BUG || workItem.getType() == Type.TASK) {
            workItem = testService.createOrUpdateTestWorkItem(id, workItem);
        } else {
            workItem = testService.createWorkItem(id, workItem);
        }
        Test test = testService.getTestById(id);

        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService
                .getTestRunStatistic(test.getTestRunId())));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

        TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));

        return workItem;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update test known issue", nickname = "updateTestKnownIssue", httpMethod = "PUT", response = WorkItem.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PutMapping("/{id}/issues")
    public WorkItem updateTestKnownIssue(
            @ApiParam(value = "Test ID", required = true) @PathVariable("id") long id,
            @RequestBody WorkItem workItem) {
        Test test = testService.getTestById(id);
        workItem.setHashCode(testService.getTestMessageHashCode(test.getMessage()));
        return workItemService.updateWorkItem(workItem);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete test work item", nickname = "deleteTestWorkItem", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @DeleteMapping("/{testId}/workitem/{workItemId}")
    public void deleteTestWorkItem(
            @PathVariable("workItemId") long workItemId,
            @PathVariable("testId") long testId) {
        Test test = testService.getTestById(testId);
        WorkItem workItem = workItemService.getWorkItemById(workItemId);
        if (workItem.getType() == Type.BUG) {
            testRunService.updateStatistics(test.getTestRunId(), TestRunStatistics.Action.REMOVE_KNOWN_ISSUE);
            if (test.isBlocker()) {
                testRunService.updateStatistics(test.getTestRunId(), TestRunStatistics.Action.REMOVE_BLOCKER);
            }
            websocketTemplate.convertAndSend(getStatisticsWebsocketPath(),
                    new TestRunStatisticPush(statisticsService.getTestRunStatistic(test.getTestRunId())));
            websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
            TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
            websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));
        }
        testService.deleteTestWorkItemByWorkItemIdAndTest(workItemId, test);
    }

    @ApiIgnore
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/jira/{issue}")
    public Issue getJiraIssue(@PathVariable("issue") String issue) {
        return jiraService.getIssue(issue)
                .orElseThrow(() -> new ForbiddenOperationException("Unable to retrieve jira issue"));
    }

    @ApiIgnore
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/jira/connect")
    public boolean getConnectionToJira() {
        return jiraService.isEnabledAndConnected();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Add test artifact", nickname = "addTestArtifact", httpMethod = "POST")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/{id}/artifacts")
    public void addTestArtifact(
            @ApiParam(value = "Test ID", required = true) @PathVariable("id") long id,
            @RequestBody TestArtifactType artifact) {
        artifact.setTestId(id);
        testArtifactService.createOrUpdateTestArtifact(mapper.map(artifact, TestArtifact.class));
        // Updating web client with latest artifacts
        Test test = testService.getTestById(id);
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
    }

}
