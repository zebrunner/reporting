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
public class WorkItem extends AbstractEntity
{
	private static final long serialVersionUID = 5440580857483390564L;

	private String jiraId;
	private String description;
	private boolean blocker;
	private Integer hashCode;
	private Long testCaseId;
	private User user;
	// TODO: think about default type
	private Type type = Type.TASK;

	public enum Type
	{
		TASK, BUG, COMMENT
	}

	public WorkItem()
	{
	}

	public WorkItem(String jiraId)
	{
		this.jiraId = jiraId;
	}

	public WorkItem(String jiraId, Type type)
	{
		this.jiraId = jiraId;
		this.type = type;
	}

	public String getJiraId()
	{
		return jiraId;
	}

	public void setJiraId(String jiraId)
	{
		this.jiraId = jiraId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean isBlocker()
	{
		return blocker;
	}

	public void setBlocker(boolean blocker)
	{
		this.blocker = blocker;
	}

	public Integer getHashCode()
	{
		return hashCode;
	}

	public void setHashCode(Integer hashCode)
	{
		this.hashCode = hashCode;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public Long getTestCaseId()
	{
		return testCaseId;
	}

	public void setTestCaseId(Long testCaseId)
	{
		this.testCaseId = testCaseId;
	}
}
