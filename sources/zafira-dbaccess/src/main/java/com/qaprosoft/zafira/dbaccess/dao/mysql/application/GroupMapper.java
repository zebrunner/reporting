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
package com.qaprosoft.zafira.dbaccess.dao.mysql.application;

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
