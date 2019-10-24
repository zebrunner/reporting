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
package com.qaprosoft.zafira.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Exception that is thrown to indicate that certain operation is not valid according to business logic of application
 * (e.g. when someone attempts to update recourse with a certain status that indicates immutable state at given moment -
 * simply put can not be updated).
 * Reserved range 2060 - 2079
 */
public class IllegalOperationException extends ApplicationException {

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum IllegalOperationErrorDetail implements ErrorDetail {

        USER_CAN_NOT_BE_CREATED(2060),
        DASHBOARD_CAN_NOT_BE_CREATED(2061),
        FILTER_CAN_NOT_BE_CREATED(2062),
        INTEGRATION_CAN_NOT_BE_CREATED(2063),
        JOB_CAN_NOT_BE_STARTED(2064),
        INVITATION_CAN_NOT_BE_CREATED(2065),
        ILLEGAL_FILTER_ACCESS(2066),
        TEST_RUN_CAN_NOT_BE_STARTED(2067),
        LAUNCHER_PRESET_CAN_NOT_BE_CREATED(2068);

        private final Integer code;
        private String messageKey;

    }

    public IllegalOperationException(IllegalOperationErrorDetail errorDetail, String message) {
        super(errorDetail, message);
    }

    public IllegalOperationException(IllegalOperationErrorDetail errorDetail, String message, Object... args) {
        super(errorDetail, message, args);
    }

}
