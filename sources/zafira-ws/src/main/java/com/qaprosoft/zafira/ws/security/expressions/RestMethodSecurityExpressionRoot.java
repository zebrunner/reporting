package com.qaprosoft.zafira.ws.security.expressions;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Add possibility to use hasPermission('expression') and hasAnyPermission('expressions') method expression (by default is not exist)
 * @author Bogdan Rutskov
 */
public class RestMethodSecurityExpressionRoot extends SecurityExpressionRoot
		implements MethodSecurityExpressionOperations
{

	private UserPermissionEvaluator permissionEvaluator;

	public RestMethodSecurityExpressionRoot(Authentication authentication, UserPermissionEvaluator permissionEvaluator)
	{
		super(authentication);
		this.permissionEvaluator = permissionEvaluator;
	}

	public boolean hasPermission(String permission)
	{
		return permissionEvaluator.hasPermission(super.authentication, null, permission);
	}

	public boolean hasAnyPermission(String... permissions)
	{
		return permissionEvaluator.hasAnyPermission(super.authentication, permissions);
	}

	@Override
	public void setFilterObject(Object filterObject)
	{
	}

	@Override
	public Object getFilterObject()
	{
		return null;
	}

	@Override
	public void setReturnObject(Object returnObject)
	{

	}

	@Override
	public Object getReturnObject()
	{
		return null;
	}

	@Override
	public Object getThis()
	{
		return null;
	}
}
