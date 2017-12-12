package com.qaprosoft.zafira.models.db;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Group extends AbstractEntity
{
	private static final long serialVersionUID = -1122566583572312653L;

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
		ROLE_USER, ROLE_ADMIN
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
