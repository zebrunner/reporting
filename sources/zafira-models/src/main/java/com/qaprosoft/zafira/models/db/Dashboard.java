package com.qaprosoft.zafira.models.db;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AbstractEntity
{
	private static final long serialVersionUID = -6778089455852822053L;

	private String title;
	private List<Widget> widgets = new ArrayList<Widget>();
	private Type type;
	private Integer position;

	public enum Type {
		GENERAL, PERFORMANCE, USER_PERFORMANCE;
	}

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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
}
