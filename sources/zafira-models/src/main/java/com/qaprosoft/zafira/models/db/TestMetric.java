package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TestMetric extends AbstractEntity
{
	private static final long serialVersionUID = -5494737921559000889L;
	
	private String operation;
	private Long elapsed;
	private Long testId;
	
	public TestMetric()
	{
	}

	public TestMetric(String operation, Long elapsed, Long testId)
	{
		this.operation = operation;
		this.elapsed = elapsed;
		this.testId = testId;
	}

	public String getOperation()
	{
		return operation;
	}

	public void setOperation(String operation)
	{
		this.operation = operation;
	}

	public Long getElapsed()
	{
		return elapsed;
	}

	public void setElapsed(Long elapsed)
	{
		this.elapsed = elapsed;
	}

	public Long getTestId()
	{
		return testId;
	}

	public void setTestId(Long testId)
	{
		this.testId = testId;
	}
}
