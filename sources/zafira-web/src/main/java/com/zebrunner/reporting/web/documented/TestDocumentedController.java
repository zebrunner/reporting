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

import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestSearchCriteria;
import com.zebrunner.reporting.domain.db.Test;
import com.zebrunner.reporting.domain.db.WorkItem;
import com.zebrunner.reporting.domain.dto.IssueDTO;
import com.zebrunner.reporting.domain.dto.TestArtifactType;
import com.zebrunner.reporting.domain.dto.TestType;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Api("Tests API")
public interface TestDocumentedController {

    @ApiOperation(
            value = "Starts a test",
            notes = "Creates a new test or updates an old test if there is any rerun logic",
            nickname = "startTest",
            httpMethod = "POST",
            response = TestType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "t", paramType = "body", dataType = "TestType", required = true, value = "The test to create or update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created or updated test", response = TestType.class),
            @ApiResponse(code = 404, message = "Indicates that a test run for the test does not exist", response = ErrorResponse.class)
    })
    TestType startTest(TestType t);

    @ApiOperation(
            value = "Finishes a test",
            notes = "Updates a test according to test results, such as status, error message, work items, etc.",
            nickname = "finishTest",
            httpMethod = "POST",
            response = TestType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The id of the test to finish"),
            @ApiImplicitParam(name = "t", paramType = "body", dataType = "TestType", required = true, value = "The test with result data to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated test", response = TestType.class),
            @ApiResponse(code = 404, message = "Indicates that test with the specified id does not exist", response = ErrorResponse.class)
    })
    TestType finishTest(long id, TestType t);

    @ApiOperation(
            value = "Updates a test status",
            notes = "Returns the updated test",
            nickname = "updateTest",
            httpMethod = "PUT",
            response = Test.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "test", paramType = "body", dataType = "Test", required = true, value = "The test with a status to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated test", response = Test.class),
            @ApiResponse(code = 404, message = "Indicates that the test does not exist", response = ErrorResponse.class)
    })
    Test updateTest(Test test);

    @ApiOperation(
            value = "Creates test work items",
            notes = "Returns a test with created work items",
            nickname = "createTestWorkItems",
            httpMethod = "POST",
            response = TestType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test id"),
            @ApiImplicitParam(name = "workItems", paramType = "body", dataType = "array", required = true, value = "The work items to attach")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns a test with created work items", response = TestType.class),
            @ApiResponse(code = 404, message = "Indicates that the test does not exist", response = ErrorResponse.class)
    })
    TestType createTestWorkItems(long id, List<String> workItems);

    @ApiOperation(
            value = "Deletes a test by its id",
            nickname = "deleteTest",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The test was deleted successfully")
    })
    ResponseEntity<Void> deleteTest(long id);

    @ApiOperation(
            value = "Retrieves tests by search criteria",
            notes = "Criteria for test search",
            nickname = "searchTests",
            httpMethod = "POST",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "sc", paramType = "body", dataType = "TestSearchCriteria", required = true, value = "Test search criteria")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Criteria for test search", response = SearchResult.class)
    })
    SearchResult<Test> searchTests(TestSearchCriteria sc);

    @ApiOperation(
            value = "Retrieves test work items",
            notes = "Retrieves test work items by the test id and work item type",
            nickname = "getTestCaseWorkItemsByType",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test id"),
            @ApiImplicitParam(name = "type", paramType = "path", dataType = "string", required = true, value = "The work item type")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found work items", response = List.class)
    })
    List<WorkItem> getTestCaseWorkItemsByType(long id, WorkItem.Type type);

    @ApiOperation(
            value = "Links a work item to a test",
            notes = "Creates a new work item or attaches an old work item to a test",
            nickname = "linkWorkItem",
            httpMethod = "POST",
            response = WorkItem.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test id"),
            @ApiImplicitParam(name = "workItem", paramType = "body", dataType = "WorkItem", required = true, value = "The work item to attach")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the registered test run", response = WorkItem.class),
            @ApiResponse(code = 400, message = "Indicates that the test status is not failed or skipped, and the work item type is a BUG", response = ErrorResponse.class)
    })
    WorkItem linkWorkItem(long id, WorkItem workItem);

    @ApiOperation(
            value = "Updates a test work item",
            notes = "Updates an error message hashcode of an existing work item",
            nickname = "updateTestKnownIssue",
            httpMethod = "PUT",
            response = WorkItem.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test id"),
            @ApiImplicitParam(name = "workItem", paramType = "body", dataType = "WorkItem", required = true, value = "The work item to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated test work item", response = WorkItem.class)
    })
    WorkItem updateTestKnownIssue(long id, WorkItem workItem);

    @ApiOperation(
            value = "Deletes a test work item",
            notes = "Unlinks a test work item from tests and deletes it",
            nickname = "deleteTestWorkItem",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "workItemId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The work item id"),
            @ApiImplicitParam(name = "testId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The test work item was deleted successfully")
    })
    void deleteTestWorkItem(long workItemId, long testId);

    @ApiOperation(
            value = "Finds a Jira issue by its id",
            notes = "Returns the found issue",
            nickname = "getJiraIssue",
            httpMethod = "GET",
            response = IssueDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "issue", paramType = "path", dataType = "string", required = true, value = "The Jira issue number")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found issue", response = IssueDTO.class),
            @ApiResponse(code = 400, message = "Indicates that Jira integration does not exist", response = ErrorResponse.class)
    })
    IssueDTO getJiraIssue(String issue);

    @ApiOperation(
            value = "Checks whether Jira integration exists and is connected",
            notes = "Returns the connection status",
            nickname = "getConnectionToJira",
            httpMethod = "GET",
            response = Boolean.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the connection status", response = Boolean.class)
    })
    boolean getConnectionToJira();

    @ApiOperation(
            value = "Creates a test artifact and attaches it to a test",
            nickname = "addTestArtifact",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test id"),
            @ApiImplicitParam(name = "artifact", paramType = "body", dataType = "TestArtifactType", required = true, value = "The test artifact to create and attach")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The test artifact was created and attached to the test successfully")
    })
    void addTestArtifact(long id, TestArtifactType artifact);

}
