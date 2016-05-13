package com.qaprosoft.zafira.ws.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import org.dozer.Mapper;
import org.dozer.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.TestService;
import com.qaprosoft.zafira.ws.dto.TestRunType;

@Controller
@RequestMapping("tests/runs")
public class TestRunsController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private TestRunService testRunService;
	
	@Autowired
	private TestService testService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType createTestRun(@RequestBody @Valid TestRunType tr) throws ServiceException, MappingException, JAXBException
	{
		TestRun testRun = testRunService.initializeTestRun(mapper.map(tr, TestRun.class), tr.isRerun());
		return mapper.map(testRun, TestRunType.class);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/finish", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType finishTestRun(@PathVariable(value="id") long id) throws ServiceException
	{
		TestRun testRun = testRunService.finilizeTestRun(id);
		return mapper.map(testRun, TestRunType.class);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType getTestRun(@PathVariable(value="id") long id) throws ServiceException
	{
		TestRun testRun = testRunService.getTestRunById(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		return mapper.map(testRun, TestRunType.class);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Test> getTestRunResults(@PathVariable(value="id") long id) throws ServiceException
	{
		return testService.getTestsByTestRunId(id);
	}
	
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
}
