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
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestNotFoundException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;
import com.qaprosoft.zafira.services.exceptions.UnableToRebuildCIJobException;
import com.qaprosoft.zafira.services.exceptions.UnhealthyStateException;
import com.qaprosoft.zafira.services.exceptions.UserNotFoundException;
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

import java.util.List;

/**
 * Centralized API exception handler
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_MSG = "Unexpected error has occurred. Please try again later.";

    @Value("${zafira.debugMode:false}")
    private Boolean debugMode;

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleJobNotFoundException(JobNotFoundException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.JOB_NOT_FOUND));
        return result;
    }

    @ExceptionHandler(TestRunNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTestRunNotFoundException(TestRunNotFoundException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.TEST_RUN_NOT_FOUND));
        return result;
    }

    @ExceptionHandler(TestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTestNotFoundException(TestNotFoundException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.TEST_RUN_NOT_FOUND));
        return result;
    }

    @ExceptionHandler(InvalidTestRunException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidTestRunException(InvalidTestRunException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.INVALID_TEST_RUN));
        return result;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(Exception e) {
        ErrorResponse result = new ErrorResponse();
        BindingResult bindingResult = null;

        result.setError(new Error(ErrorCode.VALIDATION_ERROR));

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

                result.getValidationErrors().add(error);
            }
        }

        return result;
    }

    @ExceptionHandler(UnableToRebuildCIJobException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnableToRebuildCIJobException(UnableToRebuildCIJobException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.TEST_RUN_NOT_REBUILT));
        return result;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.UNAUTHORIZED));
        return result;
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenOperationException(ForbiddenOperationException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.FORBIDDENT, null, e.isShowMessage() ? e.getMessage() : null));
        return result;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.USER_NOT_FOUND));
        return result;
    }

    @ExceptionHandler(IntegrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIntegrationException(IntegrationException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.INTEGRATION_UNAVAILABLE));
        return result;
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityIsAlreadyExistsException(EntityAlreadyExistsException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.ENTITY_ALREADY_EXISTS, e.getFieldName(), e.getMessage()));
        return result;
    }

    @ExceptionHandler(EntityNotExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityIsNotExistsException(EntityNotExistsException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.ENTITY_NOT_EXISTS, e.getMessage()));
        return result;
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProjectNotFoundException(ProjectNotFoundException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.PROJECT_NOT_EXISTS, e.getMessage()));
        return result;
    }

    @ExceptionHandler(UnhealthyStateException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleUnhealthyStateException(UnhealthyStateException e) {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.UNHEALTHY_STATUS, "reason", e.getMessage()));
        return result;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleOtherException(Exception e) throws ServiceException {
        if (debugMode) {
            throw new ServiceException(e);
        }
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG));
        return result;
    }

}
