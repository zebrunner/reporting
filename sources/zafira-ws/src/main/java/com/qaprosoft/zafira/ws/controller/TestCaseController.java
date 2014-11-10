package com.qaprosoft.zafira.ws.controller;

import javax.validation.Valid;

import org.apache.commons.lang.ArrayUtils;
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

import com.qaprosoft.zafira.dbaccess.model.TestCase;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestCaseService;
import com.qaprosoft.zafira.ws.dto.TestCaseType;

@Controller
@RequestMapping("tests/cases")
public class TestCaseController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private TestCaseService testCaseService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestCaseType [] createTestCases(@RequestBody @Valid TestCaseType [] tcs) throws ServiceException
	{
		if(!ArrayUtils.isEmpty(tcs))
		{
			TestCase [] testCases = new TestCase[tcs.length];
			for(int i = 0; i < tcs.length; i++)
			{
				testCases[i] = mapper.map(tcs[i], TestCase.class);
			}
			testCases = testCaseService.initiateTestCases(testCases);
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