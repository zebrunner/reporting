package com.qaprosoft.zafira.models.dto.auth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AccessTokenType implements Serializable
{
	private static final long serialVersionUID = 2982073032065087590L;
	
	public AccessTokenType()
	{
	}
	
	public AccessTokenType(String token)
	{
		this.token = token;
	}

	private String token;

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}
}
