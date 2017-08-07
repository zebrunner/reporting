package com.qaprosoft.zafira.services.exceptions;

public class ForbiddenOperationException extends ServiceException
{
	private static final long serialVersionUID = -1840720518398070678L;

	public ForbiddenOperationException()
	{
		super();
	}

	public ForbiddenOperationException(String message)
	{
		super(message);
	}

	public ForbiddenOperationException(Throwable cause)
	{
		super(cause);
	}

	public ForbiddenOperationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}