package com.qaprosoft.zafira.models.db.application;

import com.qaprosoft.zafira.models.db.AbstractEntity;

public class MonitorStatus extends AbstractEntity
{
	private static final long serialVersionUID = 496067410211084296L;

	private Boolean success;

	public MonitorStatus()
	{
	}

	public MonitorStatus(boolean success)
	{
		this.success = success;
	}

	public Boolean getSuccess()
	{
		return success;
	}

	public void setSuccess(Boolean success)
	{
		this.success = success;
	}
}
