package com.qaprosoft.zafira.services.exceptions;

public class ProcessingException extends ApplicationException {

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
