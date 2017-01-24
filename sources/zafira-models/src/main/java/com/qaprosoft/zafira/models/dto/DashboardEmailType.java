package com.qaprosoft.zafira.models.dto;

import java.util.List;

public class DashboardEmailType extends EmailType
{
	private List<String> urls;
	private String hostname;

	public List<String> getUrls()
	{
		return urls;
	}

	public void setUrls(List<String> urls)
	{
		this.urls = urls;
	}

	public String getHostname()
	{
		return hostname;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}
}
