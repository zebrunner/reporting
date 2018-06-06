package com.qaprosoft.zafira.services.exceptions;

public class IntegrationException extends ServiceException
{
    public IntegrationException() {
        super();
    }

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(Throwable cause) {
        super(cause);
    }

    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
