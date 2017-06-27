package com.qaprosoft.zafira.ws.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.qaprosoft.zafira.models.db.*;
import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.services.services.*;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import com.qaprosoft.zafira.models.db.WorkItem.Type;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
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
	private TestArtifactService testArtifactService;

	@Autowired
	private WorkItemService workItemService;

	@Autowired
	private JiraService jiraService;

	@Autowired
	private SimpMessagingTemplate websocketTemplate;

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
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));
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
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));
		return mapper.map(test, TestType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Mark test passed", nickname = "markTestAsPassed", code = 200, httpMethod = "POST", response = TestType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/passed", method = RequestMethod.POST)
	public @ResponseBody TestType markTestAsPassed(@PathVariable(value = "id") long id)
			throws ServiceException, InterruptedException
	{
		Test test = testService.markTestAsPassed(id);
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));

		TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRun));

		return mapper.map(test, TestType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create test work item", nickname = "createTestWorkItem", code = 200, httpMethod = "POST", response = TestType.class)
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
	@ApiOperation(value = "Delete test dublicates", nickname = "deleteTestDublicates", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "duplicates/remove", method = RequestMethod.PUT)
	public void deleteTestDuplicates(@RequestBody TestType test) throws ServiceException
	{
		testService.deleteTestByTestRunIdAndTestCaseIdAndLogURL(mapper.map(test, Test.class));
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
	@ApiOperation(value = "Get test known issues", nickname = "getTestKnownIssues", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/issues", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<WorkItem> getTestKnownIssues(
			@ApiParam(value = "Test ID", required = true) @PathVariable(value = "id") long id) throws ServiceException
	{
		List<WorkItem> issues = new ArrayList<>();
		Test test = testService.getTestById(id);
		if (test != null)
		{
			issues = workItemService.getWorkItemsByTestCaseIdAndType(test.getTestCaseId(), Type.BUG);
		}
		return issues;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get testArtifact results by testId", nickname = "getTestArtifacts", code = 200, httpMethod = "GET", response = java.util.List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/artifacts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<TestArtifactType> getTestArtifacts(@PathVariable(value = "id") long id) throws ServiceException
	{
		List<TestArtifactType> testArtifacts = new ArrayList<>();
		for (TestArtifact testArtifact: testArtifactService.getAllTestArtifacts(id))
		{
			testArtifacts.add(mapper.map(testArtifact, TestArtifactType.class));
		}
		return testArtifacts;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create test known issue", nickname = "createTestKnownIssue", code = 200, httpMethod = "POST", response = WorkItem.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/issues", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody WorkItem createTestKnownIssue(
			@ApiParam(value = "Test ID", required = true) @PathVariable(value = "id") long id,
			@RequestBody WorkItem workItem) throws ServiceException, InterruptedException
	{
		if (getPrincipalId() > 0)
		{
			workItem.setUser(new User(getPrincipalId()));
		}
		workItem = testService.createTestKnownIssue(id, workItem);

		Test test = testService.getTestById(id);
		TestRun testRun = testRunService.getTestRunById(test.getTestRunId());

		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRun));

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
	@ApiOperation(value = "Delete test known issue", nickname = "deleteTestKnownIssue", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{testId}/issues/{workItemId}", method = RequestMethod.DELETE)
	public void deleteTestKnownIssue(@PathVariable(value = "workItemId") long workItemId,
			@PathVariable(value = "testId") long testId) throws ServiceException, InterruptedException
	{
		Test test = testService.getTestById(testId);
		TestRun testRun = testService.deleteTestWorkItemByWorkItemIdAndTest(workItemId, test);
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRun));
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
