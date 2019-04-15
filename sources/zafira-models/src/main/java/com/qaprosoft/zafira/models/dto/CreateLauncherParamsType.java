package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateLauncherParamsType implements Serializable
{
	private static final long serialVersionUID = -5754742673797369219L;

	@NotNull(message = "{error.repo.required}")
	private String repo;

	@NotEmpty(message = "{error.job.url.required}")
	private String jobUrl;

	private Map <String, String> jobParameters;

	public String getRepo()
	{
		return repo;
	}

	public void setRepo(String repo)
	{
		this.repo = repo;
	}

	public String getJobUrl()
	{
		return jobUrl;
	}

	public void setJobUrl(String jobUrl)
	{
		this.jobUrl = jobUrl;
	}

	public Map<String, String> getJobParameters()
	{
		return jobParameters;
	}

	public void setJobParameters(Map<String, String> jobParameters)
	{
		this.jobParameters = jobParameters;
	}
}
