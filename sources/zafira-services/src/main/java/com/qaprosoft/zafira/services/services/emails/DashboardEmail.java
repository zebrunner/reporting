/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
