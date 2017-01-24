package com.qaprosoft.zafira.services.services.emails;

import java.util.List;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.db.Device;

public class GridStatusEmail implements IEmailMessage
{
	private static final String SUBJECT = "Grid Health Status";
	private static final String TEMPLATE = "grid_status.ftl";
	
	private List<Device> devices;
	
	public GridStatusEmail(List<Device> devices)
	{
		this.devices = devices;
	}

	public List<Device> getDevices()
	{
		return devices;
	}

	public void setDevices(List<Device> devices)
	{
		this.devices = devices;
	}

	@Override
	public String getSubject()
	{
		return SUBJECT;
	}

	@Override
	public String getTemplate()
	{
		return TEMPLATE;
	}

	@Override
	public List<Attachment> getAttachments() 
	{
		return null;
	}

	@Override
	public String getText() {
		return null;
	}
}
