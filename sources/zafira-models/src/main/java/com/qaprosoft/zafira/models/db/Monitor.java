package com.qaprosoft.zafira.models.db;

public class Monitor extends AbstractEntity
{

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
	private String cronExpression;
	private boolean active;
	private String recipients;
	private Type type;
	private int expectedResponseCode;

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

	public String getCronExpression()
	{
		return cronExpression;
	}

	public void setCronExpression(String cronExpression)
	{
		this.cronExpression = cronExpression;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public int getExpectedResponseCode()
	{
		return expectedResponseCode;
	}

	public void setExpectedResponseCode(int expectedResponseCode)
	{
		this.expectedResponseCode = expectedResponseCode;
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

	@Override
	public String toString()
	{
		return "Monitor{" +
				"name='" + name + '\'' +
				", url='" + url + '\'' +
				", httpMethod=" + httpMethod +
				", requestBody='" + requestBody + '\'' +
				", cronExpression='" + cronExpression + '\'' +
				", active=" + active +
				", emails='" + recipients + '\'' +
				", type=" + type +
				", expectedResponseCode=" + expectedResponseCode +
				'}';
	}
}


