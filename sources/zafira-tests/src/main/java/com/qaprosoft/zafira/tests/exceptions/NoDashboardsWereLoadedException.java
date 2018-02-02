package com.qaprosoft.zafira.tests.exceptions;

public class NoDashboardsWereLoadedException extends RuntimeException
{ public NoDashboardsWereLoadedException()
	{
		super();
	}

	public NoDashboardsWereLoadedException(String message)
	{
		super(message);
	}

	public NoDashboardsWereLoadedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NoDashboardsWereLoadedException(Throwable cause)
	{
		super(cause);
	}

	protected NoDashboardsWereLoadedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
