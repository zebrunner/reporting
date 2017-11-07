package com.qaprosoft.zafira.services.services.emails;

import java.util.List;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.db.Monitor;

public class MonitorEmailMessageNotification implements IEmailMessage
{

	private static final String TEMPLATE = "monitor_status.ftl";

	private Monitor monitor;
	private Integer actualStatus;
	private String text;
	private String subject;

	public MonitorEmailMessageNotification(String subject, String text, Monitor monitor, Integer actualStatus)
	{
		this.subject = subject;
		this.text = text;
		this.monitor = monitor;
		this.actualStatus = actualStatus;
	}

	public Monitor getMonitor()
	{
		return monitor;
	}

	public void setMonitor(Monitor monitor)
	{
		this.monitor = monitor;
	}

	public Integer getActualStatus()
	{
		return actualStatus;
	}

	public void setActualStatus(Integer actualStatus)
	{
		this.actualStatus = actualStatus;
	}


	@Override
	public String getSubject()
	{
		return subject;
	}

	@Override
	public String getText()
	{
		return text;
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
}
