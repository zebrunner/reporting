package com.qaprosoft.zafira.models.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

import static com.qaprosoft.zafira.models.dto.filter.Operator.*;

public class Criteria
{

	@NotNull(message = "Criteria name required")
	private Name name;
	private List<Operator> operators;
	@NotNull(message = "Operator required")
	private Operator operator;
	private String value;

	public enum Name
	{
		STATUS, TEST_SUITE, JOB_URL, ENV, PLATFORM, DATE, PROJECT
	}

	public Name getName()
	{
		return name;
	}

	public void setName(Name name)
	{
		this.name = name;
	}

	public List<Operator> getOperators()
	{
		return operators;
	}

	public void setOperators(List<Operator> operators)
	{
		this.operators = operators;
	}

	public Operator getOperator()
	{
		return operator;
	}

	public void setOperator(Operator operator)
	{
		this.operator = operator;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@JsonIgnore
	@AssertTrue(message = "Incorrect value")
	public boolean isValueNull()
	{
		return Arrays.asList(LAST_SEVEN_DAYS, LAST_FOURTEEN_DAYS, LAST_THIRTY_DAYS).contains(this.operator)
				? value == null : value != null;
	}
}
