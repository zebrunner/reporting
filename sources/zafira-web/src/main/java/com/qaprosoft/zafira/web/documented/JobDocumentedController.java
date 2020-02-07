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
            value = "Creates or updates a job",
            notes = "Creates a job if it does not exist. Otherwise, updates it",
            nickname = "createJob",
            httpMethod = "POST",
            response = JobDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "jobDTO", paramType = "body", dataType = "JobDTO", required = true, value = "The job to create or update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created or updated job", response = JobDTO.class)
    })
    JobDTO createJob(JobDTO jobDTO);

    @ApiOperation(
            value = "Creates a job using Jenkins job URL",
            notes = "Returns the created job",
            nickname = "createJobByUrl",
            httpMethod = "POST",
            response = JobDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "jobUrl", paramType = "body", dataType = "JobUrlType", required = true, value = "The job URL to create a new job")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created job", response = JobDTO.class)
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
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found jobs", response = List.class)
    })
    List<Job> getAllJobs();

    @ApiOperation(
            value = "Retrieves the latest job test runs by the environment and job ids grouped by job ids",
            notes = "Returns the latest job test runs for the last 2 weeks",
            nickname = "getLatestJobTestRuns",
            httpMethod = "POST",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "env", paramType = "query", dataType = "string", required = true, value = "The test runs environment"),
            @ApiImplicitParam(name = "jobViews", paramType = "body", dataType = "List", required = true, value = "Job views")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the latest job test runs", response = Map.class)
    })
    Map<Long, TestRun> getLatestJobTestRuns(String env, List<JobViewDTO> jobViews);

    @ApiOperation(
            value = "Creates a batch of job views",
            notes = "Returns created job views",
            nickname = "createJobViews",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "jobViewDTOs", paramType = "body", dataType = "List", required = true, value = "Job views to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created job views", response = List.class)
    })
    List<JobViewDTO> createJobViews(List<JobViewDTO> jobViewDTOs);

    @ApiOperation(
            value = "Updates a batch of job views",
            notes = "Deletes job views by the view id and environment, and creates new job views",
            nickname = "updateJobViews",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "jobViewDTOs", paramType = "body", dataType = "List", required = true, value = "The job view to update"),
            @ApiImplicitParam(name = "viewId", paramType = "path", dataType = "number", required = true, value = "The id of the job view to delete"),
            @ApiImplicitParam(name = "env", paramType = "query", dataType = "string", required = true, value = "The environment of job views to delete")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created job views", response = List.class)
    })
    List<JobViewDTO> updateJobViews(List<JobViewDTO> jobViewDTOs, long viewId, String env);

    @ApiOperation(
            value = "Retrieves job views by the view id",
            notes = "Returns found job views grouped by environment",
            nickname = "getJobViews",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "The view id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found job views grouped by environment", response = Map.class)
    })
    Map<String, List<JobViewDTO>> getJobViews(long id);

    @ApiOperation(
            value = "Deletes job views",
            notes = "Deletes job views by the view id and environment",
            nickname = "deleteJobViews",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "viewId", paramType = "path", dataType = "number", required = true, value = "The view id"),
            @ApiImplicitParam(name = "env", paramType = "query", dataType = "string", required = true, value = "The environment")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Job views were deleted successfully")
    })
    void deleteJobViews(long viewId, String env);

}
