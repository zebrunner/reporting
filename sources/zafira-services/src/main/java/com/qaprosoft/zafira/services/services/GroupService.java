package com.qaprosoft.zafira.services.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.qaprosoft.zafira.models.db.Permission;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.GroupMapper;
import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Group.Role;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class GroupService
{
	private static final Logger LOGGER = Logger.getLogger(GroupService.class);

	@Autowired
	private GroupMapper groupMapper;

	@CachePut(value = "groups", key = "#group.id")
	@Transactional(rollbackFor = Exception.class)
	public Group createGroup(Group group) throws ServiceException
	{
		groupMapper.createGroup(group);
		addPermissionsToGroup(group);
		return group;
	}

	@Transactional(rollbackFor = Exception.class)
	public Group addPermissionsToGroup(Group group) throws ServiceException
	{
		Group dbGroup = groupMapper.getGroupById(group.getId());

		Set<Permission> intersection = new HashSet<>(group.getPermissions());
		intersection.retainAll(dbGroup.getPermissions());

		dbGroup.getPermissions().removeAll(intersection);
		group.getPermissions().removeAll(intersection);
		dbGroup.getPermissions().forEach(permission -> {
			try
			{
				deletePermissionFromGroup(group.getId(), permission.getId());
			} catch (ServiceException e)
			{
				LOGGER.error(e.getMessage());
			}
		});
		groupMapper.addPermissionsToGroup(group.getId(), group.getPermissions());
		group.getPermissions().addAll(intersection);
		return group;
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "groups", key = "#id")
	public Group getGroupById(long id)
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

	@Transactional(readOnly = true)
	public Integer getGroupsCount() throws ServiceException
	{
		return groupMapper.getGroupsCount();
	}

	@CachePut(value = "groups", key = "#group.id")
	@Transactional(rollbackFor = Exception.class)
	public Group updateGroup(Group group) throws ServiceException
	{
		groupMapper.updateGroup(group);
		addPermissionsToGroup(group);
		return group;
	}

	@CacheEvict(value = "groups", key = "#id")
	@Transactional(rollbackFor = Exception.class)
	public void deleteGroup(long id) throws ServiceException
	{
		groupMapper.deleteGroup(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deletePermissionFromGroup(long groupId, long permissionId) throws ServiceException
	{
		groupMapper.deletePermissionFromGroup(groupId, permissionId);
	}
}