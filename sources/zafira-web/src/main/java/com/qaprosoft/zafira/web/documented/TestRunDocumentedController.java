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
            value = "Reruns test run jobs by search criteria",
            notes = "Returns restarted test runs",
            nickname = "rerunJobs",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "doRebuild", paramType = "query", dataType = "boolean", value = "Flag must be true for rerun action"),
            @ApiImplicitParam(name = "rerunFailures", paramType = "query", dataType = "boolean", value = "Indicates that only failed tests will be restarted"),
            @ApiImplicitParam(name = "sc", paramType = "body", dataType = "JobSearchCriteria", required = true, value = "Search criteria")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns restarted test runs", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that test automation integration does not exist", response = ErrorResponse.class)
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
            value = "Deletes test run by id",
            nickname = "deleteTestRun",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Test run was deleted successfully")
    })
    void deleteTestRun(long id);

    @ApiOperation(
            value = "Sends test run result via email",
            notes = "Collects test run result data and sends report via email",
            nickname = "sendTestRunResultsEmail",
            httpMethod = "POST",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id"),
            @ApiImplicitParam(name = "email", paramType = "body", dataType = "EmailType", required = true, value = "Email to send"),
            @ApiImplicitParam(name = "filter", paramType = "query", dataType = "string", value = "Test run result filter (failures)"),
            @ApiImplicitParam(name = "showStacktrace", paramType = "query", dataType = "boolean", value = "Indicates test logs visibility")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Email was sent successfully. Returns email content", response = String.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    String sendTestRunResultsEmail(String id, EmailType email, String filter, boolean showStacktrace);

    @ApiOperation(
            value = "Sends test run result via email",
            notes = "Collects test run result data and sends report via email",
            nickname = "sendTestRunFailureEmail",
            httpMethod = "POST",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id"),
            @ApiImplicitParam(name = "email", paramType = "body", dataType = "EmailType", required = true, value = "Email to send"),
            @ApiImplicitParam(name = "suiteOwner", paramType = "query", dataType = "boolean", value = "Indicates that email will be sent to suite owner"),
            @ApiImplicitParam(name = "suiteRunner", paramType = "query", dataType = "boolean", value = "Indicates that email will be sent to suite runner")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Email was sent successfully. Returns email content", response = String.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    String sendTestRunFailureEmail(String id, EmailType email, boolean suiteOwner, boolean suiteRunner);

    @ApiOperation(
            value = "Builds test run result html as string",
            notes = "Returns built test run results html",
            nickname = "exportTestRunHTML",
            httpMethod = "GET",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns built test run results html", response = String.class)
    })
    String exportTestRunHTML(String id);

    @ApiOperation(
            value = "Marks test run as reviewed",
            notes = "Attaches comment and marks as reviewed",
            nickname = "markTestRunAsReviewed",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id"),
            @ApiImplicitParam(name = "comment", paramType = "body", dataType = "CommentType", required = true, value = "Test run comment")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Test run was marked as reviewed successfully"),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    void markTestRunAsReviewed(long id, CommentType comment);

    @ApiOperation(
            value = "Rerun test run by id",
            notes = "Rerun test run by id(all tests ar failed only)",
            nickname = "rerunTestRun",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id"),
            @ApiImplicitParam(name = "rerunFailures", paramType = "query", dataType = "boolean", value = "Indicates that will be ran failed tests only")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Test run was re-ran successfully"),
            @ApiResponse(code = 400, message = "Indicates that test run is passed but flag is rerun failures", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    void rerunTestRun(long id, boolean rerunFailures);

    @ApiOperation(
            value = "Starts debug job",
            notes = "Needs to debug test run remotely",
            nickname = "debugTestRun",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test cases"),
            @ApiResponse(code = 400, message = "Indicates that test run is passed but flag is rerun failures or test automation integration does not exist", response = ErrorResponse.class)
    })
    void debugTestRun(long id);

    @ApiOperation(
            value = "Aborts test run job or debug process",
            nickname = "abortCIJob",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "query", dataType = "number", value = "Test run id"),
            @ApiImplicitParam(name = "ciRunId", paramType = "query", dataType = "string", value = "Test run ci run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Test run job or debug process was aborted successfully"),
            @ApiResponse(code = 400, message = "Indicates that test automation integration does not exist", response = ErrorResponse.class)
    })
    void abortCIJob(Long id, String ciRunId);

    @ApiOperation(
            value = "Builds test run job",
            notes = "Builds test run job using custom provided job parameters",
            nickname = "buildTestRun",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id"),
            @ApiImplicitParam(name = "jobParameters", paramType = "body", dataType = "Map", required = true, value = "Job parameters")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Test run job was built successfully"),
            @ApiResponse(code = 400, message = "Indicates that test automation integration does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    void buildTestRun(long id, Map<String, String> jobParameters);

    @ApiOperation(
            value = "Retrieves job parameters from test run job by id",
            nickname = "getJobParameters",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test cases", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that test automation integration does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    List<BuildParameterType> getJobParameters(long id);

    @ApiOperation(
            value = "Retrieves all test run environments",
            notes = "Returns found test run environments",
            nickname = "getEnvironments",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test run environments", response = List.class)
    })
    List<String> getEnvironments();

    @ApiOperation(
            value = "Retrieves all test run platforms",
            notes = "Returns found test run platforms",
            nickname = "getPlatforms",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test run platforms", response = List.class)
    })
    List<String> getPlatforms();

    @ApiOperation(
            value = "Retrieves test run job console lines",
            notes = "Returns found lines",
            nickname = "getConsoleOutput",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "count", paramType = "path", dataType = "number", required = true, value = "Test run job console lines count"),
            @ApiImplicitParam(name = "fullCount", paramType = "path", dataType = "number", required = true, value = "Offset from which will be searching start"),
            @ApiImplicitParam(name = "id", paramType = "query", dataType = "number", value = "Test run id"),
            @ApiImplicitParam(name = "ciRunId", paramType = "query", dataType = "string", value = "Test run ci run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found lines", response = Map.class),
            @ApiResponse(code = 400, message = "Indicates that test automation integration does not exist", response = ErrorResponse.class)
    })
    Map<Integer, String> getConsoleOutput(int count, int fullCount, Long id, String ciRunId);

}
