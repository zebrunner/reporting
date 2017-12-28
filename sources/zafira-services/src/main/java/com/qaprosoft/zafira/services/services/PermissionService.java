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
package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.PermissionMapper;
import com.qaprosoft.zafira.models.db.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.qaprosoft.zafira.models.db.Permission.Name;

@Service
public class PermissionService
{

	@Autowired
	private PermissionMapper permissionMapper;

	@Transactional(rollbackFor = Exception.class)
	public Permission createPermission(Permission permission)
	{
		permissionMapper.createPermission(permission);
		return permission;
	}

	@Transactional(readOnly = true)
	public Permission getPermissionById(Long id)
	{
		return permissionMapper.getPermissionById(id);
	}

	@Transactional(readOnly = true)
	public Permission getPermissionByName(Name name)
	{
		return permissionMapper.getPermissionByName(name);
	}

	@Transactional(readOnly = true)
	public List<Permission> getAllPermissions()
	{
		return permissionMapper.getAllPermissions();
	}

	@Transactional(rollbackFor = Exception.class)
	public Permission updatePermission(Permission permission)
	{
		permissionMapper.updatePermission(permission);
		return permission;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deletePermissionById(Long id)
	{
		permissionMapper.deletePermissionById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deletePermissionByName(Name name)
	{
		permissionMapper.getPermissionByName(name);
	}
}
