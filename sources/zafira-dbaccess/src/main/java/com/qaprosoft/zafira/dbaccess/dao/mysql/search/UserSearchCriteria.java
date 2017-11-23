package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

import java.util.Date;

public class UserSearchCriteria extends SearchCriteria implements DateSearchCriteria
{
	private Long id;
	private String username;
	private String firstLastName;
	private String email;
	private Date date;
	private String groupName;
	private Date fromDate;
	private Date toDate;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
