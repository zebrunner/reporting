package com.qaprosoft.zafira.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Exception that is meant to be thrown when error, related to processing application own resources (e.g. application
 * configuration and/or properties are invalid) occurs.
 * Reserved range 2100 - 2119
 */
public class ProcessingException extends ApplicationException {

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum ProcessingErrorDetail implements ErrorDetail {

        EMPTY_OR_MISSING_CRYPTO_KEY(2100),
        WIDGET_QUERY_EXECUTION_ERROR(2101),
        UNPROCESSABLE_XML_ENTITY(2102);

        private final Integer code;
        private String messageKey;

    }

    public ProcessingException(ProcessingErrorDetail errorDetail, String message) {
        super(errorDetail, message);
    }

    public ProcessingException(ProcessingErrorDetail errorDetail, String message, Throwable cause) {
        super(errorDetail, message, cause);
    }

}
