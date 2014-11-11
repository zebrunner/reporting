package com.qaprosoft.zafira.ws.controller;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.dbaccess.model.Job;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.JobsService;
import com.qaprosoft.zafira.ws.dto.JobType;

@Controller
@RequestMapping("jobs")
public class JobsController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private JobsService jobsService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JobType createJob(@RequestBody @Valid JobType job) throws ServiceException
	{
		return mapper.map(jobsService.initializeJob(mapper.map(job, Job.class)), JobType.class);
	}
}
