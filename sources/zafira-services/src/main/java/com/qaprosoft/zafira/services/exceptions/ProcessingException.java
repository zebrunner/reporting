package com.qaprosoft.zafira.services.exceptions;

public class ProcessingException extends ServiceException {

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
