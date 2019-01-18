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
package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Job extends AbstractEntity
{
	private static final long serialVersionUID = -7136622077881406856L;

	private String name;
	private String jobURL;
	private String jenkinsHost;
	private User user = new User();

	public Job()
	{
	}

	public Job(String name, String jobURL) {
		this.name = name;
		this.jobURL = jobURL;
	}

	public Job(String name, String jobURL, String jenkinsHost, User user)
	{
		this.name = name;
		this.jobURL = jobURL;
		this.jenkinsHost = jenkinsHost;
		this.user = user;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
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
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof Job && this.hashCode() == ((Job) obj).hashCode());
	}
	
	@Override
	public int hashCode()
	{
		return (jobURL + user.getUsername()).hashCode();
	}
}
