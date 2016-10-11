package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestSearchCriteria;
import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.TestMetric;
import com.qaprosoft.zafira.dbaccess.model.TestRun;
import com.qaprosoft.zafira.dbaccess.model.push.TestPush;
import com.qaprosoft.zafira.dbaccess.model.push.TestRunPush;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestMetricService;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.TestService;
import com.qaprosoft.zafira.ws.annotations.DeleteResponse;
import com.qaprosoft.zafira.ws.annotations.PostResponse;
import com.qaprosoft.zafira.ws.dto.TestType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@Api(value = "testsController", description = "Tests operations")
@RequestMapping("tests")
public class TestsController extends AbstractController
{
	private static final Logger LOGGER = Logger.getLogger(TestsController.class);
	
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private TestService testService;
	
	@Autowired
	private TestMetricService testMetricService;
	
	@Autowired
	private TestRunService testRunService;
	
	@Autowired
	private SimpMessagingTemplate websocketTemplate;

	@PostResponse
	@ApiOperation(value = "Start test", nickname = "startTest", code = 200, httpMethod = "POST",
			notes = "start test", response = TestType.class, responseContainer = "TestType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestType startTest(@RequestBody @Valid TestType t, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		Test test = testService.startTest(mapper.map(t, Test.class), t.getWorkItems(), t.getConfigXML());
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));
		return mapper.map(test, TestType.class);
	}

	@PostResponse
	@ApiOperation(value = "Finish test", nickname = "finishTest", code = 200, httpMethod = "POST",
			notes = "finish test", response = TestType.class, responseContainer = "TestType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/finish", method = RequestMethod.POST)
	public @ResponseBody TestType finishTest(@ApiParam(value = "Id of the test", required = true) @PathVariable(value="id") long id, @RequestBody TestType t) throws ServiceException
	{
		t.setId(id);
		Test test = testService.finishTest(mapper.map(t, Test.class));
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));
		
		try
		{
			Map<String, Long> testMetrics = t.getTestMetrics();
			if(testMetrics != null)
			{
				for(String key : testMetrics.keySet())
				{
					testMetricService.createTestMetric(new TestMetric(key, testMetrics.get(key), test.getId()));
				}
			}
		}
		catch(Exception e)
		{
			LOGGER.error("Unable to register test metrics: " + e.getMessage());
		}
		
		return mapper.map(test, TestType.class);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/passed", method = RequestMethod.POST)
	public @ResponseBody TestType markTestAsPassed(@PathVariable(value="id") long id) throws ServiceException
	{
		Test test = testService.markTestAsPassed(id);
		TestRun testRun = testRunService.recalculateTestRunResult(test.getTestRunId());
		if(testRun != null) 
		{
			websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(testRun));
		}
		websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestPush(test));
		return mapper.map(test, TestType.class);
	}

	@PostResponse
	@ApiOperation(value = "Create test work item", nickname = "createTestWorkItem", code = 200, httpMethod = "POST",
			notes = "create a new test work item", response = TestType.class, responseContainer = "TestType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/workitems", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestType createTestWorkItems(@ApiParam(value = "Id of the test work item", required = true) @PathVariable(value="id") long id, @RequestBody List<String> workItems) throws ServiceException
	{
		return mapper.map(testService.createTestWorkItems(id, workItems), TestType.class);
	}

	@DeleteResponse
	@ApiOperation(value = "Delete test dublicates by test type", nickname = "deleteTestDublicates", code = 200, httpMethod = "DELETE",
			notes = "delete test dublicates by test type", response = TestType.class, responseContainer = "TestType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="duplicates/remove", method = RequestMethod.PUT)
	public void deleteTestDuplicates(@RequestBody TestType test) throws ServiceException
	{
		testService.deleteTestByTestRunIdAndTestCaseIdAndLogURL(mapper.map(test, Test.class));
	}

	@DeleteResponse
	@ApiOperation(value = "Delete test by id", nickname = "deleteTest", code = 200, httpMethod = "DELETE",
			notes = "delete test by id", response = Test.class, responseContainer = "Test")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteTest(@ApiParam(value = "Id of the test", required = true) @PathVariable(value="id") long id) throws ServiceException
	{
		testService.deleteTestById(id);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<Test> searchTests(@RequestBody TestSearchCriteria sc) throws ServiceException
	{
		return testService.searchTests(sc);
	}
}
