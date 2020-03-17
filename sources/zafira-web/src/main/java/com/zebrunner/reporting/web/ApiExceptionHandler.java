package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.dto.errors.AdditionalErrorData;
import com.zebrunner.reporting.domain.dto.errors.Error;
import com.zebrunner.reporting.domain.dto.errors.ErrorCode;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import com.zebrunner.reporting.service.exception.ApplicationException;
import com.zebrunner.reporting.service.exception.AuthException;
import com.zebrunner.reporting.service.exception.ForbiddenOperationException;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.IntegrationException;
import com.zebrunner.reporting.service.exception.ProcessingException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private static final String ERR_MSG_METHOD_ARGUMENT_TYPE_MISMATCH = "Request parameter has invalid type.";
    private static final String ERR_MSG_UNACCEPTABLE_MIME_TYPE = "Requested content type can not be served. Supported types: %s";
    private static final String ERR_MSG_INTERNAL_SERVER_ERROR = "Unexpected error has occurred. Please try again later.";
    private static final String ERR_MSG_DEBUG_INFO = "Error message: [%s]. Caused by: [%s]";

    private boolean debugEnabled;

    public void setDebugEnabled(@Value("${zafira.debug-enabled:false}") boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.RESOURCE_NOT_FOUND, e.getMessage()));
        return response;
    }

    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<ErrorResponse> handleIllegalOperationException(IllegalOperationException e) {
        ResponseEntity<ErrorResponse> responseEntity;
        ErrorResponse response = new ErrorResponse();
        // We need to return code 200 for all auth-related operations
        // to avoid bruteforce obtaining of user data
        ApplicationException.ErrorDetail errorDetail = e.getErrorDetail();
        if (errorDetail.equals(IllegalOperationException.IllegalOperationErrorDetail.TOKEN_RESET_IS_NOT_POSSIBLE) || errorDetail.equals(IllegalOperationException.IllegalOperationErrorDetail.CREDENTIALS_RESET_IS_NOT_POSSIBLE)) {
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            response.setError(new Error(ErrorCode.VALIDATION_ERROR, e.getMessage()));
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
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

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthException(AuthException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.UNAUTHORIZED));
        return response;
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenOperationException(ForbiddenOperationException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.FORBIDDEN, null, e.isShowMessage() ? e.getMessage() : null));
        return response;
    }

    @ExceptionHandler(ProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProcessingException(ProcessingException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.VALIDATION_ERROR, e.getMessage()));
        return response;
    }

    @ExceptionHandler(IntegrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIntegrationException(IntegrationException e) {
        LOGGER.error("Unable to complete operation against integration", e);
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.INTEGRATION_UNAVAILABLE));
        Map<String, String> additionalErrorInfo = e.getAdditionalInfo();
        if (additionalErrorInfo != null && !additionalErrorInfo.isEmpty()) {
            List<Error> errors = additionalErrorInfo.entrySet().stream()
                                                    .map(entry -> new Error(ErrorCode.INTEGRATION_UNAVAILABLE, entry.getKey(), entry.getValue()))
                                                    .collect(Collectors.toList());
            response.setValidationErrors(errors);
        }
        return response;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.INVALID_VALUE, e.getName(), ERR_MSG_METHOD_ARGUMENT_TYPE_MISMATCH));
        return response;
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        List<MediaType> mediaTypes = e.getSupportedMediaTypes();
        String supportedTypes = mediaTypes.stream()
                                          .map(MimeType::toString)
                                          .collect(Collectors.joining(", "));

        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.INVALID_MIME_TYPE, String.format(ERR_MSG_UNACCEPTABLE_MIME_TYPE, supportedTypes)));
        return response;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBindException(BindException e) {
        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.INVALID_VALUE, ERR_MSG_METHOD_ARGUMENT_TYPE_MISMATCH));
        return response;
    }

    /**
     * This handler will only be invoked if certain application exception occures. It provides generic handling
     * to compensate lack of error context. Once such exception occurs it should be properly addressed ASAP.
     */
    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleApplicationException(ApplicationException e) {
        LOGGER.error("Unhandled application exception occured", e);

        ErrorResponse response = new ErrorResponse();
        response.setError(new Error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage()));
        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherException(Exception e) {
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
