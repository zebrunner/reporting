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
import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.TestRun;
import com.qaprosoft.zafira.dbaccess.model.push.TestRunPush;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;
import com.qaprosoft.zafira.services.services.ProjectService;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.TestService;
import com.qaprosoft.zafira.ws.dto.EmailType;
import com.qaprosoft.zafira.ws.dto.TestRunType;
import com.qaprosoft.zafira.ws.dto.TestType;
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
	private ProjectService projectService;
	
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
		tr.setProject(projectService.getProjectByName(project));
		TestRun testRun = testRunService.startTestRun(mapper.map(tr, TestRun.class));
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
		testRunService.updateTestRun(testRun);
		return mapper.map(testRun, TestRunType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Finish test run", nickname = "finishTestRun", code = 200, httpMethod = "POST",
			notes = "Finishes test run.", response = TestRunType.class, responseContainer = "TestRunType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/finish", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType finishTestRun(@ApiParam(value = "Id of the test-run", required = true) @PathVariable(value="id") long id) throws ServiceException
	{
		TestRun testRun = testRunService.calculateTestRunResult(id, true);
		TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRunFull));
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
	public void deleteTestRun(@PathVariable(value="id") long id) throws ServiceException
	{
		testRunService.deleteTestRunById(id);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/email", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String sendTestRunResultsEmail(@PathVariable(value="id") long id, @RequestBody @Valid EmailType email, @RequestParam(value="filter", defaultValue="all", required=false) String filter) throws ServiceException, JAXBException
	{
		return testRunService.sendTestRunResultsEmail(id, "failures".equals(filter), email.getRecipients().trim().replaceAll(",", " ").replaceAll(";", " ").split(" "));
	}
}
