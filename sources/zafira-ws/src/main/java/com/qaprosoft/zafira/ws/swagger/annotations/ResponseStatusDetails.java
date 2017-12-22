/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.ws.swagger.annotations;

import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {@io.swagger.annotations.ApiResponse(code = 200, message = "OK"),
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad request"),
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden"),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found"),
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server error")})
public @interface ResponseStatusDetails {
}
