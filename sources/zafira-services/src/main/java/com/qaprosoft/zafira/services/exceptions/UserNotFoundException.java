package com.qaprosoft.zafira.services.exceptions;

public class UserNotFoundException extends ServiceException
{
	private static final long serialVersionUID = 201855553642859489L;

	public UserNotFoundException()
	{
		super();
	}

	public UserNotFoundException(String message)
	{
		super(message);
	}

	public UserNotFoundException(Throwable cause)
	{
		super(cause);
	}

	public UserNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}