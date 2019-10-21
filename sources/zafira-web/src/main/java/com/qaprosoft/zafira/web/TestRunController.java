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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.FilterSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.JobSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.BuildParameterType;
import com.qaprosoft.zafira.models.dto.CommentType;
import com.qaprosoft.zafira.models.dto.EmailType;
import com.qaprosoft.zafira.models.dto.QueueTestRunParamsType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.filter.FilterType;
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.models.push.TestRunStatisticPush;
import com.qaprosoft.zafira.service.FilterService;
import com.qaprosoft.zafira.service.JobsService;
import com.qaprosoft.zafira.service.LauncherCallbackService;
import com.qaprosoft.zafira.service.ProjectService;
import com.qaprosoft.zafira.service.TestRunService;
import com.qaprosoft.zafira.service.TestService;
import com.qaprosoft.zafira.service.TestSuiteService;
import com.qaprosoft.zafira.service.UserService;
import com.qaprosoft.zafira.service.cache.StatisticsService;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import com.qaprosoft.zafira.service.integration.tool.impl.AutomationServerService;
import com.qaprosoft.zafira.service.integration.tool.impl.google.models.TestRunSpreadsheetService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.service.FilterService.Template.TEST_RUN_TEMPLATE;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_RUN_NOT_FOUND;

