package com.qaprosoft.zafira.models.dto.monitor;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorCheckType implements Serializable
{
	private static final long serialVersionUID = 3232356935782375373L;

	private Integer actualCode;
	private boolean success;

	public MonitorCheckType(Integer actualCode, boolean success)
	{
		this.actualCode = actualCode;
		this.success = success;
	}

	public Integer getActualCode()
	{
		return actualCode;
	}

	public boolean isSuccess()
	{
		return success;
	}
}
