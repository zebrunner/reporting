package com.qaprosoft.zafira.models.dto.user;

import java.util.*;

import com.qaprosoft.zafira.models.db.Permission;
import com.qaprosoft.zafira.models.db.UserPreference;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.Group.Role;
import com.qaprosoft.zafira.models.dto.AbstractType;

@JsonInclude(Include.NON_NULL)
public class UserType extends AbstractType
{
	private static final long serialVersionUID = -6663692781158665080L;
	
	@NotEmpty(message = "Username required")
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String photoURL;
	private List<Role> roles = new ArrayList<>();
	private Set<Permission> permissions = new HashSet<>();
	private List<UserPreference> preferences = new ArrayList<>();
	private Date lastLogin;

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

	public String getPhotoURL()
	{
		return photoURL;
	}

	public void setPhotoURL(String photoURL)
	{
		this.photoURL = photoURL;
	}

	public List<Role> getRoles()
	{
		return roles;
	}

	public void setRoles(List<Role> roles)
	{
		this.roles = roles;
	}

	public List<UserPreference> getPreferences() {
		return preferences;
	}

	public void setPreferences(List<UserPreference> preferences) {
		this.preferences = preferences;
	}

	public Set<Permission> getPermissions()
	{
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions)
	{
		this.permissions = permissions;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public Date getLastLogin()
	{
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin)
	{
		this.lastLogin = lastLogin;
	}
}