package com.qaprosoft.zafira.services.services.emails;

import java.util.List;

import com.qaprosoft.zafira.models.db.Attachment;

public class DashboardEmail implements IEmailMessage
{
	private static final String TEMPLATE = "dashboard.ftl";
	
	private String subject;
	private String text;
	private List<Attachment> attachments;
	
	public DashboardEmail(String subject, String text, List<Attachment> attachments)
	{
		this.subject = subject;
		this.text = text;
		this.attachments = attachments;
	}

	@Override
	public String getSubject()
	{
		return subject;
	}

	@Override
	public String getTemplate()
	{
		return TEMPLATE;
	}

	@Override
	public List<Attachment> getAttachments() 
	{
		return attachments;
	}

	@Override
	public String getText() 
	{
		return text;
	}
}
