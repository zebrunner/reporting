package com.qaprosoft.zafira.models.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.AbstractEntity;
import com.qaprosoft.zafira.models.db.Job;

@JsonInclude(Include.NON_NULL)
public class JobViewType extends AbstractEntity
{
	private static final long serialVersionUID = -3868077369004418496L;

	@NotNull
	private Job job;
	@NotNull
	private Long viewId;
	@NotNull
	private String env;
	@NotNull
	private Integer position;
	@NotNull
	private Integer size;

	public Job getJob()
	{
		return job;
	}

	public void setJob(Job job)
	{
		this.job = job;
	}

	public Long getViewId()
	{
		return viewId;
	}

	public void setViewId(Long viewId)
	{
		this.viewId = viewId;
	}

	public String getEnv()
	{
		return env;
	}

	public void setEnv(String env)
	{
		this.env = env;
	}

	public Integer getPosition()
	{
		return position;
	}

	public void setPosition(Integer position)
	{
		this.position = position;
	}

	public Integer getSize()
	{
		return size;
	}

	public void setSize(Integer size)
	{
		this.size = size;
	}
}