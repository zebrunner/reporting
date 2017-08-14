package com.qaprosoft.zafira.models.db;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AbstractEntity
{

	private static final long serialVersionUID = -562795025453363474L;

	private String title;
	private List<Widget> widgets = new ArrayList<Widget>();
	private boolean hidden;
	private Integer position;
	private List<Attribute> attributes;


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

	public Integer getPosition()
	{
		return position;
	}

	public void setPosition(Integer position)
	{
		this.position = position;
	}

	public List<Attribute> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes)
	{
		this.attributes = attributes;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}