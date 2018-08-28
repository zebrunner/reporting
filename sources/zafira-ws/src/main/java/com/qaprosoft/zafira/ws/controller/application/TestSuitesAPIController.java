/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.ws.controller.application;

import javax.validation.Valid;

import com.qaprosoft.zafira.ws.controller.AbstractController;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.TestSuite;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.TestSuiteService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Test suites operations")
@RequestMapping("api/tests/suites")
public class TestSuitesAPIController extends AbstractController
{
	@Autowired
	private Mapper mapper;

	@Autowired
	private TestSuiteService testSuiteService;

	@ResponseStatusDetails
	@ApiOperation(value = "Create test suite", nickname = "createTestSuite", code = 200, httpMethod = "POST", notes = "Create a new test suite.", response = TestSuiteType.class, responseContainer = "TestSuiteType")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestSuiteType createTestSuite(@RequestBody @Valid TestSuiteType testSuite,
			@RequestHeader(value = "Project", required = false) String project) throws ServiceException
	{
		return mapper.map(testSuiteService.createOrUpdateTestSuite(mapper.map(testSuite, TestSuite.class)),
				TestSuiteType.class);
	}
}
