package com.qaprosoft.zafira.ws.dto.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Error
{
	@JsonInclude(Include.NON_EMPTY)
	private String field;
	@JsonInclude(Include.NON_NULL)
	private ErrorCode code;
	@JsonInclude(Include.NON_NULL)
	private AdditionalErrorData additional;

	public Error()
	{
	}

	public Error(ErrorCode code)
	{
		this.code = code;
	}

	public Error(ErrorCode code, String field)
	{
		this.code = code;
		this.field = field;
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public ErrorCode getCode()
	{
		return code;
	}

	public void setCode(ErrorCode code)
	{
		this.code = code;
	}

	public AdditionalErrorData getAdditional()
	{
		return additional;
	}

	public void setAdditional(AdditionalErrorData additional)
	{
		this.additional = additional;
	}
}
