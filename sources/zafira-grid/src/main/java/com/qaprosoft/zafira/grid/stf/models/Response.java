package com.qaprosoft.zafira.grid.stf.models;

public class Response<T>
{
	private int status;
	private T object;
	
	public Response(int status, T object)
	{
		this.status = status;
		this.object = object;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public T getObject()
	{
		return object;
	}

	public void setObject(T object)
	{
		this.object = object;
	}
}