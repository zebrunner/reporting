package com.qaprosoft.zafira.ws.controller;

import javax.validation.Valid;

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
	public @ResponseBody TestType initializeTest(@RequestBody @Valid TestType test) throws ServiceException
	{
		return mapper.map(testService.initializeTest(mapper.map(test, Test.class)), TestType.class);
	}
}
