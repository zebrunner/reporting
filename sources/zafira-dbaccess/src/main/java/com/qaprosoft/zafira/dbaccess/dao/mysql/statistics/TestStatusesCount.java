package com.qaprosoft.zafira.dbaccess.dao.mysql.statistics;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.dbaccess.model.Test.Status;

@JsonInclude(Include.NON_NULL)
public class TestStatusesCount implements Serializable
{
	private static final long serialVersionUID = 5963824070315837074L;
	
	private int count;
	private Status status;
	private Date date;

	public TestStatusesCount()
	{
		super();
	}

	public TestStatusesCount(int count, Status status)
	{
		super();
		this.count = count;
		this.status = status;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
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
