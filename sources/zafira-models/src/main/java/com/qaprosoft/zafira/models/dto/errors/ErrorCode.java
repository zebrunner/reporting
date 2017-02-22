package com.qaprosoft.zafira.models.dto.errors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qaprosoft.zafira.models.dto.errors.ErrorCodeSerializer;

@JsonSerialize(using = ErrorCodeSerializer.class)
public enum ErrorCode
{
	JOB_NOT_FOUND(1000),
	INVALID_TEST_RUN(1001),
	TEST_RUN_NOT_FOUND(1002),
	TEST_NOT_FOUND(1003),
	TEST_RUN_NOT_REBUILT(1004);

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
