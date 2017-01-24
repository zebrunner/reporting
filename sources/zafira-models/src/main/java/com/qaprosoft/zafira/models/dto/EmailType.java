package com.qaprosoft.zafira.models.dto;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmailType extends AbstractType
{
	@NotEmpty
	private String recipients;
	private String subject;
	private String text;
	
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
}
