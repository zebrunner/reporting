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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueueTestRunParamsType implements Serializable
{
	private static final long serialVersionUID = 5893913105698710480L;

	private String jobName;
	private String branch;
	private String env;
	private String ciRunId;
	private String ciParentUrl;
	private String ciParentBuild;
	private String buildNumber;
	private String project;

	public String getJobName()
	{
		return jobName;
	}

	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}

	public String getBranch()
	{
		return branch;
	}

	public void setBranch(String branch)
	{
		this.branch = branch;
	}

	public String getEnv()
	{
		return env;
	}

	public void setEnv(String env)
	{
		this.env = env;
	}

	public String getCiRunId()
	{
		return ciRunId;
	}

	public void setCiRunId(String ciRunId)
	{
		this.ciRunId = ciRunId;
	}

	public String getCiParentUrl()
	{
		return ciParentUrl;
	}

	public void setCiParentUrl(String ciParentUrl)
	{
		this.ciParentUrl = ciParentUrl;
	}

	public String getCiParentBuild()
	{
		return ciParentBuild;
	}

	public void setCiParentBuild(String ciParentBuild)
	{
		this.ciParentBuild = ciParentBuild;
	}

	public String getBuildNumber()
	{
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber)
	{
		this.buildNumber = buildNumber;
	}

	public String getProject()
	{
		return project;
	}

	public void setProject(String project)
	{
		this.project = project;
	}
}
