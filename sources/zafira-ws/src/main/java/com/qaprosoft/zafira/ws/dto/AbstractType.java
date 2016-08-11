package com.qaprosoft.zafira.ws.dto;

import org.apache.commons.lang3.StringUtils;

public class AbstractType
{
	private long id;
	private String project;
	
	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getProject()
	{
		return project;
	}

	public void setProject(String project)
	{
		if(!StringUtils.isEmpty(project))
		{
			this.project = project;
		}
	}
}
