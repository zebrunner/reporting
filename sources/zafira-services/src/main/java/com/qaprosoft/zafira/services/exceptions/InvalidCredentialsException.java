package com.qaprosoft.zafira.services.exceptions;

public class InvalidCredentialsException extends ServiceException
{
	private static final long serialVersionUID = -7280348651406679381L;

	public InvalidCredentialsException()
	{
		super();
	}

	public InvalidCredentialsException(String message)
	{
		super(message);
	}

	public InvalidCredentialsException(Throwable cause)
	{
		super(cause);
	}

	public InvalidCredentialsException(String message, Throwable cause)
	{
		super(message, cause);
	}
}