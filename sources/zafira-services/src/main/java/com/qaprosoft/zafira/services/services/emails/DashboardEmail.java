package com.qaprosoft.zafira.services.services.emails;

import java.io.File;

public class DashboardEmail implements IEmailMessage
{
	private static final String TEMPLATE = "dashboard.ftl";
	
	private String subject;
	private String text;
	private File attachment;
	
	public DashboardEmail(String subject, String text, File attachment)
	{
		this.subject = subject;
		this.text = text;
		this.attachment = attachment;
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
	public File getAttachment() 
	{
		return attachment;
	}

	@Override
	public String getText() 
	{
		return text;
	}
}
