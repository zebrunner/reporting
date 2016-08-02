package com.qaprosoft.zafira.dbaccess.dao.mysql.statistics;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TestCaseImplementationCount implements Serializable
{
	private static final long serialVersionUID = 5963824070315837074L;
	
	private int count;
	private Date date;

	public TestCaseImplementationCount()
	{
		super();
	}

	public TestCaseImplementationCount(int count, Date date)
	{
		super();
		this.count = count;
		this.date = date;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}
}
