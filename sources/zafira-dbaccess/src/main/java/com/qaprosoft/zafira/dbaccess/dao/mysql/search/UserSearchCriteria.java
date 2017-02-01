package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

import java.util.Date;

public class UserSearchCriteria extends SearchCriteria
{
	private Long id;
	private String userName;
	private String firstLastName;
	private String email;
	private Date date;
	private String groupName;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
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

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getFirstLastName()
	{
		return firstLastName;
	}

	public void setFirstLastName(String firstLastName)
	{
		this.firstLastName = firstLastName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
