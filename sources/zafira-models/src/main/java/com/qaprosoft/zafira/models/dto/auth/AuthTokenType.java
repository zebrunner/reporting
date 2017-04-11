package com.qaprosoft.zafira.models.dto.auth;

import java.io.Serializable;

public class AuthTokenType implements Serializable
{
	private static final long serialVersionUID = -586102250911687530L;
	
	private String token;
	
	public AuthTokenType(String token)
	{
		this.token = token;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}
}
