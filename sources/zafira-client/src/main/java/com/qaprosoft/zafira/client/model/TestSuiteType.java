package com.qaprosoft.zafira.client.model;


public class TestSuiteType extends AbstractType
{
	private String name;
	private String description;
	private String userName;
	
	public TestSuiteType(String name, String userName)
	{
		this.name = name;
		this.userName = userName;
	}

	public TestSuiteType(String name, String description, String userName)
	{
		this.name = name;
		this.description = description;
		this.userName = userName;
	}

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

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}
}
