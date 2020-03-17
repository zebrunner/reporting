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
package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.JobSearchCriteria;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestRunSearchCriteria;
import com.zebrunner.reporting.domain.db.Test;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.dto.BuildParameterType;
import com.zebrunner.reporting.domain.dto.CommentType;
import com.zebrunner.reporting.domain.dto.EmailType;
import com.zebrunner.reporting.domain.dto.QueueTestRunParamsType;
import com.zebrunner.reporting.domain.dto.TestRunType;
import com.zebrunner.reporting.domain.dto.TestType;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
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
            value = "Creates a test run",
            notes = "Returns the registered test run",
            nickname = "startTestRun",
            httpMethod = "POST",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "testRunType", paramType = "body", dataType = "TestRunType", required = true, value = "The test run to create"),
            @ApiImplicitParam(name = "project", paramType = "header", dataType = "string", value = "The test run project")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the registered test run", response = TestRunType.class),
            @ApiResponse(code = 400, message = "Indicates that the startedBy property has an incorrect behavior", response = ErrorResponse.class)
    })
    TestRunType startTestRun(TestRunType testRunType, String project);

    @ApiOperation(
            value = "Updates an existing test run",
            notes = "Returns the updated test run",
            nickname = "updateTestRun",
            httpMethod = "PUT",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "testRunType", paramType = "body", dataType = "TestRunType", required = true, value = "The test run to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated test run", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that the test run does not exist", response = ErrorResponse.class)
    })
    TestRunType updateTestRun(TestRunType testRunType);

    @ApiOperation(
            value = "Finishes a test run",
            notes = "Initializes on finish test run data",
            nickname = "finishTestRun",
            httpMethod = "POST",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the finished test run", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that the test run does not exist", response = ErrorResponse.class)
    })
    TestRunType finishTestRun(long id);

    @ApiOperation(
            value = "Aborts a test run",
            notes = "Aborts a test run with IN_PROGRESS or QUEUED statuses only",
            nickname = "abortTestRun",
            httpMethod = "POST",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "query", dataTypeClass = Long.class, value = "The test run id"),
            @ApiImplicitParam(name = "ciRunId", paramType = "query", dataType = "string", value = "The test run ciRun id"),
            @ApiImplicitParam(name = "abortCause", paramType = "body", dataType = "CommentType", value = "A comment about the abort cause")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the aborted test run", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    TestRunType abortTestRun(Long id, String ciRunId, CommentType abortCause);

    @ApiOperation(
            value = "Creates a queued test run",
            notes = "Queued test run is a predicted value about possible run based on previous job run",
            nickname = "createQueuedTestRun",
            httpMethod = "POST",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "queuedTestRunParams", paramType = "body", dataType = "QueueTestRunParamsType", required = true, value = "The job parameters to create the test run")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created queued test run", response = TestRunType.class)
    })
    TestRunType createQueuedTestRun(QueueTestRunParamsType queuedTestRunParams);

    @ApiOperation(
            value = "Retrieves a test run by its id",
            notes = "Returns the found test run",
            nickname = "getTestRun",
            httpMethod = "GET",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found test run", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that the test run does not exist", response = ErrorResponse.class)
    })
    TestRunType getTestRun(long id);

    @ApiOperation(
            value = "Searches for test runs by criteria",
            notes = "Returns found test runs",
            nickname = "searchTestRuns",
            httpMethod = "GET",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "sc", paramType = "body", dataType = "TestRunSearchCriteria", required = true, value = "Search criteria"),
            @ApiImplicitParam(name = "projectNames", paramType = "query", dataType = "array", value = "The names of the projects to search"),
            @ApiImplicitParam(name = "filterId", paramType = "query", dataTypeClass = Long.class, value = "The filter id to search")
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
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "doRebuild", paramType = "query", dataType = "boolean", value = "The flag must true to start a rerun"),
            @ApiImplicitParam(name = "rerunFailures", paramType = "query", dataType = "boolean", value = "Indicates that only failed tests will be restarted"),
            @ApiImplicitParam(name = "sc", paramType = "body", dataType = "JobSearchCriteria", required = true, value = "Search criteria")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns restarted test runs", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that the test automation integration does not exist", response = ErrorResponse.class)
    })
    List<TestRunType> rerunJobs(boolean doRebuild, boolean rerunFailures, JobSearchCriteria sc);

    @ApiOperation(
            value = "Retrieves a test run by the CI run id",
            notes = "Returns the found test run",
            nickname = "getTestRunByCiRunId",
            httpMethod = "GET",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "ciRunId", paramType = "query", dataType = "string", required = true, value = "The test run CI run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found test run", response = TestRunType.class),
            @ApiResponse(code = 404, message = "Indicates that the test run does not exist", response = ErrorResponse.class)
    })
    TestRunType getTestRunByCiRunId(String ciRunId);

    @ApiOperation(
            value = "Retrieves tests by the test run id",
            notes = "Returns found tests",
            nickname = "getTestRunResults",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found tests", response = List.class)
    })
    List<TestType> getTestRunResults(long id);

    @ApiOperation(
            value = "Collects test run data",
            notes = "Joins identical tests and adds other at the end of structure",
            nickname = "createCompareMatrix",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "testRunIds", paramType = "path", dataType = "string", required = true, value = "The test run ids concatenated with a plus (+) symbol")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the built structure", response = Map.class)
    })
    Map<Long, Map<String, Test>> createCompareMatrix(String testRunIds);

    @ApiOperation(
            value = "Deletes a test run by its id",
            nickname = "deleteTestRun",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The test run was deleted successfully")
    })
    void deleteTestRun(long id);

    @ApiOperation(
            value = "Sends test run results via email",
            notes = "Collects test run result data and sends a report via email",
            nickname = "sendTestRunResultsEmail",
            httpMethod = "POST",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "string", required = true, value = "The test run id"),
            @ApiImplicitParam(name = "email", paramType = "body", dataType = "EmailType", required = true, value = "The email to send the report to"),
            @ApiImplicitParam(name = "filter", paramType = "query", dataType = "string", value = "The test run result filter (failures)"),
            @ApiImplicitParam(name = "showStacktrace", paramType = "query", dataType = "boolean", value = "Indicates test logs visibility")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The email was sent successfully. Returns the email content", response = String.class),
            @ApiResponse(code = 404, message = "Indicates that the test run does not exist", response = ErrorResponse.class)
    })
    String sendTestRunResultsEmail(String id, EmailType email, String filter, boolean showStacktrace);

    @ApiOperation(
            value = "Sends test run results via email",
            notes = "Collects test run result data and sends a report via email",
            nickname = "sendTestRunFailureEmail",
            httpMethod = "POST",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "string", required = true, value = "The test run id"),
            @ApiImplicitParam(name = "email", paramType = "body", dataType = "EmailType", required = true, value = "The email to send the report to"),
            @ApiImplicitParam(name = "suiteOwner", paramType = "query", dataType = "boolean", value = "Indicates that the email will be sent to the suite owner"),
            @ApiImplicitParam(name = "suiteRunner", paramType = "query", dataType = "boolean", value = "Indicates that the email will be sent to a suite runner")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The email was sent successfully. Returns the email content", response = String.class),
            @ApiResponse(code = 404, message = "Indicates that the test run does not exist", response = ErrorResponse.class)
    })
    String sendTestRunFailureEmail(String id, EmailType email, boolean suiteOwner, boolean suiteRunner);

    @ApiOperation(
            value = "Builds test run results in HTML format",
            notes = "Returns built test run results in HTML format",
            nickname = "exportTestRunHTML",
            httpMethod = "GET",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "string", required = true, value = "The test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns built test run results in HTML format", response = String.class)
    })
    String exportTestRunHTML(String id);

    @ApiOperation(
            value = "Marks a test run as reviewed",
            notes = "Attaches a specified comment and marks a test run as reviewed",
            nickname = "markTestRunAsReviewed",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test run id"),
            @ApiImplicitParam(name = "comment", paramType = "body", dataType = "CommentType", required = true, value = "A test run comment")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The test run was marked as reviewed successfully"),
            @ApiResponse(code = 404, message = "Indicates that the test run does not exist", response = ErrorResponse.class)
    })
    void markTestRunAsReviewed(long id, CommentType comment);

    @ApiOperation(
            value = "Reruns a test run by its id",
            notes = "Reruns a test run by its id (all tests or failed only)",
            nickname = "rerunTestRun",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test run id"),
            @ApiImplicitParam(name = "rerunFailures", paramType = "query", dataType = "boolean", value = "Indicates that only failed tests will be rerun")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The test run was rerun successfully"),
            @ApiResponse(code = 400, message = "Indicates that test run is passed but flag is rerun failures", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that test run does not exist", response = ErrorResponse.class)
    })
    void rerunTestRun(long id, boolean rerunFailures);

    @ApiOperation(
            value = "Starts a debug job",
            notes = "Needs to debug test run remotely",
            nickname = "debugTestRun",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test cases"),
            @ApiResponse(code = 400, message = "Indicates that the test run is passed but flag is rerun failures or test automation integration does not exist ", response = ErrorResponse.class)
    })
    void debugTestRun(long id);

    @ApiOperation(
            value = "Aborts a test run job or debug process",
            nickname = "abortCIJob",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "query", dataTypeClass = Long.class, value = "The test run id"),
            @ApiImplicitParam(name = "ciRunId", paramType = "query", dataType = "string", value = "The test run ciRun id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The test run job or debug process was aborted successfully"),
            @ApiResponse(code = 400, message = "Indicates that the test automation integration does not exist", response = ErrorResponse.class)
    })
    void abortCIJob(Long id, String ciRunId);

    @ApiOperation(
            value = "Builds a test run job",
            notes = "Builds a test run job using custom job parameters",
            nickname = "buildTestRun",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test run id"),
            @ApiImplicitParam(name = "jobParameters", paramType = "body", dataType = "Map", required = true, value = "The job parameters")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The test run job was built successfully"),
            @ApiResponse(code = 400, message = "Indicates that the test automation integration does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that the test run does not exist", response = ErrorResponse.class)
    })
    void buildTestRun(long id, Map<String, String> jobParameters);

    @ApiOperation(
            value = "Retrieves job parameters from a test run job by its id",
            nickname = "getJobParameters",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test run id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test cases", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that the test automation integration does not exist", response = ErrorResponse.class),
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
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test run environments", response = List.class)
    })
    List<String> getEnvironments();

    @ApiOperation(
            value = "Retrieves all test run config platforms",
            notes = "Returns found test run config platforms",
            nickname = "getPlatforms",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test run platforms", response = List.class)
    })
    List<String> getPlatforms();

    @ApiOperation(
            value = "Retrieves all test run config browsers",
            notes = "Returns found test run config browsers",
            nickname = "getBrowsers",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test run config browsers", response = List.class)
    })
    List<String> getBrowsers();

    @ApiOperation(
            value = "Retrieves test run job console lines",
            notes = "Returns found lines",
            nickname = "getConsoleOutput",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "count", paramType = "path", dataTypeClass = Integer.class, required = true, value = "The count of test run job console lines "),
            @ApiImplicitParam(name = "fullCount", paramType = "path", dataTypeClass = Integer.class, required = true, value = "The offset where the search starts"),
            @ApiImplicitParam(name = "id", paramType = "query", dataTypeClass = Long.class, value = "The test run id"),
            @ApiImplicitParam(name = "ciRunId", paramType = "query", dataType = "string", value = "The test run ciRun id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found lines", response = Map.class),
            @ApiResponse(code = 400, message = "Indicates that the test automation integration does not exist", response = ErrorResponse.class)
    })
    Map<Integer, String> getConsoleOutput(int count, int fullCount, Long id, String ciRunId);

}
