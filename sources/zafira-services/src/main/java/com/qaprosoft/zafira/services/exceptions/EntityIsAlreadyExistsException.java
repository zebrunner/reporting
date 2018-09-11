package com.qaprosoft.zafira.services.exceptions;

public class EntityIsAlreadyExistsException extends ServiceException {

    private static final long serialVersionUID = 5062713977366541469L;
    private static final String ERROR_MESSAGE = "%s with this %s is already exists.";
    private static final String ERROR_MESSAGE_WITH_VALUE = "%s with %s '%s' is already exists.";

    private String fieldName;
    private String fieldValue;

    public EntityIsAlreadyExistsException(String fieldName) {
        this.fieldName = fieldName;
    }

    public EntityIsAlreadyExistsException(String fieldName, Class entityClass) {
        super(buildMessage(fieldName, entityClass));
        this.fieldName = fieldName;
    }

    public EntityIsAlreadyExistsException(String fieldName, String fieldValue, Class entityClass) {
        super(buildMessage(fieldName, fieldValue, entityClass));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public EntityIsAlreadyExistsException(String fieldName, Throwable cause) {
        super(cause);
        this.fieldName = fieldName;
    }

    public EntityIsAlreadyExistsException(String fieldName, Throwable cause, Class entityClass) {
        super(buildMessage(fieldName, entityClass), cause);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    private static String buildMessage(String fieldName, Class entityClass) {
        return String.format(ERROR_MESSAGE, entityClass.getSimpleName(), fieldName);
    }

    private static String buildMessage(String fieldName, String fieldValue, Class entityClass) {
        return String.format(ERROR_MESSAGE_WITH_VALUE, entityClass.getSimpleName(), fieldName, fieldValue);
    }
}
