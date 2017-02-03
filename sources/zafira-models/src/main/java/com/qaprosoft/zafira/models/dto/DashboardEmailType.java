package com.qaprosoft.zafira.models.dto;

import java.util.List;

public class DashboardEmailType extends EmailType
{
	private static final long serialVersionUID = 4407281347247491977L;
	private List<String> urls;
	private String hostname;
	private String dimension;
	
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

	public String getDimension()
	{
		return dimension;
	}

	public void setDimension(String dimension)
	{
		this.dimension = dimension;
	}
}
