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

import com.qaprosoft.zafira.dbaccess.model.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.ws.dto.TestRunType;

@Controller
@RequestMapping("tests/runs")
public class TestRunsController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private TestRunService testRunService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestRunType createTestRun(@RequestBody @Valid TestRunType testRun) throws ServiceException
	{
		return mapper.map(testRunService.initializeTestRun(mapper.map(testRun, TestRun.class)), TestRunType.class);
	}
}
