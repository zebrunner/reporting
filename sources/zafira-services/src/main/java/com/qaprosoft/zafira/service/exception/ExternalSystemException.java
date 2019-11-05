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

public class ExternalSystemException extends ApplicationException {

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum ExternalSystemErrorDetail implements ErrorDetail {

        JIRA_ISSUE_CAN_NOT_BE_FOUND(2150),
        POSTGRES_VERSION_CAN_NOT_BE_FOUND(2151),
        LDAP_USER_DOES_NOT_EXIST(2152),
        LDAP_AUTHENTICATION_FAILED(2153),
        JWT_TOKEN_IS_INVALID(2154),
        JENKINS_JOB_DOES_NOT_EXIST(2155),
        JENKINS_BUILD_DOES_NOT_EXIST(2156),
        JENKINS_QUEUE_REFERENCE_IS_NOT_OBTAINED(2157);

        private final Integer code;
        private String messageKey;

    }

    public ExternalSystemException(ExternalSystemErrorDetail errorDetail, String message, Throwable cause) {
        super(errorDetail, message, cause);
    }
    public ExternalSystemException(ExternalSystemErrorDetail errorDetail, String message, Object... args) {
        super(errorDetail, message, args);
    }

    public ExternalSystemException(ExternalSystemErrorDetail errorDetail, String message) {
        super(errorDetail, message);
    }

    public ExternalSystemException(String message, Throwable cause) {
        super(message, cause);
    }

}
