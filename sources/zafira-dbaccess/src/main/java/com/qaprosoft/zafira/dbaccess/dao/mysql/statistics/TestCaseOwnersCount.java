package com.qaprosoft.zafira.dbaccess.dao.mysql.statistics;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TestCaseOwnersCount implements Serializable
{
	private static final long serialVersionUID = 5963824070315837074L;
	
	private int count;
	private String userName;

	public TestCaseOwnersCount()
	{
		super();
	}

	public TestCaseOwnersCount(int count, String userName)
	{
		super();
		this.count = count;
		this.userName = userName;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
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
