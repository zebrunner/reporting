package com.qaprosoft.zafira.services.services.emails;

import java.io.File;

public interface IEmailMessage
{
	String getSubject();
	String getText();
	String getTemplate();
	File getAttachment();
}
