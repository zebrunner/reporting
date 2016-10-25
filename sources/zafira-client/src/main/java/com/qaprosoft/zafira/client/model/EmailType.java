package com.qaprosoft.zafira.client.model;

public class EmailType extends AbstractType {
	
	private String recipients;
	
	public EmailType(String recipients) {
		this.recipients = recipients;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
}
