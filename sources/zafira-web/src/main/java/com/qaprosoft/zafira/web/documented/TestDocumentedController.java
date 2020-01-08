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
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestSearchCriteria;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.dto.IssueDTO;
import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@Api("Tests API")
public interface TestDocumentedController {

    @ApiOperation(
            value = "Starts test",
            notes = "Creates new test or updates old test if there is any rerun logic",
            nickname = "startTest",
            httpMethod = "POST",
            response = TestType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "t", paramType = "body", dataType = "TestType", required = true, value = "Test to create or update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created or updated test", response = TestType.class),
            @ApiResponse(code = 404, message = "Indicates that test run for test does not exist", response = ErrorResponse.class)
    })
    TestType startTest(TestType t);

    @ApiOperation(
            value = "Finishes test",
            notes = "Update test according test result like status, error message, work items etc.",
            nickname = "finishTest",
            httpMethod = "POST",
            response = TestType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test id to finish"),
            @ApiImplicitParam(name = "t", paramType = "body", dataType = "TestType", required = true, value = "Test with result data to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated test", response = TestType.class),
            @ApiResponse(code = 404, message = "Indicates that test by id does not exist", response = ErrorResponse.class)
    })
    TestType finishTest(long id, TestType t);

    @ApiOperation(
            value = "Updates test status",
            notes = "Returns updated test",
            nickname = "updateTest",
            httpMethod = "PUT",
            response = Test.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "test", paramType = "body", dataType = "Test", required = true, value = "Test with status to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated test", response = Test.class),
            @ApiResponse(code = 404, message = "Indicates that test does not exist", response = ErrorResponse.class)
    })
    Test updateTest(Test test);

    @ApiOperation(
            value = "Creates test work items",
            notes = "Returns test with with created work items inside",
            nickname = "createTestWorkItems",
            httpMethod = "POST",
            response = TestType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test id"),
            @ApiImplicitParam(name = "workItems", paramType = "body", dataType = "array", required = true, value = "Work items to attache")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns test with with created work items inside", response = TestType.class),
            @ApiResponse(code = 404, message = "Indicates that test does not exist", response = ErrorResponse.class)
    })
    TestType createTestWorkItems(long id, List<String> workItems);

    @ApiOperation(
            value = "Deletes test by id",
            nickname = "deleteTest",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Test was deleted successfully")
    })
    void deleteTest(long id);

    @ApiOperation(
            value = "Retrieve tests by search criteria",
            notes = "Returns found tests",
            nickname = "searchTests",
            httpMethod = "POST",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "sc", paramType = "body", dataType = "TestSearchCriteria", required = true, value = "Test search criteria")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found tests", response = SearchResult.class)
    })
    SearchResult<Test> searchTests(TestSearchCriteria sc);

    @ApiOperation(
            value = "Retrieves test work items",
            notes = "Retrieves test work items by test id and work item type",
            nickname = "getTestCaseWorkItemsByType",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test id"),
            @ApiImplicitParam(name = "type", paramType = "path", dataType = "string", required = true, value = "Work item type")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found work items", response = List.class)
    })
    List<WorkItem> getTestCaseWorkItemsByType(long id, WorkItem.Type type);

    @ApiOperation(
            value = "Links work item to test",
            notes = "Creates new work item or attache old work item to test",
            nickname = "linkWorkItem",
            httpMethod = "POST",
            response = WorkItem.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test id"),
            @ApiImplicitParam(name = "workItem", paramType = "body", dataType = "WorkItem", required = true, value = "Work item to attache")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns registered test run", response = WorkItem.class),
            @ApiResponse(code = 400, message = "Indicates that test status is not failed or skipped and work item type is BUG", response = ErrorResponse.class)
    })
    WorkItem linkWorkItem(long id, WorkItem workItem);

    @ApiOperation(
            value = "Updates test work item",
            notes = "Updates error message hashcode of existing work item",
            nickname = "updateTestKnownIssue",
            httpMethod = "PUT",
            response = WorkItem.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test id"),
            @ApiImplicitParam(name = "workItem", paramType = "body", dataType = "WorkItem", required = true, value = "Work item to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated test work item", response = WorkItem.class)
    })
    WorkItem updateTestKnownIssue(long id, WorkItem workItem);

    @ApiOperation(
            value = "Deletes test work item",
            notes = "Unlinks test work item from tests and deletes it",
            nickname = "deleteTestWorkItem",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "workItemId", paramType = "path", dataType = "number", required = true, value = "Work item id"),
            @ApiImplicitParam(name = "testId", paramType = "path", dataType = "number", required = true, value = "Test id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Test work item was deleted successfully")
    })
    void deleteTestWorkItem(long workItemId, long testId);

    @ApiOperation(
            value = "Finds jira issue by id",
            notes = "Returns found issue",
            nickname = "getJiraIssue",
            httpMethod = "GET",
            response = IssueDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "issue", paramType = "path", dataType = "string", required = true, value = "Jira issue number")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found issue", response = IssueDTO.class),
            @ApiResponse(code = 400, message = "Indicates that Jira integration does not exist", response = ErrorResponse.class)
    })
    IssueDTO getJiraIssue(String issue);

    @ApiOperation(
            value = "Checks that Jira integration exists and connected",
            notes = "Returns connection status",
            nickname = "getConnectionToJira",
            httpMethod = "GET",
            response = Boolean.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns connection status", response = Boolean.class)
    })
    boolean getConnectionToJira();

    @ApiOperation(
            value = "Creates test artifact and attaches it to test",
            nickname = "addTestArtifact",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Test id"),
            @ApiImplicitParam(name = "artifact", paramType = "body", dataType = "TestArtifactType", required = true, value = "Test artifact to create and attache")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Test artifact was created and attached to test successfully")
    })
    void addTestArtifact(long id, TestArtifactType artifact);

}
