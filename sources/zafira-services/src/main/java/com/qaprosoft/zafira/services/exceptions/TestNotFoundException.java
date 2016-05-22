package com.qaprosoft.zafira.services.exceptions;

public class TestNotFoundException extends ServiceException
{
	private static final long serialVersionUID = -3668001782851976950L;

	public TestNotFoundException()
	{
		super();
	}

	public TestNotFoundException(String message)
	{
		super(message);
	}

	public TestNotFoundException(Throwable cause)
	{
		super(cause);
	}

	public TestNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
