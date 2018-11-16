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
package com.qaprosoft.zafira.ws.security.expressions;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.security.core.Authentication;

import com.qaprosoft.zafira.models.dto.auth.JwtUserType;
import com.qaprosoft.zafira.models.dto.auth.UserGrantedAuthority;

/**
 * Checks user permissions
 * @author Bogdan Rutskov
 */
public class UserPermissionEvaluator implements IUserPermissionEvaluator
{
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
	{
		if(authentication != null && permission instanceof String)
		{
			boolean hasPermission = true;
			if(targetDomainObject != null)
			{
				hasPermission = (Boolean) targetDomainObject;
			}
			return checkAuthority(authentication, p -> p.equalsIgnoreCase(permission.toString())) && hasPermission;
		}
		return false;
	}

	@Override
	public boolean hasAnyPermission(Authentication authentication, String... permissions)
	{
		if(authentication != null)
		{
			return checkAuthority(authentication, p -> Arrays.asList(permissions).contains(p));
		}
		return false;
	}

	@Override
	public boolean isOwner(Authentication authentication, Object targetDomainObject)
	{
		if(authentication != null && targetDomainObject instanceof Long)
		{
			return ((JwtUserType)authentication.getPrincipal()).getId() == (Long) targetDomainObject;
		}
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission)
	{
		return hasPermission(authentication, targetType, permission);
	}

	private boolean checkAuthority(Authentication authentication, EvaluatorComparator evaluatorComparator)
	{
		return authentication.getAuthorities().stream()
				.flatMap(grantedAuthority -> ((UserGrantedAuthority)grantedAuthority).getPermissions().stream())
				.anyMatch(evaluatorComparator::contains);
	}
}
