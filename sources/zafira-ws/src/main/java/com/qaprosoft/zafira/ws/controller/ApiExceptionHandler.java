package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.models.dto.errors.AdditionalErrorData;
import com.qaprosoft.zafira.models.dto.errors.Error;
import com.qaprosoft.zafira.models.dto.errors.ErrorCode;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import com.qaprosoft.zafira.services.exceptions.EntityAlreadyExistsException;
import com.qaprosoft.zafira.services.exceptions.EntityNotExistsException;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.exceptions.InvalidTestRunException;
import com.qaprosoft.zafira.services.exceptions.JobNotFoundException;
import com.qaprosoft.zafira.services.exceptions.ProjectNotFoundException;
import com.qaprosoft.zafira.services.exceptions.TestNotFoundException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;
import com.qaprosoft.zafira.services.exceptions.UnableToRebuildCIJobException;
import com.qaprosoft.zafira.services.exceptions.UnhealthyStateException;
import com.qaprosoft.zafira.services.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Centralized API exception handler
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private static final String ERR_MSG_METHOD_ARGUMENT_TYPE_MISMATCH = "Request parameter has invalid type.";
    private static final String ERR_MSG_INTERNAL_SERVER_ERROR = "Unexpected error has occurred. Please try again later.";
    private static final String ERR_MSG_DEBUG_INFO = "Error message: [%s]. Caused by: [%s]";

    private boolean debugEnabled;

    public void setDebugEnabled(@Value("${zafira.debug-enabled:false}") boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleJobNotFoundException(JobNotFoundException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.JOB_NOT_FOUND));
        return response;
    }

    @ExceptionHandler(TestRunNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTestRunNotFoundException(TestRunNotFoundException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.TEST_RUN_NOT_FOUND));
        return response;
    }

    @ExceptionHandler(TestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTestNotFoundException(TestNotFoundException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.TEST_RUN_NOT_FOUND));
        return response;
    }

    @ExceptionHandler(InvalidTestRunException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidTestRunException(InvalidTestRunException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.INVALID_TEST_RUN));
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(Exception e) {
        ErrorResponse response = new ErrorResponse();
        BindingResult bindingResult = null;

        response.setError(new Error(ErrorCode.VALIDATION_ERROR));

        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        }

        if (null != bindingResult) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                Error error = new Error(ErrorCode.INVALID_VALUE, fieldError.getField(), fieldError.getDefaultMessage());

                Object rejectedValue = fieldError.getRejectedValue();

                if ((rejectedValue instanceof String) || (rejectedValue instanceof Number)) {
                    AdditionalErrorData additionalErrorData = new AdditionalErrorData();

                    additionalErrorData.setValue(rejectedValue);
                    error.setAdditional(additionalErrorData);
                }

                response.getValidationErrors().add(error);
            }
        }

        return response;
    }

    @ExceptionHandler(UnableToRebuildCIJobException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnableToRebuildCIJobException(UnableToRebuildCIJobException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.TEST_RUN_NOT_REBUILT));
        return response;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.UNAUTHORIZED));
        return response;
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenOperationException(ForbiddenOperationException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.FORBIDDENT, null, e.isShowMessage() ? e.getMessage() : null));
        return response;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.USER_NOT_FOUND));
        return response;
    }

    @ExceptionHandler(IntegrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIntegrationException(IntegrationException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.INTEGRATION_UNAVAILABLE));
        return response;
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityIsAlreadyExistsException(EntityAlreadyExistsException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.ENTITY_ALREADY_EXISTS, e.getFieldName(), e.getMessage()));
        return response;
    }

    @ExceptionHandler(EntityNotExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityIsNotExistsException(EntityNotExistsException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.ENTITY_NOT_EXISTS, e.getMessage()));
        return response;
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProjectNotFoundException(ProjectNotFoundException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.PROJECT_NOT_EXISTS, e.getMessage()));
        return response;
    }

    @ExceptionHandler(UnhealthyStateException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleUnhealthyStateException(UnhealthyStateException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.UNHEALTHY_STATUS, "reason", e.getMessage()));
        return response;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.INVALID_VALUE, e.getName(), ERR_MSG_METHOD_ARGUMENT_TYPE_MISMATCH));
        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleOtherException(Exception e) {
        LOGGER.error("Unexpected internal server error", e);

        ErrorResponse response = new ErrorResponse();
        if (debugEnabled) {
            String errorMessage = e.getMessage();
            Throwable cause = e.getCause();
            String causedByMessage = cause != null ? cause.getMessage() : "message not available";
            response.setError(new Error(ErrorCode.INTERNAL_SERVER_ERROR, String.format(ERR_MSG_DEBUG_INFO, errorMessage, causedByMessage)));
        } else {
            response.setError(new Error(ErrorCode.INTERNAL_SERVER_ERROR, ERR_MSG_INTERNAL_SERVER_ERROR));
        }
        return response;
    }

}
