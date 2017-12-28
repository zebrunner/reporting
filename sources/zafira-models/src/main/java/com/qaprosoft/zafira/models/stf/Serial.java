/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.models.stf;

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
