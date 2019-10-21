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
package com.qaprosoft.zafira.models.dto.errors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ErrorCodeSerializer.class)
public enum ErrorCode {

    VALIDATION_ERROR(0),
    INVALID_VALUE(1),
    INVALID_MIME_TYPE(2),

    UNAUTHORIZED(401),
    FORBIDDEN(403),

    INTERNAL_SERVER_ERROR(500),

    INVALID_TEST_RUN(1001),
    RESOURCE_NOT_FOUND(1002),
    TEST_RUN_NOT_REBUILT(1004),
    USER_NOT_FOUND(1005),
    ENTITY_ALREADY_EXISTS(1006),
    PROJECT_NOT_EXISTS(1008),

    INTEGRATION_UNAVAILABLE(2001),
    UNHEALTHY_STATUS(2002);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}