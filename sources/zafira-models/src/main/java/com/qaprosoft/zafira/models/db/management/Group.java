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
package com.qaprosoft.zafira.models.db.management;

import com.qaprosoft.zafira.models.db.AbstractEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Group extends AbstractEntity
{
	private static final long serialVersionUID = 3501204058816003724L;

	private String name;
	private Role role;
	private List<User> users;
	private Set<Permission> permissions;

	public Group()
	{
	}

	public Group(String name, Role role, Set<Permission> permissions)
	{
		this.name = name;
		this.role = role;
		this.permissions = permissions;
	}

	public enum Role
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public List<User> getUsers()
	{
		return users;
	}

	public void setUsers(List<User> users)
	{
		this.users = users;
	}

	public Set<Permission> getPermissions()
	{
		return permissions;
	}

	public Set<String> getPermissionNames()
	{
		return this.permissions.stream().map(permission -> permission.getName().name())
				.collect(Collectors.toSet());
	}

	public void setPermissions(Set<Permission> permissions)
	{
		this.permissions = permissions;
	}

	public boolean hasPermissions()
	{
		return this.permissions != null && this.permissions.size() > 0;
	}
}
