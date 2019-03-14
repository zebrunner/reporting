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
package com.qaprosoft.zafira.ws.controller.application;

import static com.qaprosoft.zafira.services.services.application.FilterService.Template.TEST_RUN_COUNT_TEMPLATE;
import static com.qaprosoft.zafira.services.services.application.FilterService.Template.TEST_RUN_TEMPLATE;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.JobSearchCriteria;
import com.qaprosoft.zafira.services.exceptions.*;
import com.qaprosoft.zafira.services.services.application.*;
import com.qaprosoft.zafira.services.services.application.cache.StatisticsService;
import com.qaprosoft.zafira.services.services.application.jmx.google.models.TestRunSpreadsheetService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.FilterSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestRunSearchCriteria;
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
import com.qaprosoft.zafira.services.services.application.jmx.JenkinsService;
import com.qaprosoft.zafira.services.services.application.jmx.SlackService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@Api(value = "Test runs API")
@RequestMapping("api/tests/runs")
public class TestRunsAPIController extends AbstractController {
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
	private JenkinsService jenkinsService;

	@Autowired
	private SlackService slackService;

	@Autowired
	private SimpMessagingTemplate websocketTemplate;

	@Autowired
	private StatisticsService statisticsService;

	private static final Logger LOGGER = LoggerFactory.getLogger(TestRunsAPIController.class);

