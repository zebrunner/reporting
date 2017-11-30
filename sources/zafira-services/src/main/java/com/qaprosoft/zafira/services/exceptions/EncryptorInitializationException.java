package com.qaprosoft.zafira.services.exceptions;

/**
 * Created by irina on 18.8.17.
 */
public class EncryptorInitializationException extends ServiceException
{
	private static final long serialVersionUID = 1272617468905992114L;

	public EncryptorInitializationException()
	{
		super();
	}

	public EncryptorInitializationException(String message)
	{
		super(message);
	}

	public EncryptorInitializationException(Throwable cause)
	{
		super(cause);
	}

	public EncryptorInitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
