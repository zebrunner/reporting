package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Group.Role;

public interface GroupMapper
{
	void createGroup(Group group);

	Group getGroupById(long id);

	List<Group> getAllGroups();
	
	Group getPrimaryGroupByRole(Role role);

	void updateGroup(Group group);

	void deleteGroup(long id);

	Integer getGroupsCount();
}
