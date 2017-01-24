package com.qaprosoft.zafira.models.push;

import java.util.UUID;

public class AbstractPush 
{
	public static enum Type 
	{
		TEST_RUN, TEST
	}

	private Type type;
	private String uid;

	public AbstractPush(Type type) 
	{
		this.type = type;
		this.uid = UUID.randomUUID().toString();
	}

	public Type getType() 
	{
		return type;
	}

	public void setType(Type type) 
	{
		this.type = type;
	}

	public String getUid() 
	{
		return uid;
	}

	public void setUid(String uid) 
	{
		this.uid = uid;
	}
}
