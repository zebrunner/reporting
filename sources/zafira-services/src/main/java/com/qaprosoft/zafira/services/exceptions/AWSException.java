package com.qaprosoft.zafira.services.exceptions;

public class AWSException extends ServiceException
{
	private static final long serialVersionUID = 201855553642859489L;

	public AWSException()
	{
		super();
	}

	public AWSException(String message)
	{
		super(message);
	}

	public AWSException(Throwable cause)
	{
		super(cause);
	}

	public AWSException(String message, Throwable cause)
	{
		super(message, cause);
	}
}