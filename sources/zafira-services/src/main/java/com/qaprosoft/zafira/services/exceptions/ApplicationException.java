/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public abstract class ApplicationException extends RuntimeException {

    public interface ErrorDetail {

        Integer getCode();

        String getMessageKey();

    }

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum UnexpectedErrorDetail implements ErrorDetail {

        UNEXPECTED_SERVER_ERROR(2666);

        private final Integer code;
        private String messageKey;

    }

    private ErrorDetail errorDetail;

    ApplicationException(ErrorDetail errorDetail, String message) {
        super(message);
        this.errorDetail = errorDetail;
    }

    ApplicationException(ErrorDetail errorDetail, String format, Object... args) {
        super(String.format(format, args));
        this.errorDetail = errorDetail;
    }

    ApplicationException(ErrorDetail errorDetail, String message, Throwable cause) {
        super(message, cause);
        this.errorDetail = errorDetail;
    }

    ApplicationException(ErrorDetail errorDetail, Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
        this.errorDetail = errorDetail;
    }

    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message, Throwable cause, boolean writableStackTrace) {
        super(message, cause, false, writableStackTrace);
    }

}
