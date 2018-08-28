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

public class JobView extends AbstractEntity
{
	private static final long serialVersionUID = -3868077369004418496L;

	private Long viewId;
	private Job job = new Job();
	private String env;
	private Integer position;
	private Integer size;

	public Job getJob()
	{
		return job;
	}

	public void setJob(Job job)
	{
		this.job = job;
	}

	public Long getViewId()
	{
		return viewId;
	}

	public void setViewId(Long viewId)
	{
		this.viewId = viewId;
	}

	public String getEnv()
	{
		return env;
	}

	public void setEnv(String env)
	{
		this.env = env;
	}

	public Integer getPosition()
	{
		return position;
	}

	public void setPosition(Integer position)
	{
		this.position = position;
	}

	public Integer getSize()
	{
		return size;
	}

	public void setSize(Integer size)
	{
		this.size = size;
	}
}