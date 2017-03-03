package com.qaprosoft.zafira.models.db;

public class JobView extends AbstractEntity
{
	private static final long serialVersionUID = -3868077369004418496L;

	private Long viewId;
	private Job job = new Job();
	private String env;
	private Integer position;
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