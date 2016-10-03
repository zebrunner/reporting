package com.qaprosoft.zafira.dbaccess.model;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AbstractEntity
{
	private static final long serialVersionUID = -6778089455852822053L;

	private String title;
	private List<Widget> widgets = new ArrayList<Widget>();

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public List<Widget> getWidgets()
	{
		return widgets;
	}

	public void setWidgets(List<Widget> widgets)
	{
		this.widgets = widgets;
	}
}
