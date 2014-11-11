package com.qaprosoft.zafira.ws.dto.errors;

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

	public void setError(Error error)
	{
		this.error = error;
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