@Api("Test runs API")
@RequestMapping(path = "api/tests/runs", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestRunController extends AbstractController {

    private static final String ERR_MSG_TEST_RUN_NOT_FOUND = "Test run with id %s can not be found";
    private static final String ERR_MSG_TEST_RUN_NOT_FOUND_BY_CI_RUN_ID = "Test run for CI run id %s can not be found";

    @Autowired
    private Mapper mapper;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private TestSuiteService testSuiteService;

    @Autowired
    private TestRunSpreadsheetService testRunSpreadsheetService;

    @Autowired
    private FilterService filterService;

    @Autowired
    private TestService testService;

    @Autowired
    private JobsService jobsService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private AutomationServerService automationServerService;

    @Autowired
    private SimpMessagingTemplate websocketTemplate;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private LauncherCallbackService launcherCallbackService;

    @ApiResponseStatuses
    @ApiOperation(value = "Start test run", nickname = "startTestRun", httpMethod = "POST", response = TestRunType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping()
    public TestRunType startTestRun(
            @RequestBody @Valid TestRunType tr,
            @RequestHeader(value = "Project", required = false) String project
    ) {
        TestRun testRun = mapper.map(tr, TestRun.class);
        testRun.setProject(projectService.getProjectByName(project));
        testRun = testRunService.startTestRun(testRun);
        TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRunFull));
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));
        return mapper.map(testRun, TestRunType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update test run config", nickname = "updateTestRun", httpMethod = "PUT", response = TestRunType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PutMapping()
    public TestRunType updateTestRun(@RequestBody TestRunType tr) {
        TestRun testRun = testRunService.getTestRunById(tr.getId());
        if (testRun == null && !StringUtils.isEmpty(tr.getConfigXML())) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, tr.getId());
        }
        testRun.setConfigXML(tr.getConfigXML());
        testRunService.initTestRunWithXml(testRun);
        testRunService.updateTestRun(testRun);
        TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRunFull));
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));
        return mapper.map(testRun, TestRunType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Finish test run", nickname = "finishTestRun", httpMethod = "POST", response = TestRunType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/{id}/finish")
    public TestRunType finishTestRun(@ApiParam(value = "Id of the test-run", required = true) @PathVariable("id") long id) {
        TestRun testRun = testRunService.calculateTestRunResult(id, true);
        TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());

        launcherCallbackService.notifyOnTestRunFinish(testRun.getCiRunId());

        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(id)));
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRunFull));
        return mapper.map(testRun, TestRunType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Abort test run", nickname = "abortTestRun", httpMethod = "POST", response = TestRunType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_TEST_RUNS')")
    @PostMapping("/abort")
    public TestRunType abortTestRun(
            @ApiParam(value = "Test run id") @RequestParam(value = "id", required = false) Long id,
            @ApiParam(value = "Test run CI id") @RequestParam(value = "ciRunId", required = false) String ciRunId,
            @RequestBody(required = false) CommentType abortCause
    ) throws UnsupportedEncodingException {
        TestRun testRun = id != null ? testRunService.getTestRunById(id) : testRunService.getTestRunByCiRunId(ciRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }
        String abortCauseDecoded = null;
        if (abortCause != null && abortCause.getComment() != null) {
            abortCauseDecoded = URLDecoder.decode(abortCause.getComment(), "UTF-8");
        }
        Status testRunStatus = testRun.getStatus();
        if (Status.IN_PROGRESS.equals(testRunStatus) || Status.QUEUED.equals(testRunStatus)) {
            testRunService.abortTestRun(testRun, abortCauseDecoded);
            for (Test test : testService.getTestsByTestRunId(testRun.getId())) {
                if (Status.ABORTED.equals(test.getStatus())) {
                    websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
                }
            }
            websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRunService.getTestRunByIdFull(testRun.getId())));
            websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));
        }
        return mapper.map(testRun, TestRunType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create queued testRun", nickname = "queueTestRun", httpMethod = "POST", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/queue")
    public TestRunType createQueuedTestRun(@RequestBody QueueTestRunParamsType queuedTestRunParams) {
        TestRun testRun = new TestRun();
        if (jobsService.getJobByJobURL(queuedTestRunParams.getJobUrl()) != null) {
            testRun = testRunService.queueTestRun(queuedTestRunParams, userService.getUserById(getPrincipalId()));
        }
        return mapper.map(testRun, TestRunType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get test run", nickname = "getTestRun", httpMethod = "GET", response = TestRunType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/{id}")
    public TestRunType getTestRun(@ApiParam(value = "Id of the test-run", required = true) @PathVariable("id") long id) {
        TestRun testRun = testRunService.getTestRunById(id);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }
        return mapper.map(testRun, TestRunType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Search test runs", nickname = "searchTestRuns", httpMethod = "GET", response = SearchResult.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/search")
    public SearchResult<TestRun> searchTestRuns(
            TestRunSearchCriteria sc,
            @RequestParam(value = "projectNames", required = false) List<String> projectNames,
            @RequestParam(value = "filterId", required = false) Long filterId
    ) {
        if (filterId != null) {
            FilterType filterType = mapper.map(filterService.getFilterById(filterId), FilterType.class);
            if (filterType != null) {
                String whereClause = filterService.getTemplate(filterType, TEST_RUN_TEMPLATE);
                sc.setFilterSearchCriteria(new FilterSearchCriteria(whereClause));
            }
        }
        if (projectNames != null) {
            List<Project> projects = projectNames.stream()
                                                 .map(name -> projectService.getProjectByName(name))
                                                 .collect(Collectors.toList());
            sc.setProjects(projects);
        }
        return testRunService.searchTestRuns(sc);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Rerun jobs", nickname = "smartRerun", httpMethod = "POST", response = SearchResult.class)
    @PostMapping("/rerun/jobs")
    public List<TestRunType> rerunJobs(
            @RequestParam(value = "doRebuild", defaultValue = "false", required = false) boolean doRebuild,
            @RequestParam(value = "rerunFailures", defaultValue = "true", required = false) boolean rerunFailures,
            @RequestBody JobSearchCriteria sc
    ) {
        List<TestRun> testRuns = testRunService.executeSmartRerun(sc, doRebuild, rerunFailures);
        return testRuns.stream()
                       .map(testRun -> mapper.map(testRun, TestRunType.class))
                       .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get test run by ci run id", nickname = "getTestRunByCiRunId", httpMethod = "GET", response = TestRunType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping()
    public TestRunType getTestRunByCiRunId(@RequestParam("ciRunId") String ciRunId) {
        TestRun testRun = testRunService.getTestRunByCiRunId(ciRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND_BY_CI_RUN_ID, ciRunId);
        }
        return mapper.map(testRun, TestRunType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get test run results by id", nickname = "getTestRunResults", httpMethod = "GET", response = java.util.List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/{id}/results")
    public List<TestType> getTestRunResults(@PathVariable("id") long id) {
        List<Test> tests = testService.getTestsByTestRunId(id);
        return tests.stream()
                    .map(test -> mapper.map(test, TestType.class))
                    .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Create compare matrix", nickname = "createCompareMatrix", httpMethod = "GET", response = Map.class)
    @GetMapping("/{ids}/compare")
    public Map<Long, Map<String, Test>> createCompareMatrix(@PathVariable("ids") String testRunIds) {
        List<Long> ids = Arrays.stream(testRunIds.split("\\+"))
                               .map(Long::valueOf)
                               .collect(Collectors.toList());

        return testRunService.createCompareMatrix(ids);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Delete test run", nickname = "deleteTestRun", httpMethod = "DELETE")
    @PreAuthorize("hasPermission('MODIFY_TEST_RUNS')")
    @DeleteMapping("/{id}")
    public void deleteTestRun(@PathVariable("id") long id) {
        testRunService.deleteTestRunById(id);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Send test run result email", nickname = "sendTestRunResultsEmail", httpMethod = "POST", response = String.class)
    @PostMapping(path = "/{id}/email", produces = MediaType.TEXT_HTML_VALUE)
    public String sendTestRunResultsEmail(
            @PathVariable("id") String id,
            @RequestBody @Valid EmailType email,
            @RequestParam(value = "filter", defaultValue = "all", required = false) String filter,
            @RequestParam(value = "showStacktrace", defaultValue = "true", required = false) boolean showStacktrace
    ) {
        String[] recipients = getRecipients(email.getRecipients());
        return testRunService.sendTestRunResultsEmail(id, "failures".equals(filter), showStacktrace, recipients);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Send failed test run result email", nickname = "sendTestRunFailureEmail", httpMethod = "POST", response = String.class)
    @PostMapping(path = "/{id}/emailFailure", produces = MediaType.TEXT_HTML_VALUE)
    public String sendTestRunFailureEmail(
            @PathVariable("id") String id,
            @RequestBody @Valid EmailType email,
            @RequestParam(name = "suiteOwner", defaultValue = "false", required = false) boolean suiteOwner,
            @RequestParam(name = "suiteRunner", defaultValue = "false", required = false) boolean suiteRunner
    ) {
        String[] recipients = getRecipients(email.getRecipients());
        if (suiteOwner) {
            Long testSuiteId = testRunService.getTestRunByCiRunIdFull(id).getTestSuite().getId();
            String suiteOwnerEmail = testSuiteService.getTestSuiteByIdFull(testSuiteId).getUser().getEmail();
            ArrayUtils.add(recipients, suiteOwnerEmail);
        }
        if (suiteRunner) {
            String suiteRunnerEmail = testRunService.getTestRunByCiRunIdFull(id).getUser().getEmail();
            ArrayUtils.add(recipients, suiteRunnerEmail);
        }

        return testRunService.sendTestRunResultsEmail(id, false, true, recipients);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Create test run results spreadsheet", nickname = "createTestRunResultSpreadsheet", httpMethod = "POST", response = String.class)
    @PostMapping(path = "/{id}/spreadsheet", produces = MediaType.TEXT_HTML_VALUE)
    public String createTestRunResultSpreadsheet(@PathVariable("id") String id, @RequestBody String recipients) {
        recipients = recipients + ";" + userService.getUserById(getPrincipalId()).getEmail();
        return testRunSpreadsheetService.createTestRunResultSpreadsheet(testRunService.getTestRunByIdFull(id), getRecipients(recipients));
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get test run result html text", nickname = "exportTestRunHTML", httpMethod = "GET", response = String.class)
    @GetMapping(path = "/{id}/export", produces = "text/html;charset=UTF-8")
    public String exportTestRunHTML(@PathVariable("id") String id) {
        return testRunService.exportTestRunHTML(id);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Mark test run as reviewed", nickname = "markTestRunAsReviewed", httpMethod = "POST")
    @PreAuthorize("hasPermission('MODIFY_TEST_RUNS')")
    @PostMapping("/{id}/markReviewed")
    public void markTestRunAsReviewed(@PathVariable("id") long id, @RequestBody @Valid CommentType comment) {
        TestRun tr = testRunService.markAsReviewed(id, comment.getComment());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(tr.getId())));
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Rerun test run", nickname = "rerunTestRun", httpMethod = "GET")
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @GetMapping("/{id}/rerun")
    public void rerunTestRun(
            @PathVariable("id") long id,
            @RequestParam(name = "rerunFailures", required = false, defaultValue = "false") boolean rerunFailures
    ) {
        TestRun testRun = testRunService.getTestRunByIdFull(id);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }
        testRun.setComments(null);
        testRun.setReviewed(false);
        testRunService.updateTestRun(testRun);
        automationServerService.rerunJob(testRun.getJob(), testRun.getBuildNumber(), rerunFailures);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Debug test run", nickname = "debugTestRun", httpMethod = "GET")
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @GetMapping("/{id}/debug")
    public void debugTestRun(@PathVariable("id") long id) {
        TestRun testRun = testRunService.getTestRunByIdFull(id);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }

        automationServerService.debugJob(testRun.getJob(), testRun.getBuildNumber());
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Abort job or debug", nickname = "abortCIJob", httpMethod = "GET")
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @GetMapping({ "/abort/ci", "/abort/debug" })
    public void abortCIJob(
            @ApiParam(value = "Test run id") @RequestParam(value = "id", required = false) Long id,
            @ApiParam(value = "Test run CI id") @RequestParam(value = "ciRunId", required = false) String ciRunId
    ) {
        TestRun testRun = id != null ? testRunService.getTestRunByIdFull(id) : testRunService.getTestRunByCiRunIdFull(ciRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }
        automationServerService.abortJob(testRun.getJob(), testRun.getBuildNumber());
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Build test run", nickname = "buildTestRun", httpMethod = "POST")
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @PostMapping("/{id}/build")
    public void buildTestRun(
            @PathVariable("id") long id,
            @RequestBody Map<String, String> jobParameters
    ) {
        TestRun testRun = testRunService.getTestRunByIdFull(id);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }
        automationServerService.buildJob(testRun.getJob(), jobParameters);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get job parameters", nickname = "getjobParameters", httpMethod = "GET", response = Map.class)
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @GetMapping("/{id}/jobParameters")
    public List<BuildParameterType> getJobParameters(@PathVariable("id") long id) {
        TestRun testRun = testRunService.getTestRunByIdFull(id);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }
        return automationServerService.getBuildParameters(testRun.getJob(), testRun.getBuildNumber());
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get environments", nickname = "getEnvironments", httpMethod = "GET", response = List.class)
    @GetMapping("/environments")
    public List<String> getEnvironments() {
        return testRunService.getEnvironments();
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get platforms", nickname = "getPlatforms", httpMethod = "GET", response = List.class)
    @GetMapping("/platforms")
    public List<String> getPlatforms() {
        return testRunService.getPlatforms();
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get console output from jenkins by test run id", nickname = "getConsoleOutput", httpMethod = "GET")
    @GetMapping("/jobConsoleOutput/{count}/{fullCount}")
    public Map<Integer, String> getConsoleOutput(
            @PathVariable("count") int count,
            @PathVariable("fullCount") int fullCount,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "ciRunId", required = false) String ciRunId
    ) {
        TestRun testRun = id != null ? testRunService.getTestRunByIdFull(id) : testRunService.getTestRunByCiRunIdFull(ciRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, id);
        }
        return automationServerService.getBuildConsoleOutput(testRun.getJob(), testRun.getBuildNumber(), count, fullCount);
    }

    private String[] getRecipients(String recipients) {
        return !StringUtils.isEmpty(recipients)
                ? recipients.trim().replaceAll(",", " ").replaceAll(";", " ").replaceAll("\\[\\]", " ").split(" ")
                : new String[] {};
    }

}
