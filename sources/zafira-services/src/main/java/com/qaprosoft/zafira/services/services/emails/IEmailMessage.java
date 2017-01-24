package com.qaprosoft.zafira.services.services.emails;

import java.util.List;

import com.qaprosoft.zafira.models.db.Attachment;

public interface IEmailMessage
{
	String getSubject();
	String getText();
	String getTemplate();
	List<Attachment> getAttachments();
}
