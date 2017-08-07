package com.qaprosoft.zafira.models.db;


public class Device extends AbstractEntity
{
	private static final long serialVersionUID = -6702376922547540366L;

	private String model;
	private String serial;
	private boolean enabled;
	private boolean lastStatus;
	private boolean statusChanged;
	private int disconnects;

	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public String getSerial()
	{
		return serial;
	}

	public void setSerial(String serial)
	{
		this.serial = serial;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public boolean isLastStatus()
	{
		return lastStatus;
	}

	public void setLastStatus(boolean lastStatus)
	{
		this.lastStatus = lastStatus;
	}

	public int getDisconnects()
	{
		return disconnects;
	}

	public void setDisconnects(int disconnects)
	{
		this.disconnects = disconnects;
	}

	public boolean isStatusChanged()
	{
		return statusChanged;
	}

	public void setStatusChanged(boolean statusChanged)
	{
		this.statusChanged = statusChanged;
	}
}
