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
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.models.push.TestRunStatisticPush;
import com.qaprosoft.zafira.service.LauncherCallbackService;
import com.qaprosoft.zafira.service.TestRunService;
import com.qaprosoft.zafira.service.TestService;
import com.qaprosoft.zafira.service.cache.StatisticsService;
import com.qaprosoft.zafira.service.util.EmailUtils;
import com.qaprosoft.zafira.web.documented.TestRunDocumentedController;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.models.db.Status.IN_PROGRESS;
import static com.qaprosoft.zafira.models.db.Status.QUEUED;

@RequestMapping(path = "api/tests/runs", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestRunController extends AbstractController implements TestRunDocumentedController {

    private final TestRunService testRunService;
    private final TestService testService;
    private final SimpMessagingTemplate websocketTemplate;
    private final StatisticsService statisticsService;
    private final LauncherCallbackService launcherCallbackService;
    private final Mapper mapper;

    public TestRunController(TestRunService testRunService, TestService testService, SimpMessagingTemplate websocketTemplate,
                             StatisticsService statisticsService, LauncherCallbackService launcherCallbackService, Mapper mapper) {
        this.testRunService = testRunService;
        this.testService = testService;
        this.websocketTemplate = websocketTemplate;
        this.statisticsService = statisticsService;
        this.launcherCallbackService = launcherCallbackService;
        this.mapper = mapper;
    }

    @PostMapping()
    @Override
    public TestRunType startTestRun(
            @RequestBody @Valid TestRunType testRunType,
            @RequestHeader(value = "Project", required = false) String project
    ) {
        TestRun testRun = mapper.map(testRunType, TestRun.class);
        testRun.setProject(new Project(project));
        testRun = testRunService.startTestRun(testRun);

        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));

        return mapper.map(testRun, TestRunType.class);
    }

    @PutMapping()
    @Override
    public TestRunType updateTestRun(@Valid @RequestBody TestRunType testRunType) {
        TestRun testRun = mapper.map(testRunType, TestRun.class);
        testRunService.updateTestRunWithXml(testRun);

        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));

        return mapper.map(testRun, TestRunType.class);
    }

    @PostMapping("/{id}/finish")
    @Override
    public TestRunType finishTestRun(@PathVariable("id") long id) {
        TestRun testRun = testRunService.calculateTestRunResult(id, true);
        TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());

        launcherCallbackService.notifyOnTestRunFinish(testRun.getCiRunId());

        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(id)));
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRunFull));

        return mapper.map(testRun, TestRunType.class);
    }

    @PreAuthorize("hasPermission('MODIFY_TEST_RUNS')")
    @PostMapping("/abort")
    @Override
    public TestRunType abortTestRun(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "ciRunId", required = false) String ciRunId,
            @RequestBody(required = false) CommentType abortCause
    ) {
        TestRun testRun = TestRun.builder()
                                 .id(id)
                                 .ciRunId(ciRunId)
                                 .build();

        testRun = testRunService.abortTestRun(testRun, abortCause);

        if (List.of(IN_PROGRESS, QUEUED).contains(testRun.getStatus())) {
            List<Test> tests = testService.getTestsByTestRunId(testRun.getId());
            tests.stream()
                 .filter(test -> Status.ABORTED.equals(test.getStatus()))
                 .forEach(test -> websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test)));

            websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRunService.getTestRunByIdFull(testRun.getId())));
            websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));
        }
        return mapper.map(testRun, TestRunType.class);
    }

    @PostMapping("/queue")
    @Override
    public TestRunType createQueuedTestRun(@RequestBody QueueTestRunParamsType queuedTestRunParams) {
        TestRun testRun = testRunService.queueTestRun(queuedTestRunParams, getPrincipalId());
        return mapper.map(testRun, TestRunType.class);
    }

    @GetMapping("/{id}")
    @Override
    public TestRunType getTestRun(@PathVariable("id") long id) {
        TestRun testRun = testRunService.getNotNullTestRunById(id);
        return mapper.map(testRun, TestRunType.class);
    }

    @GetMapping("/search")
    @Override
    public SearchResult<TestRun> searchTestRuns(
            TestRunSearchCriteria sc,
            @RequestParam(value = "projectNames", required = false) List<String> projectNames,
            @RequestParam(value = "filterId", required = false) Long filterId
    ) throws IOException {
        return testRunService.search(sc, projectNames, filterId);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Rerun jobs", nickname = "smartRerun", httpMethod = "POST", response = SearchResult.class)
    @PostMapping("/rerun/jobs")
    @Override
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

    @GetMapping()
    @Override
    public TestRunType getTestRunByCiRunId(@RequestParam("ciRunId") String ciRunId) {
        TestRun testRun = testRunService.getNotNullTestRunByCiRunId(ciRunId);
        return mapper.map(testRun, TestRunType.class);
    }

    @GetMapping("/{id}/results")
    @Override
    public List<TestType> getTestRunResults(@PathVariable("id") long id) {
        List<Test> tests = testService.getTestsByTestRunId(id);
        return tests.stream()
                    .map(test -> mapper.map(test, TestType.class))
                    .collect(Collectors.toList());
    }

    @GetMapping("/{ids}/compare")
    @Override
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
    @Override
    public void deleteTestRun(@PathVariable("id") long id) {
        testRunService.deleteTestRunById(id);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Send test run result email", nickname = "sendTestRunResultsEmail", httpMethod = "POST", response = String.class)
    @PostMapping(path = "/{id}/email", produces = MediaType.TEXT_HTML_VALUE)
    @Override
    public String sendTestRunResultsEmail(
            @PathVariable("id") String id,
            @RequestBody @Valid EmailType email,
            @RequestParam(value = "filter", defaultValue = "all", required = false) String filter,
            @RequestParam(value = "showStacktrace", defaultValue = "true", required = false) boolean showStacktrace
    ) {
        String[] recipients = EmailUtils.obtainRecipients(email.getRecipients());
        return testRunService.sendTestRunResultsEmail(id, "failures".equals(filter), showStacktrace, recipients);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Send failed test run result email", nickname = "sendTestRunFailureEmail", httpMethod = "POST", response = String.class)
    @PostMapping(path = "/{id}/emailFailure", produces = MediaType.TEXT_HTML_VALUE)
    @Override
    public String sendTestRunFailureEmail(
            @PathVariable("id") String id,
            @RequestBody @Valid EmailType email,
            @RequestParam(name = "suiteOwner", defaultValue = "false", required = false) boolean suiteOwner,
            @RequestParam(name = "suiteRunner", defaultValue = "false", required = false) boolean suiteRunner
    ) {
        String[] recipients = EmailUtils.obtainRecipients(email.getRecipients());
        return testRunService.sendTestRunResultsEmailFailure(id, suiteOwner, suiteRunner, recipients);
    }

    @PostMapping(path = "/{id}/spreadsheet", produces = MediaType.TEXT_HTML_VALUE)
    public String createTestRunResultSpreadsheet(@PathVariable("id") String id, @RequestBody String recipientsLine) {
        String[] recipients = EmailUtils.obtainRecipients(recipientsLine);
        return testRunService.createTestRunResultSpreadsheet(id, getPrincipalId(), recipients);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get test run result html text", nickname = "exportTestRunHTML", httpMethod = "GET", response = String.class)
    @GetMapping(path = "/{id}/export", produces = "text/html;charset=UTF-8")
    @Override
    public String exportTestRunHTML(@PathVariable("id") String id) {
        return testRunService.exportTestRunHTML(id);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Mark test run as reviewed", nickname = "markTestRunAsReviewed", httpMethod = "POST")
    @PreAuthorize("hasPermission('MODIFY_TEST_RUNS')")
    @PostMapping("/{id}/markReviewed")
    @Override
    public void markTestRunAsReviewed(@PathVariable("id") long id, @RequestBody @Valid CommentType comment) {
        TestRun testRun = testRunService.markAsReviewed(id, comment.getComment());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Rerun test run", nickname = "rerunTestRun", httpMethod = "GET")
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @GetMapping("/{id}/rerun")
    @Override
    public void rerunTestRun(@PathVariable("id") long id, @RequestParam(name = "rerunFailures", required = false) boolean rerunFailures) {
        testRunService.rerunTestRun(id, rerunFailures);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Debug test run", nickname = "debugTestRun", httpMethod = "GET")
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @GetMapping("/{id}/debug")
    @Override
    public void debugTestRun(@PathVariable("id") long id) {
        testRunService.debugTestRun(id);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Abort job or debug", nickname = "abortCIJob", httpMethod = "GET")
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @GetMapping({ "/abort/ci", "/abort/debug" })
    @Override
    public void abortCIJob(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "ciRunId", required = false) String ciRunId
    ) {
        TestRun testRun = TestRun.builder()
                                 .id(id)
                                 .ciRunId(ciRunId)
                                 .build();
        testRunService.abortTestRunJob(testRun);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Build test run", nickname = "buildTestRun", httpMethod = "POST")
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @PostMapping("/{id}/build")
    @Override
    public void buildTestRun(
            @PathVariable("id") long id,
            @RequestBody Map<String, String> jobParameters
    ) {
        testRunService.buildTestRunJob(id, jobParameters);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get job parameters", nickname = "getjobParameters", httpMethod = "GET", response = Map.class)
    @PreAuthorize("hasPermission('TEST_RUNS_CI')")
    @GetMapping("/{id}/jobParameters")
    @Override
    public List<BuildParameterType> getJobParameters(@PathVariable("id") long id) {
        return testRunService.getTestRunJobParameters(id);
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get environments", nickname = "getEnvironments", httpMethod = "GET", response = List.class)
    @GetMapping("/environments")
    @Override
    public List<String> getEnvironments() {
        return testRunService.getEnvironments();
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get platforms", nickname = "getPlatforms", httpMethod = "GET", response = List.class)
    @GetMapping("/platforms")
    @Override
    public List<String> getPlatforms() {
        return testRunService.getPlatforms();
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get console output from jenkins by test run id", nickname = "getConsoleOutput", httpMethod = "GET")
    @GetMapping("/jobConsoleOutput/{count}/{fullCount}")
    @Override
    public Map<Integer, String> getConsoleOutput(
            @PathVariable("count") int count,
            @PathVariable("fullCount") int fullCount,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "ciRunId", required = false) String ciRunId
    ) {
        TestRun testRun = TestRun.builder()
                                 .id(id)
                                 .ciRunId(ciRunId)
                                 .build();
        return testRunService.getBuildConsoleOutput(testRun, count, fullCount);
    }

}
