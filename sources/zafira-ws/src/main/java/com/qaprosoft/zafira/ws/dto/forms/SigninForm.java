package com.qaprosoft.zafira.ws.dto.forms;

import org.apache.commons.lang3.StringUtils;

public class SigninForm
{
	private String email;

	private String password;
	
	private boolean signinFailed;
	
	public SigninForm(boolean signinFailed)
	{
		this.signinFailed = signinFailed; 
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = StringUtils.lowerCase(email);
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean isSigninFailed()
	{
		return signinFailed;
	}

	public void setSigninFailed(boolean signinFailed)
	{
		this.signinFailed = signinFailed;
	}
}
