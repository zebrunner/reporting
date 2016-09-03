package com.qaprosoft.zafira.grid.queue.models;

import java.util.ArrayList;
import java.util.List;

public class GridRequest
{
	private String gridSessionId;
	private String testId;
	private String serial;
	private List<String> tags = new ArrayList<>();
	private List<String> models = new ArrayList<>();
	private Operation operation;
	
	public GridRequest() 
	{
	}
	
	public GridRequest(String serial) 
	{
		this.serial = serial;
	}

	public String getGridSessionId() 
	{
		return gridSessionId;
	}

	public void setGridSessionId(String gridSessionId) 
	{
		this.gridSessionId = gridSessionId;
	}

	public String getTestId()
	{
		return testId;
	}

	public void setTestId(String testId)
	{
		this.testId = testId;
	}

	public List<String> getModels()
	{
		return models;
	}

	public void setModels(List<String> models)
	{
		this.models = models;
	}

	public List<String> getTags()
	{
		return tags;
	}

	public void setTags(List<String> tags)
	{
		this.tags = tags;
	}
	
	public Operation getOperation()
	{
		return operation;
	}

	public void setOperation(Operation operation)
	{
		this.operation = operation;
	}

	public String getSerial()
	{
		return serial;
	}

	public void setSerial(String serial)
	{
		this.serial = serial;
	}

	public enum Operation {CONNECT, DISCONNECT};
}