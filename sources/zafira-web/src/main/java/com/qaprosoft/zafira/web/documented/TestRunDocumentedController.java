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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.JobSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.BuildParameterType;
import com.qaprosoft.zafira.models.dto.CommentType;
import com.qaprosoft.zafira.models.dto.EmailType;
import com.qaprosoft.zafira.models.dto.QueueTestRunParamsType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api("Test runs API")
public interface TestRunDocumentedController {

    @ApiOperation(
            value = "Creates test run",
            notes = "Returns registered test run",
            nickname = "startTestRun",
            httpMethod = "POST",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "testRunType", paramType = "body", dataType = "TestRunType", required = true, value = "Test run to create"),
            @ApiImplicitParam(name = "project", paramType = "header", dataType = "string", value = "Test run project")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns registered test run", response = TestRunType.class),
            @ApiResponse(code = 400, message = "Indicates that startedBy property has an incorrect behavior", response = ErrorResponse.class)
    })
    TestRunType startTestRun(TestRunType testRunType, String project);

    @ApiOperation(
            value = "Updates existing test run",
            notes = "Returns updated test run",
            nickname = "updateTestRun",
            httpMethod = "PUT",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "testRunType", paramType = "body", dataType = "TestRunType", required = true, value = "Test run to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated test run", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    TestRunType updateTestRun(TestRunType testRunType);

    @ApiOperation(
            value = "Finishes test run",
            notes = "Initializes on finish test run data",
            nickname = "finishTestRun",
            httpMethod = "POST",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns finished test run", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    TestRunType finishTestRun(long id);

    @ApiOperation(
            value = "Aborts test run",
            notes = "Aborts test run with IN_PROGRESS or QUEUED statuses only",
            nickname = "abortTestRun",
            httpMethod = "POST",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "query", dataType = "number", value = "Test run id"),
            @ApiImplicitParam(name = "ciRunId", paramType = "query", dataType = "string", value = "Test run ciRun id"),
            @ApiImplicitParam(name = "abortCause", paramType = "body", dataType = "CommentType", value = "Abort cause comment")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns aborted test run", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    TestRunType abortTestRun(Long id, String ciRunId, CommentType abortCause);

    @ApiOperation(
            value = "Creates queued test run",
            notes = "Queued test run is a predicted value about possible run based on previous job run",
            nickname = "createQueuedTestRun",
            httpMethod = "POST",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "queuedTestRunParams", paramType = "body", dataType = "QueueTestRunParamsType", required = true, value = "Job params to create test run")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created queued test run", response = TestRunType.class)
    })
    TestRunType createQueuedTestRun(QueueTestRunParamsType queuedTestRunParams);

    @ApiOperation(
            value = "Retrieves test run by id",
            notes = "Returns found test run",
            nickname = "getTestRun",
            httpMethod = "GET",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test cases", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    TestRunType getTestRun(long id);

    @ApiOperation(
            value = "Searches test runs by criteria",
            notes = "Returns found test runs",
            nickname = "searchTestRuns",
            httpMethod = "GET",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "sc", paramType = "body", dataType = "TestRunSearchCriteria", required = true, value = "Search criteria"),
            @ApiImplicitParam(name = "projectNames", paramType = "query", dataType = "array", value = "Project names to search"),
            @ApiImplicitParam(name = "filterId", paramType = "query", dataType = "number", value = "Filter id to search")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test runs", response = SearchResult.class)
    })
    SearchResult<TestRun> searchTestRuns(TestRunSearchCriteria sc, List<String> projectNames, Long filterId) throws IOException;

    @ApiOperation(
            value = "Searches test cases by criteria",
            notes = "Returns found test cases",
            nickname = "rerunJobs",
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
    List<TestRunType> rerunJobs(boolean doRebuild, boolean rerunFailures, JobSearchCriteria sc);

    @ApiOperation(
            value = "Retrieves test run by ci run id",
            notes = "Returns found test run",
            nickname = "getTestRunByCiRunId",
            httpMethod = "GET",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "ciRunId", paramType = "query", dataType = "string", required = true, value = "Test run ci run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test cases", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    TestRunType getTestRunByCiRunId(String ciRunId);

    @ApiOperation(
            value = "Retrieves tests by test run id",
            notes = "Returns found tests",
            nickname = "getTestRunResults",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found tests", response = List.class)
    })
    List<TestType> getTestRunResults(long id);

    @ApiOperation(
            value = "Collect test runs data",
            notes = "Joins identical tests and adds other at the end of structure",
            nickname = "createCompareMatrix",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "testRunIds", paramType = "path", dataType = "string", required = true, value = "Test run ids concatenated using plus(+) symbol")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns built structure", response = Map.class)
    })
    Map<Long, Map<String, Test>> createCompareMatrix(String testRunIds);

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
    void deleteTestRun(long id);

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
    String sendTestRunResultsEmail(String id, EmailType email, String filter, boolean showStacktrace);

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
    String sendTestRunFailureEmail(String id, EmailType email, boolean suiteOwner, boolean suiteRunner);

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
    String exportTestRunHTML(String id);

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
    void markTestRunAsReviewed(long id, CommentType comment);

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
    void rerunTestRun(long id, boolean rerunFailures);

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
    void debugTestRun(long id);

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
    void abortCIJob(Long id, String ciRunId);

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
    void buildTestRun(long id, Map<String, String> jobParameters);

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
    List<BuildParameterType> getJobParameters(long id);

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
    List<String> getEnvironments();

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
    List<String> getPlatforms();

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
    Map<Integer, String> getConsoleOutput(int count, int fullCount, Long id, String ciRunId);

}
