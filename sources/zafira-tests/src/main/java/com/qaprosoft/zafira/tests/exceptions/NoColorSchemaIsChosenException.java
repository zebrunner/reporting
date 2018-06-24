package com.qaprosoft.zafira.tests.exceptions;

public class NoColorSchemaIsChosenException extends RuntimeException {

	private static final long serialVersionUID = -302039893494696852L;

	public NoColorSchemaIsChosenException() {
        super();
    }

    public NoColorSchemaIsChosenException(String message) {
        super(message);
    }

    public NoColorSchemaIsChosenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoColorSchemaIsChosenException(Throwable cause) {
        super(cause);
    }

    protected NoColorSchemaIsChosenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
