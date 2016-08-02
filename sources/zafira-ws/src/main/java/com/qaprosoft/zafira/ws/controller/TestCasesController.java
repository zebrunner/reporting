package com.qaprosoft.zafira.ws.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.statistics.TestCaseImplementationCount;
import com.qaprosoft.zafira.dbaccess.dao.mysql.statistics.TestCaseOwnersCount;
import com.qaprosoft.zafira.dbaccess.model.TestCase;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestCaseService;
import com.qaprosoft.zafira.ws.dto.TestCaseType;

@Controller
@RequestMapping("tests/cases")
public class TestCasesController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private TestCaseService testCaseService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView index()
	{
		return new ModelAndView("tests/cases/index");
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<TestCase> searchTestCases(@RequestBody TestCaseSearchCriteria sc) throws ServiceException
	{
		return testCaseService.searchTestRuns(sc);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestCaseType createTestCase(@RequestBody @Valid TestCaseType testCase) throws ServiceException
	{
		return mapper.map(testCaseService.createOrUpdateCase(mapper.map(testCase, TestCase.class)), TestCaseType.class);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="batch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestCaseType [] createTestCases(@RequestBody @Valid TestCaseType [] tcs) throws ServiceException
	{
		if(!ArrayUtils.isEmpty(tcs))
		{
			TestCase [] testCases = new TestCase[tcs.length];
			for(int i = 0; i < tcs.length; i++)
			{
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
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="owners/statistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody  List<TestCaseOwnersCount> getTestCaseOwnersStatistics() throws ServiceException
	{
		return testCaseService.getTestCaseOwnersStatistics();
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="implementation/statistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody  List<TestCaseImplementationCount> getTestCaseImplementationStatistics() throws ServiceException
	{
		return testCaseService.getTestCaseImplementationStatistics();
	}
}