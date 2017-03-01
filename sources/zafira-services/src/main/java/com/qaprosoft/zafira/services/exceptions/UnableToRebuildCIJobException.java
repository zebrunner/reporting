package com.qaprosoft.zafira.services.exceptions;

public class UnableToRebuildCIJobException extends ServiceException
{
	private static final long serialVersionUID = 739932392670306969L;

	public UnableToRebuildCIJobException()
	{
		super();
	}

	public UnableToRebuildCIJobException(String message)
	{
		super(message);
	}

	public UnableToRebuildCIJobException(Throwable cause)
	{
		super(cause);
	}

	public UnableToRebuildCIJobException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
