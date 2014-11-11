package com.qaprosoft.zafira.services.exceptions;

public class TestRunNotFoundException extends ServiceException
{
	private static final long serialVersionUID = 4538834387227891889L;

	public TestRunNotFoundException()
	{
		super();
	}

	public TestRunNotFoundException(String message)
	{
		super(message);
	}

	public TestRunNotFoundException(Throwable cause)
	{
		super(cause);
	}

	public TestRunNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
