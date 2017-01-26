package com.qaprosoft.zafira.models.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CommentType implements Serializable
{
	private static final long serialVersionUID = -2282390415752224043L;
	private String comment;

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}
}
