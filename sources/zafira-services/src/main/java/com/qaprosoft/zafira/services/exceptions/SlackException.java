package com.qaprosoft.zafira.services.exceptions;

public class SlackException extends ServiceException {

    private static final long serialVersionUID = -4849845839266518820L;

    public SlackException() {
        super();
    }

    public SlackException(String message) {
        super(message);
    }

    public SlackException(Throwable cause) {
        super(cause);
    }

    public SlackException(String message, Throwable cause) {
        super(message, cause);
    }

}
