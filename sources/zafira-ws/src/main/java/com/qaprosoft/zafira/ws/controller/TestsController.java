package com.qaprosoft.zafira.ws.controller;

import java.util.List;

import javax.validation.Valid;

import org.dozer.Mapper;
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
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestService;
import com.qaprosoft.zafira.ws.dto.TestType;

@Controller
@RequestMapping("tests")
public class TestsController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private TestService testService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestType createTest(@RequestBody @Valid TestType test) throws ServiceException
	{
		return mapper.map(testService.createTest(mapper.map(test, Test.class), test.getWorkItems()), TestType.class);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}/workitems", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestType createTestWorkItems(@PathVariable(value="id") long id, @RequestBody List<String> workItems) throws ServiceException
	{
		return mapper.map(testService.createTestWorkItems(id, workItems), TestType.class);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="duplicates/remove", method = RequestMethod.PUT)
	public void deleteTestDuplicates(@RequestBody TestType test) throws ServiceException
	{
		testService.deleteTestByTestRunIdAndTestCaseIdAndLogURL(mapper.map(test, Test.class));
	}
}
