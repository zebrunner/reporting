package com.qaprosoft.zafira.ws.controller;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.services.exceptions.InvalidTestRunException;
import com.qaprosoft.zafira.services.exceptions.JobNotFoundException;
import com.qaprosoft.zafira.ws.dto.errors.Error;
import com.qaprosoft.zafira.ws.dto.errors.ErrorCode;
import com.qaprosoft.zafira.ws.dto.errors.ErrorResponse;

public abstract class AbstractController
{
	@Resource(name = "messageSource")
	protected MessageSource messageSource;
	
	@ExceptionHandler(JobNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponse handleJobNotFoundException(JobNotFoundException e)
	{
		ErrorResponse result = new ErrorResponse();
		result.setError(new Error(ErrorCode.JOB_NOT_FOUND));
		return result;
	}
	
	@ExceptionHandler(InvalidTestRunException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponse handleInvalidTestRunException(InvalidTestRunException e)
	{
		ErrorResponse result = new ErrorResponse();
		result.setError(new Error(ErrorCode.INVALID_TEST_RUN));
		return result;
	}
}
