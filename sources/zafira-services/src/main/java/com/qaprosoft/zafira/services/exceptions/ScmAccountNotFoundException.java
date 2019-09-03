package com.qaprosoft.zafira.services.exceptions;

public class ScmAccountNotFoundException extends ServiceException {

    public ScmAccountNotFoundException() {
        super();
    }

    public ScmAccountNotFoundException(String message) {
        super(message);
    }

    public ScmAccountNotFoundException(Throwable cause) {
        super(cause);
    }

    public ScmAccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
