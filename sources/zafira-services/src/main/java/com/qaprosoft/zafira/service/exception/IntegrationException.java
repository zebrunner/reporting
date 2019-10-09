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
 ******************************************************************************/
package com.qaprosoft.zafira.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class IntegrationException extends ApplicationException {

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum IntegrationExceptionDetail implements ErrorDetail {

        JENKINS_SERVER_INITIALIZATION_FAILED(2200);

        private final Integer code;
        private String messageKey;

    }

    public IntegrationException() {
        super();
    }

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(Throwable cause) {
        super(cause);
    }

    public IntegrationException(ErrorDetail errorDetail, String message, Throwable cause) {
        super(errorDetail, message, cause);
    }

    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
