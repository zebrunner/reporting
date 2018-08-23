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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.apache.commons.collections4.CollectionUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.application.Job;
import com.qaprosoft.zafira.models.db.application.JobView;
import com.qaprosoft.zafira.models.db.application.TestRun;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.JobViewType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.JobsService;
import com.qaprosoft.zafira.services.services.application.TestRunService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Jobs API")
@CrossOrigin
@RequestMapping("api/jobs")
public class JobsAPIController
{

	@Autowired
	private Mapper mapper;

	@Autowired
	private JobsService jobsService;

	@Autowired
	private TestRunService testRunService;

	@ResponseStatusDetails
	@ApiOperation(value = "Create job", nickname = "createJob", code = 200, httpMethod = "POST", response = JobType.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JobType createJob(@RequestBody @Valid JobType job,
			@RequestHeader(value = "Project", required = false) String project) throws
			ServiceException
	{
		return mapper.map(jobsService.createOrUpdateJob(mapper.map(job, Job.class)), JobType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get all jobs", nickname = "getAllJobs", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Job> getAllJobs() throws ServiceException
	{
		return jobsService.getAllJobs();
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get latest job test runs", nickname = "getLatestJobTestRuns", code = 200, httpMethod = "POST", response = Map.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "views/{id}/tests/runs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<Long, TestRun> getLatestJobTestRuns(@QueryParam("env") String env,
			@RequestBody @Valid List<JobViewType> jobViews) throws ServiceException
	{
		List<Long> jobIds = new ArrayList<>();
		for (JobViewType jobView : jobViews)
		{
			jobIds.add(jobView.getJob().getId());
		}
		return testRunService.getLatestJobTestRuns(env, jobIds);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create job view", nickname = "createJobViews", code = 200, httpMethod = "POST", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "views", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({ "ROLE_ADMIN" })
	public @ResponseBody List<JobViewType> createJobViews(@RequestBody @Valid List<JobViewType> jobViews)
			throws ServiceException
	{
		for (JobViewType jobView : jobViews)
		{
			jobView = mapper.map(jobsService.createJobView(mapper.map(jobView, JobView.class)), JobViewType.class);
		}
		return jobViews;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update job view", nickname = "updateJobViews", code = 200, httpMethod = "PUT", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "views/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({ "ROLE_ADMIN" })
	public @ResponseBody List<JobViewType> updateJobViews(@RequestBody @Valid List<JobViewType> jobViews,
			@PathVariable(value = "id") long viewId, @QueryParam("env") String env) throws ServiceException
	{
		if (!CollectionUtils.isEmpty(jobViews))
		{
			jobsService.deleteJobViews(viewId, env);
			for (JobViewType jobView : jobViews)
			{
				jobView = mapper.map(jobsService.createJobView(mapper.map(jobView, JobView.class)), JobViewType.class);
			}
		}
		return jobViews;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get job views", nickname = "getJobViews", code = 200, httpMethod = "GET", response = Map.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "views/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, List<JobViewType>> getJobViews(@PathVariable(value = "id") long id)
			throws ServiceException
	{
		Map<String, List<JobViewType>> jobViews = new LinkedHashMap<>();
		for (JobView jobView : jobsService.getJobViewsByViewId(id))
		{
			if (!jobViews.containsKey(jobView.getEnv()))
			{
				jobViews.put(jobView.getEnv(), new ArrayList<>());
			}
			jobViews.get(jobView.getEnv()).add(mapper.map(jobView, JobViewType.class));
		}
		return jobViews;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete job views", nickname = "deleteJobViews", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "views/{id}", method = RequestMethod.DELETE)
	public void deleteJobViews(@PathVariable(value = "id") long viewId, @QueryParam("env") String env)
			throws ServiceException
	{
		jobsService.deleteJobViews(viewId, env);
	}
}
