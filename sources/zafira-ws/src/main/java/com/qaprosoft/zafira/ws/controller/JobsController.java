package com.qaprosoft.zafira.ws.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.dbaccess.model.Job;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.JobsService;
import com.qaprosoft.zafira.ws.dto.JobType;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

@Controller
@Api(value = "Jobs operations")
@RequestMapping("jobs")
public class JobsController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private JobsService jobsService;

	@ResponseStatusDetails
	@ApiOperation(value = "Create job", nickname = "createJob", code = 200, httpMethod = "POST",
			notes = "Creates a new job or updates existing one.", response = JobType.class, responseContainer = "JobType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JobType createJob(@RequestBody @Valid JobType job, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		return mapper.map(jobsService.createOrUpdateJob(mapper.map(job, Job.class)), JobType.class);
	}
}
