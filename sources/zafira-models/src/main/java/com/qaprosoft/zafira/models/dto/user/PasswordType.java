package com.qaprosoft.zafira.models.dto.user;

import java.io.Serializable;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PasswordType implements Serializable
{
	private static final long serialVersionUID = 8483235107118081307L;
	
	@NotEmpty(message = "Password required")
	@Size(min = 5, message = "Too short password")
	private String password;
	
	@NotEmpty(message = "Password confirmation required")
	private String confirmPassword;

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getConfirmPassword()
	{
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword)
	{
		this.confirmPassword = confirmPassword;
	}

	@AssertTrue(message = "Password confirmation not matching")
	public boolean isConfirmationValid()
	{
		return password != null && password.equals(confirmPassword);
	}
}
