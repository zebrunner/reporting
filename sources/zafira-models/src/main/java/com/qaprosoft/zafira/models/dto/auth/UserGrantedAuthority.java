package com.qaprosoft.zafira.models.dto.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Set;

public class UserGrantedAuthority implements GrantedAuthority
{

	private static final long serialVersionUID = 6435350258602099850L;

	private final String role;
	private final Set<String> permissions;

	public UserGrantedAuthority(String role, Set<String> permissions)
	{
		this.role = role;
		this.permissions = permissions;
	}

	@Override
	public String getAuthority()
	{
		return role;
	}

	public Set<String> getPermissions()
	{
		return permissions;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof UserGrantedAuthority) {
			return role.equals(((UserGrantedAuthority) obj).role) && ((UserGrantedAuthority) obj).permissions.containsAll(permissions);
		}

		return false;
	}

	public int hashCode() {
		return this.role.hashCode() + Arrays.toString(this.permissions.toArray()).hashCode();
	}
}
