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
	private boolean notificationEnabled;
	@NotNull(message = "Checkbox should be enabled or disabled")
	private boolean running;
	private String recipients;
	@NotNull(message = "Expected code required")
	private int expectedCode;
	private int lastCode;
	private boolean lastRunPassed;

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

	public boolean isNotificationEnabled()
	{
		return notificationEnabled;
	}

	public void setNotificationEnabled(boolean notificationEnabled)
	{
		this.notificationEnabled = notificationEnabled;
	}

	public boolean isRunning()
	{
		return running;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
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

	public int getLastCode()
	{
		return lastCode;
	}

	public void setLastCode(int lastCode)
	{
		this.lastCode = lastCode;
	}

	public boolean isLastRunPassed()
	{
		return lastRunPassed;
	}

	public void setLastRunPassed(boolean lastRunPassed)
	{
		this.lastRunPassed = lastRunPassed;
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
