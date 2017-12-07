package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;
import java.util.Set;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Group.Role;
import com.qaprosoft.zafira.models.db.Permission;
import org.apache.ibatis.annotations.Param;

public interface GroupMapper
{
	void createGroup(Group group);

	void addPermissionsToGroup(@Param("groupId") Long groupId, @Param("permissions") Set<Permission> permissions);

	Group getGroupById(long id);

	List<Group> getAllGroups();
	
	Group getPrimaryGroupByRole(Role role);

	void updateGroup(Group group);

	void deleteGroup(long id);

	void deletePermissionFromGroup(@Param("groupId") Long groupId, @Param("permissionId") Long permissionId);

	Integer getGroupsCount();
}
