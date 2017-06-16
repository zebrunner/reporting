package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmailType extends AbstractType
{
	private static final long serialVersionUID = 3091393414410237233L;
	private String recipients;
	private String subject;
	private String text;
	private boolean screenshotsAvailable;

	public EmailType() {
		
	}
	
	public EmailType(String recipients) {
		this.recipients = recipients;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRecipients()
	{
		return recipients;
	}

	public void setRecipients(String recipients)
	{
		this.recipients = recipients;
	}

	public boolean isScreenshotsAvailable() {
		return screenshotsAvailable;
	}

	public void setScreenshotsAvailable(boolean screenshotsAvailable) {
		this.screenshotsAvailable = screenshotsAvailable;
	}
}

