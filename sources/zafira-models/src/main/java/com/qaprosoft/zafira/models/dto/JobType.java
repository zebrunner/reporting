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
package com.qaprosoft.zafira.models.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class JobType extends AbstractType
{
	private static final long serialVersionUID = 4123576956700125643L;
	@NotNull
	private String name;
	@NotNull
	private String jobURL;
	@NotNull
	private String jenkinsHost;
	@NotNull
	private Long userId;

	public JobType() {
		
	}
	
	public JobType(String name, String jobURL, String jenkinsHost, Long userId)
	{
		this.name = name;
		this.jobURL = jobURL;
		this.jenkinsHost = jenkinsHost;
		this.userId = userId;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getJobURL()
	{
		return jobURL;
	}

	public void setJobURL(String jobURL)
	{
		this.jobURL = jobURL;
	}

	public String getJenkinsHost()
	{
		return jenkinsHost;
	}

	public void setJenkinsHost(String jenkinsHost)
	{
		this.jenkinsHost = jenkinsHost;
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
