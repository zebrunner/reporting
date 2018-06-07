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
package com.qaprosoft.zafira.models.dto.errors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qaprosoft.zafira.models.dto.errors.ErrorCodeSerializer;

@JsonSerialize(using = ErrorCodeSerializer.class)
public enum ErrorCode
{

	VALIDATION_ERROR(0),
	INVALID_VALUE(1),

	UNAUTHORIZED(401),
	FORBIDDENT(403),
	
	JOB_NOT_FOUND(1000),
	INVALID_TEST_RUN(1001),
	TEST_RUN_NOT_FOUND(1002),
	TEST_NOT_FOUND(1003),
	TEST_RUN_NOT_REBUILT(1004),
	USER_NOT_FOUND(1005),
	INTEGRATION_UNAVAILABLE(1006);

	private int code;

	private ErrorCode(int code)
	{
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}
}