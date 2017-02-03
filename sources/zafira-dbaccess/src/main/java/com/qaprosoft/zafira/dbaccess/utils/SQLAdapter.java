package com.qaprosoft.zafira.dbaccess.utils;

import java.util.List;

import com.qaprosoft.zafira.models.db.Attribute;

public class SQLAdapter
{
	private String sql;
	private List<Attribute> attributes;
	
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

	public List<Attribute> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes)
	{
		this.attributes = attributes;
	}
}