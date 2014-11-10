package com.qaprosoft.zafira.ws.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.dbaccess.model.Job;

@JsonInclude(Include.NON_NULL)
public class JobType extends Job
{
	private static final long serialVersionUID = -4887504085866015187L;
	
	@NotNull
	public void setJobURL(String jobURL)
	{
		super.setJobURL(jobURL);
	}
}
