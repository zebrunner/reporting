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
package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.AbstractEntity;
import com.qaprosoft.zafira.models.db.Monitor;
import org.quartz.CronExpression;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
public class MonitorType extends AbstractEntity
{
	private static final long serialVersionUID = -4720099488195144150L;
	
	@NotNull(message = "Name required")
	private String name;
	@NotNull(message = "URL required")
	private String url;
	@NotNull(message = "HTTP method required")
	private Monitor.HttpMethod httpMethod;
	private String requestBody;
	@NotNull(message = "Cron expression required")
	private String cronExpression;
	@NotNull(message = "Type required")
	private Monitor.Type type;
	@NotNull(message = "Checkbox should be enabled or disabled")
	private boolean notificationsEnabled;
	@NotNull(message = "Checkbox should be enabled or disabled")
	private boolean monitorEnabled;
	private String recipients;
	@NotNull(message = "Expected code required")
	private int expectedCode;
	private boolean success;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Monitor.HttpMethod getHttpMethod()
	{
		return httpMethod;
	}

	public void setHttpMethod(Monitor.HttpMethod httpMethod)
	{
		this.httpMethod = httpMethod;
	}

	public String getRequestBody()
	{
		return requestBody;
	}

	public void setRequestBody(String requestBody)
	{
		this.requestBody = requestBody;
	}

	public String getCronExpression()
	{
		return cronExpression;
	}

	public void setCronExpression(String cronExpression)
	{
		this.cronExpression = cronExpression;
	}

	public boolean isNotificationsEnabled()
	{
		return notificationsEnabled;
	}

	public void setNotificationsEnabled(boolean notificationsEnabled)
	{
		this.notificationsEnabled = notificationsEnabled;
	}

	public boolean isMonitorEnabled()
	{
		return monitorEnabled;
	}

	public void setMonitorEnabled(boolean monitorEnabled)
	{
		this.monitorEnabled = monitorEnabled;
	}

	public String getRecipients()
	{
		return recipients;
	}

	public void setRecipients(String recipients)
	{
		this.recipients = recipients;
	}

	public int getExpectedCode()
	{
		return expectedCode;
	}

	public void setExpectedCode(int expectedCode)
	{
		this.expectedCode = expectedCode;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public Monitor.Type getType()
	{
		return type;
	}

	public void setType(Monitor.Type type)
	{
		this.type = type;
	}

	@AssertTrue(message = "Cron expression is invalid")
	public boolean isCronExpressionValid()
	{
		return CronExpression.isValidExpression(this.cronExpression);
	}
}
