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
package com.qaprosoft.zafira.services.services.application.emails;

import java.util.List;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.db.Monitor;

public class MonitorEmailMessageNotification implements IEmailMessage
{

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
	public EmailType getType() {
		return EmailType.MONITOR;
	}

	@Override
	public List<Attachment> getAttachments()
	{
		return null;
	}
}
