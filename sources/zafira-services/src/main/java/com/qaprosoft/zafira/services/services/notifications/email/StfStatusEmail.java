package com.qaprosoft.zafira.services.services.notifications.email;

public class StfStatusEmail implements IEmailMessage
{
	private static final String SUBJECT = "STF Status";
	private static final String TEMPLATE = "stf_status.ftl";
	

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
}
