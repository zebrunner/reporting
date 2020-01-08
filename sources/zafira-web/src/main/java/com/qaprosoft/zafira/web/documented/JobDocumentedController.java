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

import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.JobDTO;
import com.qaprosoft.zafira.models.dto.JobUrlType;
import com.qaprosoft.zafira.models.dto.JobViewDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Map;

@Api("Jobs API")
public interface JobDocumentedController {

    @ApiOperation(
            value = "Creates or updates job",
            notes = "Creates job if it is not exist or updates it in other case",
            nickname = "createJob",
            httpMethod = "POST",
            response = JobDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "jobDTO", paramType = "body", dataType = "JobDTO", required = true, value = "Job to create or update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created or updated job", response = JobDTO.class)
    })
    JobDTO createJob(JobDTO jobDTO);

    @ApiOperation(
            value = "Creates job using Jenkins job url",
            notes = "Returns created job",
            nickname = "createJobByUrl",
            httpMethod = "POST",
            response = JobDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "jobUrl", paramType = "body", dataType = "JobUrlType", required = true, value = "Job url to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created job", response = JobDTO.class)
    })
    JobDTO createJobByUrl(JobUrlType jobUrl);

    @ApiOperation(
            value = "Retrieves all jobs",
            notes = "Returns found jobs",
            nickname = "getAllJobs",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found jobs", response = List.class)
    })
    List<Job> getAllJobs();

    @ApiOperation(
            value = "Retrieves latest job test runs by environment and job ids",
            notes = "Returns found latest job test runs for last 2 weeks",
            nickname = "getLatestJobTestRuns",
            httpMethod = "POST",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "env", paramType = "query", dataType = "string", required = true, value = "Test runs environment"),
            @ApiImplicitParam(name = "jobViews", paramType = "body", dataType = "List", required = true, value = "Job views with job ids inside")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found latest job test run", response = Map.class)
    })
    Map<Long, TestRun> getLatestJobTestRuns(String env, List<JobViewDTO> jobViews);

    @ApiOperation(
            value = "Batch creates job views",
            notes = "Returns created job views",
            nickname = "createJobViews",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "jobViewDTOs", paramType = "body", dataType = "List", required = true, value = "Job views to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created job views", response = List.class)
    })
    List<JobViewDTO> createJobViews(List<JobViewDTO> jobViewDTOs);

    @ApiOperation(
            value = "Batch updates job views",
            notes = "Deletes job views by view id and env and creates new job views",
            nickname = "updateJobViews",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "jobViewDTOs", paramType = "body", dataType = "List", required = true, value = "List to invite"),
            @ApiImplicitParam(name = "viewId", paramType = "path", dataType = "number", required = true, value = "Job view id to delete"),
            @ApiImplicitParam(name = "env", paramType = "query", dataType = "string", required = true, value = "Job view environment to delete")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created job views", response = List.class)
    })
    List<JobViewDTO> updateJobViews(List<JobViewDTO> jobViewDTOs, long viewId, String env);

    @ApiOperation(
            value = "Retrieves job views by view id",
            notes = "Returns found job views grouped by environment",
            nickname = "getJobViews",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "View id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found job views grouped by environment", response = Map.class)
    })
    Map<String, List<JobViewDTO>> getJobViews(long id);

    @ApiOperation(
            value = "Deletes job views",
            notes = "Deletes job views by view and environment",
            nickname = "deleteJobViews",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "viewId", paramType = "path", dataType = "number", required = true, value = "View id"),
            @ApiImplicitParam(name = "env", paramType = "query", dataType = "string", required = true, value = "Environment")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Job views was deleted successfully")
    })
    void deleteJobViews(long viewId, String env);

}
