package com.qaprosoft.zafira.models.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL) 
public class TestSuiteType extends AbstractType
{
	@NotNull
	private String name;
	@NotNull
	private String fileName;
	private String description;
	@NotNull
	private Long userId;

	public TestSuiteType(String name, String fileName, Long userId)
	{
		this.name = name;
		this.userId = userId;
		this.fileName = fileName;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}
}
