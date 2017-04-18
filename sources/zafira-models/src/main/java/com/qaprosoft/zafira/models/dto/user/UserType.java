package com.qaprosoft.zafira.models.dto.user;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.Group.Role;
import com.qaprosoft.zafira.models.dto.AbstractType;

@JsonInclude(Include.NON_NULL)
public class UserType extends AbstractType
{
	private static final long serialVersionUID = -6663692781158665080L;
	@NotNull
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private List<Role> roles = new ArrayList<>();

	public UserType() 
	{
	}
	
	public UserType(String username, String email, String firstName, String lastName)
	{
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public List<Role> getRoles()
	{
		return roles;
	}

	public void setRoles(List<Role> roles)
	{
		this.roles = roles;
	}
}
