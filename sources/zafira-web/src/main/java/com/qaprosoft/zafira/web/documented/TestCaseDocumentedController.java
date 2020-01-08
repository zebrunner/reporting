/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.models.db.TestMetric;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Api("Test cases API")
public interface TestCaseDocumentedController {

    @ApiOperation(
            value = "Searches test cases by criteria",
            notes = "Returns found test cases",
            nickname = "searchTestCases",
            httpMethod = "POST",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "sc", paramType = "body", dataType = "TestCaseSearchCriteria", required = true, value = "Search criteria")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test cases", response = SearchResult.class)
    })
    SearchResult<TestCase> searchTestCases(TestCaseSearchCriteria sc);

    @ApiOperation(
            value = "Retrieve test metrics by test case id",
            notes = "Returns found test metrics",
            nickname = "getTestMetricsByTestCaseId",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test case id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test metrics", response = Map.class)
    })
    Map<String, List<TestMetric>> getTestMetricsByTestCaseId(Long id);

    @ApiOperation(
            value = "Creates or updates test cases",
            notes = "Returns created or updated test case",
            nickname = "createTestCase",
            httpMethod = "POST",
            response = TestCaseType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "testCase", paramType = "body", dataType = "TestCaseType", required = true, value = "Test case to create or update"),
            @ApiImplicitParam(name = "projectName", paramType = "header", dataType = "string", value = "Project name to attache to test case")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created or updated test case", response = TestCaseType.class)
    })
    TestCaseType createTestCase(TestCaseType testCase, String projectName) throws ExecutionException;

    @ApiOperation(
            value = "Batch creates or updates test cases",
            notes = "Returns created or updated test cases",
            nickname = "createTestCases",
            httpMethod = "POST",
            response = TestCaseType[].class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "tcs", paramType = "body", dataType = "array", required = true, value = "Test cases to create or update"),
            @ApiImplicitParam(name = "projectName", paramType = "header", dataType = "string", value = "Project name to attache to test cases")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created or updated test cases", response = TestCaseType[].class)
    })
    TestCaseType[] createTestCases(TestCaseType[] tcs, String projectName) throws ExecutionException;

}
