package com.zafira.services.exceptions;

public class ServiceException extends Exception
{
	private static final long serialVersionUID = -8438824073605204525L;

	public ServiceException()
	{
		super();
	}

	public ServiceException(String message)
	{
		super(message);
	}

	public ServiceException(Throwable cause)
	{
		super(cause);
	}

	public ServiceException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
