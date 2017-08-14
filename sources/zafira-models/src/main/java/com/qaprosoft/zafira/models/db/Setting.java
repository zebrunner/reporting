package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Setting extends AbstractEntity
{

	private static final long serialVersionUID = -6809215085336377266L;
	
	private String name;
	private String value;
	private boolean isEncrypted;
	private String tool;


	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public boolean isEncrypted() {
		return isEncrypted;
	}

	public void setEncrypted(boolean encrypted) {
		isEncrypted = encrypted;
	}

	public String getTool() {
		return tool;
	}

	public void setTool(String tool) {
		this.tool = tool;
	}
}