	@ResponseStatusDetails
	@ApiOperation(value = "Start test run", nickname = "startTestRun", httpMethod = "POST", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType startTestRun(@RequestBody @Valid TestRunType tr,
			@RequestHeader(value = "Project", required = false) String project)
			throws ServiceException, MappingException, JAXBException {
		TestRun testRun = mapper.map(tr, TestRun.class);
		testRun.setProject(projectService.getProjectByName(project));
		testRun = testRunService.startTestRun(testRun);
		TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
		websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRunFull));
		websocketTemplate.convertAndSend(getStatisticsWebsocketPath(),
				new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update test run config", nickname = "updateTestRun", httpMethod = "PUT", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType updateTestRun(@RequestBody TestRunType tr)
			throws ServiceException, MappingException, JAXBException {
		TestRun testRun = testRunService.getTestRunById(tr.getId());
		if (testRun == null && !StringUtils.isEmpty(tr.getConfigXML())) {
			throw new ServiceException("Test run not found by id: " + tr.getId());
		}
		testRun.setConfigXML(tr.getConfigXML());
		testRunService.initTestRunWithXml(testRun);
		testRunService.updateTestRun(testRun);
		TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
		websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRunFull));
		websocketTemplate.convertAndSend(getStatisticsWebsocketPath(),
				new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Finish test run", nickname = "finishTestRun", httpMethod = "POST", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/finish", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType finishTestRun(
			@ApiParam(value = "Id of the test-run", required = true) @PathVariable(value = "id") long id)
			throws ServiceException, InterruptedException, IOException {
		TestRun testRun = testRunService.calculateTestRunResult(id, true);
		TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
		websocketTemplate.convertAndSend(getStatisticsWebsocketPath(),
				new TestRunStatisticPush(statisticsService.getTestRunStatistic(id)));
		websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRunFull));
		slackService.sendAutoStatus(testRunFull);
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Abort test run", nickname = "abortTestRun", httpMethod = "POST", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_TEST_RUNS')")
	@RequestMapping(value = "abort", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType abortTestRun(
			@ApiParam(value = "Test run id") @RequestParam(value = "id", required = false) Long id,
			@ApiParam(value = "Test run CI id") @RequestParam(value = "ciRunId", required = false) String ciRunId,
			@RequestBody(required = false) CommentType abortCause) throws ServiceException, InterruptedException, UnsupportedEncodingException {
		TestRun testRun = id != null ? testRunService.getTestRunById(id) : testRunService.getTestRunByCiRunId(ciRunId);
		if (testRun == null) {
			throw new TestRunNotFoundException("Test run not found for abort!");
		}

		if (Status.IN_PROGRESS.equals(testRun.getStatus()) || Status.QUEUED.equals(testRun.getStatus())) {
			testRunService.abortTestRun(testRun, URLDecoder.decode(abortCause.getComment(), "UTF-8"));
			for (Test test : testService.getTestsByTestRunId(testRun.getId())) {
				if (Status.ABORTED.equals(test.getStatus())) {
					websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestPush(test));
				}
			}
			websocketTemplate.convertAndSend(getTestRunsWebsocketPath(),
					new TestRunPush(testRunService.getTestRunByIdFull(testRun.getId())));
			websocketTemplate.convertAndSend(getStatisticsWebsocketPath(),
					new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId())));
		}
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create queued testRun", nickname = "queueTestRun", httpMethod = "POST", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "queue", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType createQueuedTestRun(@RequestBody QueueTestRunParamsType queuedTestRunParams)
			throws ServiceException {
		TestRun testRun = new TestRun();
		if (jobsService.getJobByName(queuedTestRunParams.getJobName()) != null) {
			testRun = testRunService.queueTestRun(queuedTestRunParams, userService.getUserById(getPrincipalId()));
		}
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get test run", nickname = "getTestRun", httpMethod = "GET", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType getTestRun(
			@ApiParam(value = "Id of the test-run", required = true) @PathVariable(value = "id") long id)
			throws ServiceException {
		TestRun testRun = testRunService.getTestRunById(id);
		if (testRun == null) {
			throw new TestRunNotFoundException();
		}
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Search test runs", nickname = "searchTestRuns", httpMethod = "POST", response = SearchResult.class)
	@RequestMapping(value = "search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<TestRun> searchTestRuns(
			@RequestParam(value = "filterId", required = false) Long filterId, @RequestBody TestRunSearchCriteria sc)
			throws ServiceException {
		FilterType filterType = filterId != null ? mapper.map(filterService.getFilterById(filterId), FilterType.class)
				: null;
		if (filterType != null) {
			sc.setFilterSearchCriteria(new FilterSearchCriteria());
			sc.getFilterSearchCriteria().setFilterTemplate(filterService.getTemplate(filterType, TEST_RUN_TEMPLATE));
			sc.getFilterSearchCriteria()
					.setFilterSearchCountTemplate(filterService.getTemplate(filterType, TEST_RUN_COUNT_TEMPLATE));
		}
		return testRunService.searchTestRuns(sc);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Rerun jobs", nickname = "smartRerun", httpMethod = "POST", response = SearchResult.class)
	@RequestMapping(value = "rerun/jobs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<TestRunType> rerunJobs(
			@RequestParam(value = "doRebuild", defaultValue = "false", required = false) Boolean doRebuild,
			@RequestParam(value = "rerunFailures", defaultValue = "true", required = false) Boolean rerunFailures,
			@RequestBody JobSearchCriteria sc) throws ServiceException {
		if (!StringUtils.isEmpty(sc.getUpstreamJobUrl())) {
			sc.setUpstreamJobUrl(sc.getUpstreamJobUrl().replaceAll("/$", ""));
			if (jobsService.getJobByJobURL(sc.getUpstreamJobUrl()) != null) {
				sc.setUpstreamJobId(jobsService.getJobByJobURL(sc.getUpstreamJobUrl()).getId());
			}
		}
		if (rerunFailures && sc.getFailurePercent() == null) {
			sc.setFailurePercent(0);
		}
		List<TestRun> testRuns = testRunService.getTestRunsForSmartRerun(sc);
		List<TestRunType> testRunTypes = new ArrayList<>();
		if (testRuns != null) {
			testRunTypes = testRuns.stream().map(testRun -> {
				if (doRebuild) {
					try {
						boolean success = jenkinsService.rerunJob(testRun.getJob(), testRun.getBuildNumber(),
								rerunFailures);
						if (!success) {
							throw new UnableToRebuildCIJobException();
						}
					} catch (UnableToRebuildCIJobException e) {
						LOGGER.error("Problems with job building occurred", e);
					}
				}
				return mapper.map(testRun, TestRunType.class);
			}).collect(Collectors.toList());
		}
		return testRunTypes;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get test run by ci run id", nickname = "getTestRunByCiRunId", httpMethod = "GET", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType getTestRunByCiRunId(@RequestParam(value = "ciRunId") String ciRunId)
			throws ServiceException {
		TestRun testRun = testRunService.getTestRunByCiRunId(ciRunId);
		if (testRun == null) {
			throw new TestRunNotFoundException();
		}
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get test run results by id", nickname = "getTestRunResults", httpMethod = "GET", response = java.util.List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<TestType> getTestRunResults(@PathVariable(value = "id") long id) throws ServiceException {
		List<TestType> tests = new ArrayList<>();
		for (Test test : testService.getTestsByTestRunId(id)) {
			tests.add(mapper.map(test, TestType.class));
		}
		return tests;
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Create compare matrix", nickname = "createCompareMatrix", httpMethod = "GET", response = Map.class)
	@RequestMapping(value = "{ids}/compare", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<Long, Map<String, Test>> createCompareMatrix(
			@PathVariable(value = "ids") String testRunIds) throws ServiceException {
		List<Long> ids = new ArrayList<>();
		for (String id : testRunIds.split("\\+")) {
			ids.add(Long.valueOf(id));
		}
		return testRunService.createCompareMatrix(ids);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Delete test run", nickname = "deleteTestRun", httpMethod = "DELETE")
	@PreAuthorize("hasPermission('MODIFY_TEST_RUNS')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteTestRun(@PathVariable(value = "id") long id) throws ServiceException {
		testRunService.deleteTestRunById(id);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Send test run result email", nickname = "sendTestRunResultsEmail", httpMethod = "POST", response = String.class)
	@RequestMapping(value = "{id}/email", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String sendTestRunResultsEmail(@PathVariable(value = "id") String id,
			@RequestBody @Valid EmailType email,
			@RequestParam(value = "filter", defaultValue = "all", required = false) String filter,
			@RequestParam(value = "showStacktrace", defaultValue = "true", required = false) boolean showStacktrace)
			throws ServiceException, JAXBException {
		String[] recipients = getRecipients(email.getRecipients());
		return testRunService.sendTestRunResultsEmail(id, "failures".equals(filter), showStacktrace, recipients);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Send failed test run result email", nickname = "sendTestRunFailureEmail", httpMethod = "POST", response = String.class)
	@RequestMapping(value = "{id}/emailFailure", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String sendTestRunFailureEmail(@PathVariable(value = "id") String id,
														@RequestBody @Valid EmailType email,
														@RequestParam(value = "suiteOwner", defaultValue = "false", required = false) boolean suiteOwner,
														@RequestParam(value = "suiteRunner", defaultValue = "false", required = false) boolean suiteRunner)
			throws ServiceException, JAXBException {

		String[] recipients = getRecipients(email.getRecipients());
		if(suiteOwner){
			Long testSuiteId = testRunService.getTestRunByCiRunIdFull(id).getTestSuite().getId();
			String suiteOwnerEmail = testSuiteService.getTestSuiteByIdFull(testSuiteId).getUser().getEmail();
			ArrayUtils.add(recipients, suiteOwnerEmail);
		}
		if(suiteRunner){
			String suiteRunnerEmail = testRunService.getTestRunByCiRunIdFull(id).getUser().getEmail();
			ArrayUtils.add(recipients, suiteRunnerEmail);
		}

		return testRunService.sendTestRunResultsEmail(id, false, true, recipients);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Create test run results spreadsheet", nickname = "createTestRunResultSpreadsheet", httpMethod = "POST", response = String.class)
	@RequestMapping(value = "{id}/spreadsheet", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String createTestRunResultSpreadsheet(@PathVariable(value = "id") String id,
			@RequestBody String recipients) throws ServiceException {
		recipients = recipients + ";" + userService.getUserById(getPrincipalId()).getEmail();
		return testRunSpreadsheetService.createTestRunResultSpreadsheet(testRunService.getTestRunByIdFull(id),
				getRecipients(recipients));
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get test run result html text", nickname = "exportTestRunHTML", httpMethod = "GET", response = String.class)
	@RequestMapping(value = "{id}/export", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String exportTestRunHTML(@PathVariable(value = "id") String id)
			throws ServiceException, JAXBException {
		return testRunService.exportTestRunHTML(id);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Mark test run as reviewed", nickname = "markTestRunAsReviewed", httpMethod = "POST")
	@PreAuthorize("hasPermission('MODIFY_TEST_RUNS')")
	@RequestMapping(value = "{id}/markReviewed", method = RequestMethod.POST)
	public void markTestRunAsReviewed(@PathVariable(value = "id") long id, @RequestBody @Valid CommentType comment)
			throws ServiceException {
		TestRun tr = testRunService.markAsReviewed(id, comment.getComment());
		websocketTemplate.convertAndSend(getStatisticsWebsocketPath(),
				new TestRunStatisticPush(statisticsService.getTestRunStatistic(tr.getId())));
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Rerun test run", nickname = "rerunTestRun", httpMethod = "GET")
	@PreAuthorize("hasPermission('TEST_RUNS_CI')")
	@RequestMapping(value = "{id}/rerun", method = RequestMethod.GET)
	public void rerunTestRun(@PathVariable(value = "id") long id,
			@RequestParam(value = "rerunFailures", required = false, defaultValue = "false") boolean rerunFailures)
			throws ServiceException {
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if (testRun == null) {
			throw new TestRunNotFoundException();
		}
		testRun.setComments(null);
		testRun.setReviewed(false);
		testRunService.updateTestRun(testRun);
		if (!jenkinsService.rerunJob(testRun.getJob(), testRun.getBuildNumber(), rerunFailures)) {
			throw new UnableToRebuildCIJobException();
		}
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Debug test run", nickname = "debugTestRun", httpMethod = "GET")
	@PreAuthorize("hasPermission('TEST_RUNS_CI')")
	@RequestMapping(value = "{id}/debug", method = RequestMethod.GET)
	public void debugTestRun(@PathVariable(value = "id") long id) throws ServiceException {
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if (testRun == null) {
			throw new TestRunNotFoundException();
		}

		if (!jenkinsService.debug(testRun.getJob(), testRun.getBuildNumber())) {
			throw new UnableToRebuildCIJobException();
		}
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Abort job", nickname = "abortCIJob", httpMethod = "GET")
	@PreAuthorize("hasPermission('TEST_RUNS_CI')")
	@RequestMapping(value = "abort/ci", method = RequestMethod.GET)
	public void abortCIJob(@ApiParam(value = "Test run id") @RequestParam(value = "id", required = false) Long id,
			@ApiParam(value = "Test run CI id") @RequestParam(value = "ciRunId", required = false) String ciRunId)
			throws ServiceException {
		TestRun testRun = id != null ? testRunService.getTestRunByIdFull(id)
				: testRunService.getTestRunByCiRunIdFull(ciRunId);
		if (testRun == null) {
			throw new TestRunNotFoundException();
		}

		if (!jenkinsService.abortJob(testRun.getJob(), testRun.getBuildNumber())) {
			throw new UnableToAbortCIJobException();
		}
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Abort debug", nickname = "abortDebug", httpMethod = "GET")
	@PreAuthorize("hasPermission('TEST_RUNS_CI')")
	@RequestMapping(value = "abort/debug", method = RequestMethod.GET)
	public void abortDebug(@ApiParam(value = "Test run id") @RequestParam(value = "id", required = false) Long id,
			@ApiParam(value = "Test run CI id") @RequestParam(value = "ciRunId", required = false) String ciRunId)
			throws ServiceException {
		TestRun testRun = id != null ? testRunService.getTestRunByIdFull(id)
				: testRunService.getTestRunByCiRunIdFull(ciRunId);
		if (testRun == null) {
			throw new TestRunNotFoundException();
		}

		if (!jenkinsService.abortJob(testRun.getJob(), testRun.getBuildNumber())) {
			throw new UnableToAbortCIJobException();
		}
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Build test run", nickname = "buildTestRun", httpMethod = "POST")
	@PreAuthorize("hasPermission('TEST_RUNS_CI')")
	@RequestMapping(value = "{id}/build", method = RequestMethod.POST)
	public void buildTestRun(@PathVariable(value = "id") long id, @RequestParam(value = "buildWithParameters", required = false, defaultValue = "true") boolean buildWithParameters, @RequestBody Map<String, String> jobParameters) throws ServiceException {
		
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if (testRun == null) 
			throw new TestRunNotFoundException("Unable to find test run by id");
		
		boolean success = false;
		if(buildWithParameters) {
			success = jenkinsService.buildJob(testRun.getJob(), jobParameters);
		}
		else {
			success = jenkinsService.rerunJob(testRun.getJob(), testRun.getBuildNumber(), false);
		}
		
		if (!success) 
			throw new UnableToRebuildCIJobException();
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get job parameters", nickname = "getjobParameters", httpMethod = "GET", response = Map.class)
	@PreAuthorize("hasPermission('TEST_RUNS_CI')")
	@RequestMapping(value = "{id}/jobParameters", method = RequestMethod.GET)
	public @ResponseBody List<BuildParameterType> getjobParameters(@PathVariable(value = "id") long id)
			throws ServiceException {
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if (testRun == null) {
			throw new TestRunNotFoundException();
		}
		return jenkinsService.getBuildParameters(testRun.getJob(), testRun.getBuildNumber());
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get environments", nickname = "getEnvironments", httpMethod = "GET", response = List.class)
	@RequestMapping(value = "environments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getEnvironments() throws ServiceException {
		return testRunService.getEnvironments();
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get platforms", nickname = "getPlatforms", httpMethod = "GET", response = List.class)
	@RequestMapping(value = "platforms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getPlatforms() throws ServiceException {
		return testRunService.getPlatforms();
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get console output from jenkins by test run id", nickname = "getConsoleOutput", httpMethod = "GET")
	@RequestMapping(value = "jobConsoleOutput/{count}/{fullCount}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<Integer, String> getConsoleOutput(@PathVariable(value = "count") int count,
			@PathVariable(value = "fullCount") int fullCount, @RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "ciRunId", required = false) String ciRunId) throws ServiceException {
		TestRun testRun = id != null ? testRunService.getTestRunByIdFull(id)
				: testRunService.getTestRunByCiRunIdFull(ciRunId);
		if (testRun == null) {
			throw new TestRunNotFoundException();
		}
		return jenkinsService.getBuildConsoleOutputHtml(testRun.getJob(), testRun.getBuildNumber(), count, fullCount);
	}

	private String[] getRecipients(String recipients) {
		return !StringUtils.isEmpty(recipients)
				? recipients.trim().replaceAll(",", " ").replaceAll(";", " ").replaceAll("\\[\\]", " ").split(" ")
				: new String[] {};
	}
}
