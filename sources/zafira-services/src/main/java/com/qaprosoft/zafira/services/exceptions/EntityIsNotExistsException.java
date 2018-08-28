package com.qaprosoft.zafira.services.exceptions;

public class EntityIsNotExistsException extends ServiceException {

    private static final long serialVersionUID = 419121635807145174L;

    private static final String ERROR_MESSAGE = "%s is not exists.";

    public EntityIsNotExistsException() {
    }

    public EntityIsNotExistsException(Class entityClass) {
        super(buildMessage(entityClass));
    }

    public EntityIsNotExistsException(Throwable cause) {
        super(cause);
    }

    public EntityIsNotExistsException(Class entityClass, Throwable cause) {
        super(buildMessage(entityClass), cause);
    }

    private static String buildMessage(Class entityClass) {
        return String.format(ERROR_MESSAGE, entityClass.getSimpleName());
    }
}
