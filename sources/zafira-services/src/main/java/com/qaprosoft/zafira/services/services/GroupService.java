package com.qaprosoft.zafira.services.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.GroupMapper;
import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Group.Role;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class GroupService
{
	@Autowired
	private GroupMapper groupMapper;

	@Transactional(rollbackFor = Exception.class)
	public Group createGroup(Group group) throws ServiceException
	{
		groupMapper.createGroup(group);
		return group;
	}

	@Transactional(readOnly = true)
	public Group getGroupById(long id) throws ServiceException
	{
		return groupMapper.getGroupById(id);
	}

	@Transactional(readOnly = true)
	public Group getPrimaryGroupByRole(Role role) throws ServiceException
	{
		return groupMapper.getPrimaryGroupByRole(role);
	}
	
	@Transactional(readOnly = true)
	public List<Group> getAllGroups() throws ServiceException
	{
		List<Group> groupList = groupMapper.getAllGroups();
		for (Group group : groupList)
		{
			Collections.sort(group.getUsers());
		}
		return groupList;
	}

	@Transactional(readOnly=true)
	public Integer getGroupsCount() throws ServiceException
	{
		return groupMapper.getGroupsCount();
	}

	@Transactional(rollbackFor = Exception.class)
	public Group updateGroup(Group group) throws ServiceException
	{
		groupMapper.updateGroup(group);
		return group;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteGroup(long id) throws ServiceException
	{
		groupMapper.deleteGroup(id);
	}
}