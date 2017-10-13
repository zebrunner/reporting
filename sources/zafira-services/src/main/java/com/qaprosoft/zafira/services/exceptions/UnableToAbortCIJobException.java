package com.qaprosoft.zafira.services.exceptions;

public class UnableToAbortCIJobException extends ServiceException{

    public UnableToAbortCIJobException() {
        super();
    }

    public UnableToAbortCIJobException(String message) {
        super(message);
    }

    public UnableToAbortCIJobException(Throwable cause) {
        super(cause);
    }

    public UnableToAbortCIJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
