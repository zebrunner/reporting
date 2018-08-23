package com.qaprosoft.zafira.models.dto.application.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.models.db.AbstractEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

public class FilterType extends AbstractEntity
{
	private static final long serialVersionUID = -2497558955789794119L;
			
	@NotNull(message = "Name required")
	private String name;
	private String description;
	@Valid
	private Subject subject;
	private Long userId;
	private boolean publicAccess;

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

	public Subject getSubject()
	{
		return subject;
	}

	public void setSubject(Subject subject)
	{
		this.subject = subject;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public boolean isPublicAccess()
	{
		return publicAccess;
	}

	public void setPublicAccess(boolean publicAccess)
	{
		this.publicAccess = publicAccess;
	}

	public void setSubjectFromString(String subject) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		this.subject = mapper.readValue(subject, Subject.class);
	}
}
