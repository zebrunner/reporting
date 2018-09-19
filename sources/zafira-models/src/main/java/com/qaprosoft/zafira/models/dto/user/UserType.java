/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.models.dto.user;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qaprosoft.zafira.models.db.Permission;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.UserPreference;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.Group.Role;
import com.qaprosoft.zafira.models.dto.AbstractType;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

import javax.validation.constraints.AssertTrue;

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
	private User.Source source;
	private User.Status status;

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

	public User.Source getSource() {
		return source;
	}

	public void setSource(User.Source source) {
		this.source = source;
	}

	public User.Status getStatus() {
		return status;
	}

	public void setStatus(User.Status status) {
		this.status = status;
	}

	@AssertTrue(message = "Email confirmation not matching")
	@JsonIgnore
	public boolean isEmailConfirmationValid() {
		return this.email == null || new EmailValidator().isValid(this.email, null);
	}
}