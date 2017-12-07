package com.qaprosoft.zafira.ws.security.expressions;

import com.qaprosoft.zafira.models.dto.auth.UserGrantedAuthority;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Checks user permissions
 * @author Bogdan Rutskov
 */
public class UserPermissionEvaluator implements PermissionEvaluator
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

	public boolean hasAnyPermission(Authentication authentication, String... permissions)
	{
		if(authentication != null)
		{
			return checkAuthority(authentication, p -> Arrays.asList(permissions).contains(p));
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
