package com.qaprosoft.zafira.ws.dto.errors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qaprosoft.zafira.ws.util.ErrorCodeSerializer;

@JsonSerialize(using = ErrorCodeSerializer.class)
public enum ErrorCode
{
	JOB_NOT_FOUND(1000),
	INVALID_TEST_RUN(1001);

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
