package com.qaprosoft.zafira.models.db;

public class Project extends AbstractEntity
{
	private static final long serialVersionUID = 1489890001065170767L;

	private String name;
	private String description;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
