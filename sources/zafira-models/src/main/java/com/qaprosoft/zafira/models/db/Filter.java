package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.models.dto.filter.Subject;

public class Filter extends AbstractEntity
{

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
