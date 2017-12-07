package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.Permission;

import java.util.List;

public interface PermissionMapper
{
	void createPermission(Permission permission);

	Permission getPermissionById(Long id);

	Permission getPermissionByName(Permission.Name name);

	List<Permission> getAllPermissions();

	void updatePermission(Permission permission);

	void deletePermissionById(Long id);

	void deletePermissionByName(Permission.Name name);
}
