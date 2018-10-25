/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.models.dto.monitor;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorCheckType implements Serializable
{
	private static final long serialVersionUID = 3232356935782375373L;

	private Integer actualCode;
	private boolean success;

	public MonitorCheckType(Integer actualCode, boolean success)
	{
		this.actualCode = actualCode;
		this.success = success;
	}

	public Integer getActualCode()
	{
		return actualCode;
	}

	public boolean isSuccess()
	{
		return success;
	}
}
