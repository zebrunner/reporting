package com.qaprosoft.zafira.dbaccess.model;

import org.apache.commons.lang3.StringUtils;

public class User extends AbstractEntity
{
	private static final long serialVersionUID = 2720141152633805371L;

	private String userName;
	private String email;
	private String firstName;
	private String lastName;

	public User()
	{
	}
	
	public User(String userName)
	{
		this.userName = userName;
	}
	
	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj != null && obj instanceof User)
		{
			User user = (User) obj;
			return this.getId() == user.getId() && 
					StringUtils.equals(this.userName, user.userName) && 
					StringUtils.equals(this.firstName, user.firstName) && 
					StringUtils.equals(this.lastName, user.lastName) && 
					StringUtils.equals(this.email, user.email);
		}
		else
		{
			return false;
		}
	}
}
