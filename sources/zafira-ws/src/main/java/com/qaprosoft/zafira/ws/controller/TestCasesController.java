package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.dbaccess.model.Project;
import com.qaprosoft.zafira.dbaccess.model.TestCase;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.ProjectService;
import com.qaprosoft.zafira.services.services.TestCaseService;
import com.qaprosoft.zafira.ws.annotations.PostResponse;
import com.qaprosoft.zafira.ws.dto.TestCaseType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Controller
@Api(value = "testCasesController", description = "Test cases operations")
@RequestMapping("tests/cases")
public class TestCasesController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private TestCaseService testCaseService;
	
	@Autowired
	private ProjectService projectService;

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView index()
	{
		return new ModelAndView("tests/cases/index");
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "metrics", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView metrics()
	{
		return new ModelAndView("tests/cases/metrics");
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<TestCase> searchTestCases(@RequestBody TestCaseSearchCriteria sc) throws ServiceException
	{
		return testCaseService.searchTestRuns(sc);
	}

	@PostResponse
	@ApiOperation(value = "Create test case", nickname = "createTestCase", code = 200, httpMethod = "POST",
			notes = "create a new test case", response = TestCase.class, responseContainer = "TestCase")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestCaseType createTestCase(@RequestBody @Valid TestCaseType testCase, @RequestHeader(value="Project", required=false) String projectName) throws ServiceException
	{
		testCase.setProject(projectService.getProjectByName(projectName));
		return mapper.map(testCaseService.createOrUpdateCase(mapper.map(testCase, TestCase.class)), TestCaseType.class);
	}

	@PostResponse
	@ApiOperation(value = "Create test cases", nickname = "createTestCases", code = 200, httpMethod = "POST",
			notes = "create new test cases", response = java.util.List.class, responseContainer = "TestCase")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="batch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestCaseType [] createTestCases(@RequestBody @Valid TestCaseType [] tcs, @RequestHeader(value="Project", required=false) String projectName) throws ServiceException
	{
		if(!ArrayUtils.isEmpty(tcs))
		{
			Project project = projectService.getProjectByName(projectName);
			TestCase [] testCases = new TestCase[tcs.length];
			for(int i = 0; i < tcs.length; i++)
			{
				tcs[i].setProject(project);
				testCases[i] = mapper.map(tcs[i], TestCase.class);
			}
			testCases = testCaseService.createOrUpdateCases(testCases);
			for(int i = 0; i < testCases.length; i++)
			{
				tcs[i] = mapper.map(testCases[i], TestCaseType.class);
			}
			return tcs;
		}
		else
		{
			return new TestCaseType[0]; 
		}
	}
}