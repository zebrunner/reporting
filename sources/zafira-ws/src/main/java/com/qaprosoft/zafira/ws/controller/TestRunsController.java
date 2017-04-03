package com.qaprosoft.zafira.ws.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;
import com.qaprosoft.zafira.services.exceptions.UnableToRebuildCIJobException;
import com.qaprosoft.zafira.services.services.JenkinsService;
import com.qaprosoft.zafira.services.services.ProjectService;
import com.qaprosoft.zafira.services.services.TestConfigService;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.TestService;
import com.qaprosoft.zafira.models.dto.CommentType;
import com.qaprosoft.zafira.models.dto.EmailType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@Api(value = "Test runs operations")
@RequestMapping("tests/runs")
public class TestRunsController extends AbstractController
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
	private SimpMessagingTemplate websocketTemplate;

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView index()
	{
		return new ModelAndView("tests/runs/index");
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Start test run", nickname = "startTestRun", code = 200, httpMethod = "POST",
			notes = "Starts new test run.", response = TestRunType.class, responseContainer = "TestRunType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType startTestRun(@RequestBody @Valid TestRunType tr, @RequestHeader(value="Project", required=false) String project) throws ServiceException, MappingException, JAXBException
	{
		TestRun testRun = mapper.map(tr, TestRun.class);
		testRun.setProject(projectService.getProjectByName(project));	
		testRun = testRunService.startTestRun(testRun);
		TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRunFull));
		return mapper.map(testRun, TestRunType.class);
	}
	
	@ResponseStatusDetails
	@ApiOperation(value = "Update test run config", nickname = "updateTestRun", code = 200, httpMethod = "PUT",
			notes = "Updates new test run config.", response = TestRunType.class, responseContainer = "TestRunType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType updateTestRun(@RequestBody TestRunType tr) throws ServiceException, MappingException, JAXBException
	{
		TestRun testRun = testRunService.getTestRunById(tr.getId());
		if(testRun == null && !StringUtils.isEmpty(tr.getConfigXML()))
		{
			throw new ServiceException("Test run not found by id: " + tr.getId());
		}
		testRun.setConfigXML(tr.getConfigXML());
		// TODO: remove that ASAP from controller
		for(Argument arg : testConfigService.readConfigArgs(testRun.getConfigXML(), false))
		{
			if("app_version".equals(arg.getKey()))
			{
				testRun.setAppVersion(arg.getValue());
			}
		}
		testRunService.updateTestRun(testRun);
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Finish test run", nickname = "finishTestRun", code = 200, httpMethod = "POST",
			notes = "Finishes test run.", response = TestRunType.class, responseContainer = "TestRunType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/finish", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType finishTestRun(@ApiParam(value = "Id of the test-run", required = true) @PathVariable(value="id") long id) throws ServiceException, InterruptedException
	{
		TestRun testRun = testRunService.calculateTestRunResult(id, true);
		TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRunFull));
		return mapper.map(testRun, TestRunType.class);
	}
	
	@ResponseStatusDetails
	@ApiOperation(value = "Abort test run", nickname = "abortTestRun", code = 200, httpMethod = "POST",
			notes = "Aborts test run.", response = TestRunType.class, responseContainer = "TestRunType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="abort", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType abortTestRun(@ApiParam(value = "Test run id", required = true) @RequestParam(value="id", required=false) Long id,
												  @ApiParam(value = "Test run CI id", required = true) @RequestParam(value="ciRunId", required=false) String ciRunId) throws ServiceException, InterruptedException
	{
		if(id == null && ciRunId == null) 
		{
			throw new ServiceException("Id or CI run ID should be set!");
		}
		
		TestRun testRun = id != null ? testRunService.getTestRunById(id) : testRunService.getTestRunByCiRunId(ciRunId);;
		testRunService.abortTestRun(testRun);
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRunService.getTestRunByIdFull(testRun.getId())));
		
		for(Test test : testService.getTestsByTestRunId(testRun.getId()))
		{
			if(Status.ABORTED.equals(test.getStatus()))
			{
				websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));
			}
		}
		
		return mapper.map(testRun, TestRunType.class);
	}
	
	@ResponseStatusDetails
	@ApiOperation(value = "Get test run", nickname = "getTestRun", code = 200, httpMethod = "GET",
			notes = "Returns test run by id.", response = TestRunType.class, responseContainer = "TestRunType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType getTestRun(@ApiParam(value = "Id of the test-run", required = true) @PathVariable(value="id") long id) throws ServiceException
	{
		TestRun testRun = testRunService.getTestRunById(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		return mapper.map(testRun, TestRunType.class);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<TestRun> searchTestRuns(@RequestBody TestRunSearchCriteria sc) throws ServiceException
	{
		return testRunService.searchTestRuns(sc);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get test run by ci run id", nickname = "getTestRunByCiRunId", code = 200, httpMethod = "GET",
			notes = "Returns test run by ci run id.", response = TestRunType.class, responseContainer = "TestRunType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType getTestRunByCiRunId(@RequestParam(value="ciRunId") String ciRunId) throws ServiceException
	{
		TestRun testRun = testRunService.getTestRunByCiRunId(ciRunId);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get test run results by id", nickname = "getTestRunResults", code = 200, httpMethod = "GET",
			notes = "Returns test run results by id.", response = java.util.List.class, responseContainer = "Test")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<TestType> getTestRunResults(@PathVariable(value="id") long id) throws ServiceException
	{
		List<TestType> tests = new ArrayList<>();
		for(Test test : testService.getTestsByTestRunId(id))
		{
			tests.add(mapper.map(test, TestType.class));
		}
		return tests;
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{ids}/compare", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<Long, Map<String, Test>> createCompareMatrix(@PathVariable(value="ids") String testRunIds) throws ServiceException
	{
		List<Long> ids = new ArrayList<>();
		for(String id : testRunIds.split("\\+"))
		{
			ids.add(Long.valueOf(id));
		}
		return testRunService.createCompareMatrix(ids);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="compare", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView compareTestRuns()
	{
		return new ModelAndView("tests/runs/compare");
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	@Secured({"ROLE_ADMIN"})
	public void deleteTestRun(@PathVariable(value="id") long id) throws ServiceException
	{
		testRunService.deleteTestRunById(id);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/email", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String sendTestRunResultsEmail(@PathVariable(value="id") long id, @RequestBody @Valid EmailType email, @RequestParam(value="filter", defaultValue="all", required=false) String filter,
														@RequestParam(value = "showStacktrace", defaultValue = "true", required = false) boolean showStacktrace) throws ServiceException, JAXBException
	{
		String [] recipients = !StringUtils.isEmpty(email.getRecipients()) ? email.getRecipients().trim().replaceAll(",", " ").replaceAll(";", " ").split(" ") : new String[]{};
		return testRunService.sendTestRunResultsEmail(id, "failures".equals(filter), showStacktrace, recipients);
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/comment", method = RequestMethod.POST)
	public void commentTestRun(@PathVariable(value="id") long id, @RequestBody @Valid CommentType comment) throws ServiceException, JAXBException
	{
		testRunService.addComment(id, comment.getComment());
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/rerun", method = RequestMethod.GET)
	public void rerunTestRun(@PathVariable(value="id") long id, @RequestParam(value="rerunFailures", required=false, defaultValue="false") boolean rerunFailures) throws ServiceException, JAXBException
	{
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		
		if(!jenkinsService.rerunJob(testRun.getJob(), testRun.getBuildNumber(), rerunFailures))
		{
			throw new UnableToRebuildCIJobException();
		}
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/build", method = RequestMethod.POST)
	public void buildTestRun(@PathVariable(value = "id") long id, @RequestParam(value="buildWithParameters", required=false, defaultValue="true") boolean buildWithParameters,
							 @RequestBody Map<String, String> jobParameters) throws ServiceException {
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}

		if(!jenkinsService.buildJob(testRun.getJob(), testRun.getBuildNumber(), jobParameters, buildWithParameters))
		{
			throw new UnableToRebuildCIJobException();
		}
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/jobParameters", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> getjobParameters(@PathVariable(value = "id") long id) throws ServiceException {
		TestRun testRun = testRunService.getTestRunByIdFull(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		return jenkinsService.getBuildParameters(testRun.getJob(), testRun.getBuildNumber());
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="environments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getEnvironments() throws ServiceException
	{
		return testRunService.getEnvironments();
	}
}