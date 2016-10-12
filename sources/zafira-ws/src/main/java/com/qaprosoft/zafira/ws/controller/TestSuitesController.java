package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.dbaccess.model.TestSuite;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestSuiteService;
import com.qaprosoft.zafira.ws.annotations.PostResponse;
import com.qaprosoft.zafira.ws.dto.TestSuiteType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@Api(value = "testSuitesController", description = "Test suites operations")
@RequestMapping("tests/suites")
public class TestSuitesController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private TestSuiteService testSuiteService;

	@PostResponse
	@ApiOperation(value = "Create test suite", nickname = "createTestSuite", code = 200, httpMethod = "POST",
			notes = "create a new Test Suite", response = TestSuiteType.class, responseContainer = "TestSuiteType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestSuiteType createTestSuite(@RequestBody @Valid TestSuiteType testSuite, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		return mapper.map(testSuiteService.createOrUpdateTestSuite(mapper.map(testSuite, TestSuite.class)), TestSuiteType.class);
	}
}
