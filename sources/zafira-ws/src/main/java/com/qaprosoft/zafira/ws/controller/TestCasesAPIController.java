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
package com.qaprosoft.zafira.ws.controller;

import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.dozer.Mapper;
import org.dozer.MappingException;
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

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.ProjectService;
import com.qaprosoft.zafira.services.services.TestCaseService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Test cases API")
@RequestMapping("api/tests/cases")
public class TestCasesAPIController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private TestCaseService testCaseService;
	
	@Autowired
	private ProjectService projectService;
	
	@ResponseStatusDetails
	@ApiOperation(value = "Search test cases", nickname = "searchTestCases", code = 200, httpMethod = "POST", response = SearchResult.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<TestCase> searchTestCases(@Valid @RequestBody TestCaseSearchCriteria sc) throws ServiceException
	{
		return testCaseService.searchTestCases(sc);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create test case", nickname = "createTestCase", code = 200, httpMethod = "POST",  response = TestCaseType.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestCaseType createTestCase(@RequestBody @Valid TestCaseType testCase, @RequestHeader(value="Project", required=false) String projectName) throws ServiceException, MappingException, ExecutionException
	{
		TestCase tc = mapper.map(testCase, TestCase.class);
		tc.setProject(projectService.getProjectByName(projectName));
		return mapper.map(testCaseService.createOrUpdateCase(tc), TestCaseType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create multiple test cases", nickname = "createTestCases", code = 200, httpMethod = "POST", response = TestCaseType[].class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="batch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TestCaseType [] createTestCases(@RequestBody @Valid TestCaseType [] tcs, @RequestHeader(value="Project", required=false) String projectName) throws ServiceException, ExecutionException
	{
		if(!ArrayUtils.isEmpty(tcs))
		{
			Project project = projectService.getProjectByName(projectName);
			TestCase [] testCases = new TestCase[tcs.length];
			for(int i = 0; i < tcs.length; i++)
			{
				testCases[i] = mapper.map(tcs[i], TestCase.class);
				testCases[i].setProject(project);
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