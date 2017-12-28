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
