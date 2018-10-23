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
package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.models.dto.filter.Subject;

public class Filter extends AbstractEntity
{
	private static final long serialVersionUID = 5052981349343947449L;
	
	private String name;
	private String description;
	private String subject;
	private boolean publicAccess;
	private Long userId;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public boolean isPublicAccess()
	{
		return publicAccess;
	}

	public void setPublicAccess(boolean publicAccess)
	{
		this.publicAccess = publicAccess;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public void setSubjectFromObject(Subject subject) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		this.subject = mapper.writeValueAsString(subject);
	}
}
