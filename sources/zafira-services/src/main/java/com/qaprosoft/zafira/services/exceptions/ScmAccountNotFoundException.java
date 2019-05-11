package com.qaprosoft.zafira.services.exceptions;

public class ScmAccountNotFoundException extends ServiceException {
    private static final long serialVersionUID = -3765712524095629480L;

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
