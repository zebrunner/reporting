package com.qaprosoft.zafira.services.exceptions;

public class SlackNotificationNotSendException extends ServiceException {

    private static final long serialVersionUID = -4849845839266518820L;

    public SlackNotificationNotSendException() {
        super();
    }

    public SlackNotificationNotSendException(String message) {
        super(message);
    }

    public SlackNotificationNotSendException(Throwable cause) {
        super(cause);
    }

    public SlackNotificationNotSendException(String message, Throwable cause) {
        super(message, cause);
    }

}
