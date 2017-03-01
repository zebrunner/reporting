package com.qaprosoft.zafira.models.db;

public class View extends AbstractEntity
{
	private static final long serialVersionUID = 3795611752266419360L;

	private String name;
	private Project project = new Project();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Project getProject()
	{
		return project;
	}

	public void setProject(Project project)
	{
		this.project = project;
	}
}