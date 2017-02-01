package com.qaprosoft.zafira.models.db;

public class Attribute extends AbstractEntity
{
	private static final long serialVersionUID = 6708791122991478693L;

	private String key;
	private String value;
	
	public Attribute()
	{
	}

	public Attribute(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}