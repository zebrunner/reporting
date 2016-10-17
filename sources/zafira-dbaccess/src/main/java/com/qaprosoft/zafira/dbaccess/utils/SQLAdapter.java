package com.qaprosoft.zafira.dbaccess.utils;

public class SQLAdapter
{
	String sql;
	
	public SQLAdapter()
	{
	}

	public SQLAdapter(String sql)
	{
		this.sql = sql;
	}

	public String getSql()
	{
		return sql;
	}

	public void setSql(String sql)
	{
		this.sql = sql;
	}
}