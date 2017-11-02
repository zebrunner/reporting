package com.qaprosoft.zafira.models.db;

public class Widget extends AbstractEntity
{
	private static final long serialVersionUID = -750759195176951157L;
	
	private String title;
	private String position;
	private String sql;
	private String model;
	private boolean refreshable;
	private String type;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getPosition()
	{
		return position;
	}

	public void setPosition(String position)
	{
		this.position = position;
	}

	public String getSql()
	{
		return sql;
	}

	public void setSql(String sql)
	{
		this.sql = sql;
	}

	public String getModel()
	{
		return model;
	}

	public boolean isRefreshable() {
		return refreshable;
	}

	public void setRefreshable(boolean refreshable) {
		this.refreshable = refreshable;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}
