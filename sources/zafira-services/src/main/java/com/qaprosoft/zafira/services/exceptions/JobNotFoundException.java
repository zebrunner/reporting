package com.qaprosoft.zafira.services.exceptions;

public class JobNotFoundException extends ServiceException
{
	private static final long serialVersionUID = -8012121652412303537L;
	
	public JobNotFoundException()
	{
		super();
	}

	public JobNotFoundException(String message)
	{
		super(message);
	}

	public JobNotFoundException(Throwable cause)
	{
		super(cause);
	}

	public JobNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
