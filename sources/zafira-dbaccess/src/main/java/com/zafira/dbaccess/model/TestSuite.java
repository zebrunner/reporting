package com.zafira.dbaccess.model;

import org.apache.commons.lang3.StringUtils;

public class TestSuite extends AbstractEntity
{
	private static final long serialVersionUID = -1847933012610222160L;

	private String name;
	private String description;
	private User user;

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

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj != null && obj instanceof TestSuite)
		{
			TestSuite testSuite = (TestSuite) obj;
			return StringUtils.equals(this.name, testSuite.name) && 
					StringUtils.equals(this.description, testSuite.description) && 
					this.user.equals(testSuite.getUser());
		}
		else
		{
			return false;
		}
	}
}
