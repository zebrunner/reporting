package com.qaprosoft.zafira.models.dto;

import java.io.Serializable;

public class AbstractType implements Serializable
{
	private static final long serialVersionUID = -1915862891525919654L;

	private long id;
	
	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}
}
