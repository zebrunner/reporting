package com.qaprosoft.zafira.dbaccess.model.stf;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "serial"
})
public class Serial
{
	@JsonProperty("serial")
	private String serial;
	
	@JsonProperty("timeout")
	private long timeout;
	
	public Serial(String serial, long timeout)
	{
		this.serial = serial;
		this.timeout = timeout;
	}

	@JsonProperty("serial")
	public String getSerial()
	{
		return serial;
	}

	@JsonProperty("serial")
	public void setSerial(String serial)
	{
		this.serial = serial;
	}

	@JsonProperty("serial")
	public long getTimeout()
	{
		return timeout;
	}

	@JsonProperty("serial")
	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	}
}
