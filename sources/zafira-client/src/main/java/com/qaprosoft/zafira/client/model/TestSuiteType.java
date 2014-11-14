package com.qaprosoft.zafira.client.model;


public class TestSuiteType extends AbstractType
{
	private String name;
	private String description;
	private Long userId;
	
	public TestSuiteType(String name, Long userId)
	{
		this.name = name;
		this.userId = userId;
	}

	public TestSuiteType(String name, String description, Long userId)
	{
		this.name = name;
		this.description = description;
		this.userId = userId;
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

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}
}
