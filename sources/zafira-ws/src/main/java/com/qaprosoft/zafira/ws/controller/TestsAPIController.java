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
package com.qaprosoft.zafira.ws.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.dozer.Mapper;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestSearchCriteria;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.db.WorkItem.Type;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.models.push.TestRunStatisticPush;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.StatisticsService;
import com.qaprosoft.zafira.services.services.TestMetricService;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.TestService;
import com.qaprosoft.zafira.services.services.WorkItemService;
import com.qaprosoft.zafira.services.services.jmx.JiraService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.rcarz.jiraclient.Issue;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@Api(value = "Tests API")
@RequestMapping("api/tests")
public class TestsAPIController extends AbstractController
{
	@Autowired
	private Mapper mapper;

	@Autowired
	private TestService testService;

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
	@ApiOperation(value = "Start test", nickname = "startTest", code = 200, httpMethod = "POST", response = TestType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestType startTest(@Valid @RequestBody TestType t,
			@RequestHeader(value = "Project", required = false) String project) throws ServiceException
	{
		Test test = testService.startTest(mapper.map(t, Test.class), t.getWorkItems(), t.getConfigXML());
		websocketTemplate.convertAndSend(STATISTICS_WEBSOCKET_PATH, new TestRunStatisticPush(statisticsService.getTestRunStatistic(test.getTestRunId())));
		websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
		return mapper.map(test, TestType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Finish test", nickname = "finishTest", code = 200, httpMethod = "POST", response = TestType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/finish", method = RequestMethod.POST)
	public @ResponseBody TestType finishTest(
			@ApiParam(value = "Test ID", required = true) @PathVariable(value = "id") long id, @RequestBody TestType t)
			throws ServiceException
	{
		t.setId(id);
		Test test = testService.finishTest(mapper.map(t, Test.class), t.getConfigXML());
		testMetricService.createTestMetrics(t.getId(), t.getTestMetrics());
		websocketTemplate.convertAndSend(STATISTICS_WEBSOCKET_PATH, new TestRunStatisticPush(statisticsService.getTestRunStatistic(test.getTestRunId())));
		websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
		return mapper.map(test, TestType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update test", nickname = "updateTest", code = 200, httpMethod = "PUT", response = Test.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_TESTS')")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Test updateTest(@RequestBody Test test) throws ServiceException
	{
		websocketTemplate.convertAndSend(STATISTICS_WEBSOCKET_PATH, new TestRunStatisticPush(statisticsService.getTestRunStatistic(test.getTestRunId())));
		websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

		TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
		websocketTemplate.convertAndSend(TEST_RUNS_WEBSOCKET_PATH, new TestRunPush(testRun));

		return testService.updateTest(test);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create test work items", nickname = "createTestWorkItems", code = 200, httpMethod = "POST", response = TestType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/workitems", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestType createTestWorkItems(
			@ApiParam(value = "Work item ID", required = true) @PathVariable(value = "id") long id,
			@RequestBody List<String> workItems) throws ServiceException
	{
		return mapper.map(testService.createTestWorkItems(id, workItems), TestType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete test by id", nickname = "deleteTest", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteTest(@ApiParam(value = "Test ID", required = true) @PathVariable(value = "id") long id)
			throws ServiceException
	{
		testService.deleteTestById(id);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Search tests", nickname = "searchTests", code = 200, httpMethod = "POST", response = SearchResult.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<Test> searchTests(@RequestBody TestSearchCriteria sc) throws ServiceException
	{
		return testService.searchTests(sc);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get test case work items by type", nickname = "getTestCaseWorkItemsByType", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/workitem/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<WorkItem> getTestCaseWorkItemsByType(
			@ApiParam(value = "Test ID", required = true) @PathVariable(value = "id") long id,  @PathVariable(value = "type") String type) throws ServiceException
	{
		List<WorkItem> workItems = new ArrayList<>();
		Test test = testService.getTestById(id);
		if (test != null)
		{
			workItems = workItemService.getWorkItemsByTestCaseIdAndType(test.getTestCaseId(), Type.valueOf(type));
		}
		return workItems;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create or update test work item", nickname = "createOrUpdateTestWorkItem", code = 200, httpMethod = "POST", response = WorkItem.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/workitem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody WorkItem createOrUpdateTestWorkItem(
			@ApiParam(value = "Test ID", required = true) @PathVariable(value = "id") long id,
			@RequestBody WorkItem workItem) throws ServiceException, InterruptedException
	{
		if (getPrincipalId() > 0) {
			workItem.setUser(new User(getPrincipalId()));
		}
		if (workItem.getType() == Type.BUG || workItem.getType() == Type.TASK) {
			workItem = testService.createOrUpdateTestWorkItem(id, workItem);
		} else  {
			workItem = testService.createWorkItem(id, workItem);
		}
		Test test = testService.getTestById(id);

		websocketTemplate.convertAndSend(STATISTICS_WEBSOCKET_PATH, new TestRunStatisticPush(statisticsService.getTestRunStatistic(test.getTestRunId())));
		websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
		
		TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
		websocketTemplate.convertAndSend(TEST_RUNS_WEBSOCKET_PATH, new TestRunPush(testRun));

		return workItem;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update test known issue", nickname = "updateTestKnownIssue", code = 200, httpMethod = "PUT", response = WorkItem.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/issues", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody WorkItem updateTestKnownIssue(
			@ApiParam(value = "Test ID", required = true) @PathVariable(value = "id") long id,
			@RequestBody WorkItem workItem) throws ServiceException
	{
		Test test = testService.getTestById(id);
		workItem.setHashCode(testService.getTestMessageHashCode(test.getMessage()));
		return workItemService.updateWorkItem(workItem);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete test work item", nickname = "deleteTestWorkItem", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{testId}/workitem/{workItemId}", method = RequestMethod.DELETE)
	public void deleteTestWorkItem(@PathVariable(value = "workItemId") long workItemId,
			@PathVariable(value = "testId") long testId) throws ServiceException, InterruptedException
	{
		Test test = testService.getTestById(testId);
		WorkItem workItem = workItemService.getWorkItemById(workItemId);
		if (workItem.getType() == Type.BUG) {
			testRunService.updateStatistics(test.getTestRunId(), TestRunStatistics.Action.REMOVE_KNOWN_ISSUE);
			if (test.isBlocker())
				testRunService.updateStatistics(test.getTestRunId(), TestRunStatistics.Action.REMOVE_BLOCKER);
			websocketTemplate.convertAndSend(STATISTICS_WEBSOCKET_PATH,
					new TestRunStatisticPush(statisticsService.getTestRunStatistic(test.getTestRunId())));
			websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
			TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
			websocketTemplate.convertAndSend(TEST_RUNS_WEBSOCKET_PATH, new TestRunPush(testRun));
		}
		testService.deleteTestWorkItemByWorkItemIdAndTest(workItemId, test);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "jira/{issue}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Issue getJiraIssue(@PathVariable(value = "issue") String issue)
	{
		return jiraService.getIssue(issue);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "jira/connect", method = RequestMethod.GET)
	public @ResponseBody boolean getConnectionToJira()
	{
		return jiraService.isConnected();
	}
}
