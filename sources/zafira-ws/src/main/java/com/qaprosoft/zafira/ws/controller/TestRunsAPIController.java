package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.dto.*;
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;
import com.qaprosoft.zafira.services.exceptions.UnableToRebuildCIJobException;
import com.qaprosoft.zafira.services.services.ProjectService;
import com.qaprosoft.zafira.services.services.TestConfigService;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.TestService;
import com.qaprosoft.zafira.services.services.jmx.JenkinsService;
import com.qaprosoft.zafira.services.services.jmx.SlackService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Api(value = "Test runs API")
@RequestMapping("api/tests/runs")
public class TestRunsAPIController extends AbstractController
{

	@Autowired
	private Mapper mapper;

	@Autowired
	private TestRunService testRunService;

	@Autowired
	private TestService testService;

	@Autowired
	private TestConfigService testConfigService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private JenkinsService jenkinsService;

	@Autowired
	private SlackService slackService;

	@Autowired
	private SimpMessagingTemplate websocketTemplate;

	@ResponseStatusDetails
	@ApiOperation(value = "Start test run", nickname = "startTestRun", code = 200, httpMethod = "POST", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType startTestRun(@RequestBody @Valid TestRunType tr,
			@RequestHeader(value = "Project", required = false) String project)
			throws ServiceException, MappingException, JAXBException
	{
		TestRun testRun = mapper.map(tr, TestRun.class);
		testRun.setProject(projectService.getProjectByName(project));
		testRun = testRunService.startTestRun(testRun);
		TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRunFull));
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update test run config", nickname = "updateTestRun", code = 200, httpMethod = "PUT", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType updateTestRun(@RequestBody TestRunType tr)
			throws ServiceException, MappingException, JAXBException
	{
		TestRun testRun = testRunService.getTestRunById(tr.getId());
		if (testRun == null && !StringUtils.isEmpty(tr.getConfigXML()))
		{
			throw new ServiceException("Test run not found by id: " + tr.getId());
		}
		testRun.setConfigXML(tr.getConfigXML());
		// TODO: remove that ASAP from controller
		for (Argument arg : testConfigService.readConfigArgs(testRun.getConfigXML(), false))
		{
			if ("app_version".equals(arg.getKey()))
			{
				testRun.setAppVersion(arg.getValue());
			}
		}
		testRunService.updateTestRun(testRun);
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Finish test run", nickname = "finishTestRun", code = 200, httpMethod = "POST", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/finish", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType finishTestRun(
			@ApiParam(value = "Id of the test-run", required = true) @PathVariable(value = "id") long id)
			throws ServiceException, InterruptedException, IOException
	{
		TestRun testRun = testRunService.calculateTestRunResult(id, true);
		TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRunFull));
		slackService.sendAutoStatus(testRunFull);
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Abort test run", nickname = "abortTestRun", code = 200, httpMethod = "GET", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "abort", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType abortTestRun(
			@ApiParam(value = "Test run id") @RequestParam(value = "id", required = false) Long id,
			@ApiParam(value = "Test run CI id") @RequestParam(value = "ciRunId", required = false) String ciRunId)
			throws ServiceException, InterruptedException
	{
		if (id == null && ciRunId == null)
		{
			throw new ServiceException("Id or CI run ID should be set!");
		}

		TestRun testRun = id != null ? testRunService.getTestRunById(id) : testRunService.getTestRunByCiRunId(ciRunId);
		testRunService.abortTestRun(testRun);
		if(Status.IN_PROGRESS.equals(testRun.getStatus()))
		{
			websocketTemplate.convertAndSend(WEBSOCKET_PATH,
					new TestRunPush(testRunService.getTestRunByIdFull(testRun.getId())));
		}

		for (Test test : testService.getTestsByTestRunId(testRun.getId()))
		{
			if (Status.ABORTED.equals(test.getStatus()))
			{
				websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));
			}
		}

		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get test run", nickname = "getTestRun", code = 200, httpMethod = "GET", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType getTestRun(
			@ApiParam(value = "Id of the test-run", required = true) @PathVariable(value = "id") long id)
			throws ServiceException
	{
		TestRun testRun = testRunService.getTestRunById(id);
		if (testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Search test runs", nickname = "searchTestRuns", code = 200, httpMethod = "POST", response = SearchResult.class)
	@RequestMapping(value = "search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<TestRun> searchTestRuns(@RequestBody TestRunSearchCriteria sc)
			throws ServiceException
	{
		return testRunService.searchTestRuns(sc);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get test run by ci run id", nickname = "getTestRunByCiRunId", code = 200, httpMethod = "GET", response = TestRunType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType getTestRunByCiRunId(@RequestParam(value = "ciRunId") String ciRunId)
			throws ServiceException
	{
		TestRun testRun = testRunService.getTestRunByCiRunId(ciRunId);
		if (testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get test run results by id", nickname = "getTestRunResults", code = 200, httpMethod = "GET", response = java.util.List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<TestType> getTestRunResults(@PathVariable(value = "id") long id) throws ServiceException
	{
		List<TestType> tests = new ArrayList<>();
		for (Test test : testService.getTestsByTestRunId(id))
		{
			tests.add(mapper.map(test, TestType.class));
		}
		return tests;
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Create compare matrix", nickname = "createCompareMatrix", code = 200, httpMethod = "GET", response = Map.class)
	@RequestMapping(value = "{ids}/compare", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<Long, Map<String, Test>> createCompareMatrix(
			@PathVariable(value = "ids") String testRunIds) throws ServiceException
	{
		List<Long> ids = new ArrayList<>();
		for (String id : testRunIds.split("\\+"))
		{
			ids.add(Long.valueOf(id));
		}
		return testRunService.createCompareMatrix(ids);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Delete test run", nickname = "deleteTestRun", code = 200, httpMethod = "DELETE")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteTestRun(@PathVariable(value = "id") long id) throws ServiceException
	{
		testRunService.deleteTestRunById(id);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Send test run result email", nickname = "sendTestRunResultsEmail", code = 200, httpMethod = "POST", response = String.class)
	@RequestMapping(value = "{id}/email", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String sendTestRunResultsEmail(@PathVariable(value = "id") long id,
			@RequestBody @Valid EmailType email,
			@RequestParam(value = "filter", defaultValue = "all", required = false) String filter,
			@RequestParam(value = "showStacktrace", defaultValue = "true", required = false) boolean showStacktrace)
			throws ServiceException, JAXBException
	{
		String[] recipients = !StringUtils.isEmpty(email.getRecipients())
				? email.getRecipients().trim().replaceAll(",", " ").replaceAll(";", " ").split(" ") : new String[] {};
		return testRunService.sendTestRunResultsEmail(id, "failures".equals(filter), showStacktrace, recipients);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get test run result html text", nickname = "exportTestRunHTML", code = 200, httpMethod = "GET", response = String.class)
	@RequestMapping(value = "{id}/export", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String exportTestRunHTML(@PathVariable(value = "id") long id)
			throws ServiceException, JAXBException
	{
		return testRunService.exportTestRunHTML(id);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Mark test run as reviewed", nickname = "markTestRunAsReviewed", code = 200, httpMethod = "POST")
	@RequestMapping(value = "{id}/markReviewed", method = RequestMethod.POST)
	public void markTestRunAsReviewed(@PathVariable(value = "id") long id, @RequestBody @Valid CommentType comment)
			throws ServiceException, JAXBException
	{
		testRunService.addComment(id, comment.getComment());

		TestRun tr = testRunService.getTestRunByIdFull(id);
		tr.setReviewed(true);
		tr = testRunService.updateTestRun(tr);
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(tr));
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Rerun test run", nickname = "rerunTestRun", code = 200, httpMethod = "GET")
	@RequestMapping(value = "{id}/rerun", method = RequestMethod.GET)
	public void rerunTestRun(@PathVariable(value = "id") long id,
			@RequestParam(value = "rerunFailures", required = false, defaultValue = "false") boolean rerunFailures)
			throws ServiceException, JAXBException
	{
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if (testRun == null)
		{
			throw new TestRunNotFoundException();
		}

		if (!jenkinsService.rerunJob(testRun.getJob(), testRun.getBuildNumber(), rerunFailures))
		{
			throw new UnableToRebuildCIJobException();
		}
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Build test run", nickname = "buildTestRun", code = 200, httpMethod = "POST")
	@RequestMapping(value = "{id}/build", method = RequestMethod.POST)
	public void buildTestRun(@PathVariable(value = "id") long id,
			@RequestParam(value = "buildWithParameters", required = false, defaultValue = "true") boolean buildWithParameters,
			@RequestBody Map<String, String> jobParameters) throws ServiceException
	{
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if (testRun == null)
		{
			throw new TestRunNotFoundException();
		}

		if (!jenkinsService.buildJob(testRun.getJob(), testRun.getBuildNumber(), jobParameters, buildWithParameters))
		{
			throw new UnableToRebuildCIJobException();
		}
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get job parameters", nickname = "getjobParameters", code = 200, httpMethod = "GET", response = Map.class)
	@RequestMapping(value = "{id}/jobParameters", method = RequestMethod.GET)
	public @ResponseBody List<BuildParameterType> getjobParameters(@PathVariable(value = "id") long id)
			throws ServiceException
	{
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if (testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		return jenkinsService.getBuildParameters(testRun.getJob(), testRun.getBuildNumber());
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get environments", nickname = "getEnvironments", code = 200, httpMethod = "GET", response = List.class)
	@RequestMapping(value = "environments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getEnvironments() throws ServiceException
	{
		return testRunService.getEnvironments();
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get platforms", nickname = "getPlatforms", code = 200, httpMethod = "GET", response = List.class)
	@RequestMapping(value = "platforms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getPlatforms() throws ServiceException
	{
		return testRunService.getPlatforms();
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get console output from jenkins by test run id", nickname = "getConsoleOutput", code = 200, httpMethod = "GET")
	@RequestMapping(value = "{id}/jobConsoleOutput/{count}/{fullCount}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<Integer, String> getConsoleOutput(@PathVariable(value = "id") long testRunId, @PathVariable(value = "count") int count,
			@PathVariable(value = "fullCount") int fullCount) throws ServiceException
	{
		TestRun testRun = testRunService.getTestRunByIdFull(testRunId);
		return jenkinsService.getBuildConsoleOutputHtml(testRun.getJob(), testRun.getBuildNumber(), count, fullCount);
	}
}
