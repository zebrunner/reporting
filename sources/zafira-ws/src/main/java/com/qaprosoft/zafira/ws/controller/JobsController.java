package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.dbaccess.model.Dashboard;
import com.qaprosoft.zafira.dbaccess.model.Job;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.JobsService;
import com.qaprosoft.zafira.ws.annotations.PostResponse;
import com.qaprosoft.zafira.ws.dto.JobType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@Api(value = "jobsController", description = "Jobs operations")
@RequestMapping("jobs")
public class JobsController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private JobsService jobsService;

	@PostResponse
	@ApiOperation(value = "Create job", nickname = "createJob", code = 200, httpMethod = "POST",
			notes = "create a new Job", response = Dashboard.class, responseContainer = "Job")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JobType createJob(@RequestBody @Valid JobType job, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		return mapper.map(jobsService.createOrUpdateJob(mapper.map(job, Job.class)), JobType.class);
	}
}
