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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ErrorResponse
{
	@JsonInclude(Include.ALWAYS)
	private Error error;
	@JsonInclude(Include.NON_EMPTY)
	private List<Error> validationErrors;

	public Error getError()
	{
		return error;
	}

	public ErrorResponse setError(Error error)
	{
		this.error = error;
		return this;
	}

	public List<Error> getValidationErrors()
	{
		if (null == validationErrors)
		{
			validationErrors = new ArrayList<>();
		}
		return validationErrors;
	}

	public void setValidationErrors(List<Error> validationErrors)
	{
		this.validationErrors = validationErrors;
	}
}
