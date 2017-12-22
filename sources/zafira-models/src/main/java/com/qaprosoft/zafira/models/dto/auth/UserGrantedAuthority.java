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
