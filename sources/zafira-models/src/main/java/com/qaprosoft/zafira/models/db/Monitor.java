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
package com.qaprosoft.zafira.models.db;

import com.qaprosoft.zafira.models.db.AbstractEntity;

public class Monitor extends AbstractEntity
{
	private static final long serialVersionUID = -1016459307109758493L;

	public enum HttpMethod
	{
		GET, POST, PUT, DELETE
	}

	public enum Type
	{
		HTTP, PING
	}

	private String name;
	private String url;
	private HttpMethod httpMethod;
	private String requestBody;
	private String environment;
	private String comment;
	private String tag;
	private String cronExpression;
	private boolean notificationsEnabled;
	private boolean monitorEnabled;
	private String recipients;
	private Type type;
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

	public HttpMethod getHttpMethod()
	{
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod)
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

	public String getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
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

	public int getExpectedCode()
	{
		return expectedCode;
	}

	public void setExpectedCode(int expectedCode)
	{
		this.expectedCode = expectedCode;
	}

	public String getRecipients()
	{
		return recipients;
	}

	public void setRecipients(String recipients)
	{
		this.recipients = recipients;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	@Override
	public String toString()
	{
		return "Monitor{" +
				"name='" + name + '\'' +
				", url='" + url + '\'' +
				", httpMethod=" + httpMethod +
				", requestBody='" + requestBody + '\'' +
				", cronExpression='" + cronExpression + '\'' +
				", active=" + notificationsEnabled +
				", emails='" + recipients + '\'' +
				", type=" + type +
				", expectedCode=" + expectedCode +
				'}';
	}
}


