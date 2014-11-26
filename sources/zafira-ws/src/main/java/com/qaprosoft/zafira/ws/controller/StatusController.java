package com.qaprosoft.zafira.ws.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.ws.dto.JobType;

@Controller
@RequestMapping("status")
public class StatusController extends AbstractController
{
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET)
	public void getStatus(@RequestBody @Valid JobType job) throws ServiceException
	{
	}
}
