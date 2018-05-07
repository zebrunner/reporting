package com.qaprosoft.zafira.ws.security.expressions;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public interface IUserPermissionEvaluator extends PermissionEvaluator
{

	boolean hasAnyPermission(Authentication authentication, String... permissions);

	boolean isOwner(Authentication authentication, Object targetDomainObject);
}
