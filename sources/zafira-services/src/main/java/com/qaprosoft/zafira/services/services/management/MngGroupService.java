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
package com.qaprosoft.zafira.services.services.management;

import com.qaprosoft.zafira.dbaccess.dao.mysql.management.MngGroupMapper;
import com.qaprosoft.zafira.models.db.management.Group;
import com.qaprosoft.zafira.models.db.management.Permission;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MngGroupService {
    private static final Logger LOGGER = Logger.getLogger(MngGroupService.class);

    @Autowired
    private MngGroupMapper mngGroupMapper;

    @Transactional(rollbackFor = Exception.class)
    public Group createGroup(Group group) throws ServiceException {
        mngGroupMapper.createGroup(group);
        addPermissionsToGroup(group);
        return group;
    }

    @Transactional(rollbackFor = Exception.class)
    public Group addPermissionsToGroup(Group group) throws ServiceException {
        Group dbGroup = mngGroupMapper.getGroupById(group.getId());

        Set<Permission> intersection = new HashSet<>(group.getPermissions());
        intersection.retainAll(dbGroup.getPermissions());

        dbGroup.getPermissions().removeAll(intersection);
        group.getPermissions().removeAll(intersection);
        dbGroup.getPermissions().forEach(permission -> {
            try {
                deletePermissionFromGroup(group.getId(), permission.getId());
            } catch (ServiceException e) {
                LOGGER.error(e.getMessage());
            }
        });
        mngGroupMapper.addPermissionsToGroup(group.getId(), group.getPermissions());
        group.getPermissions().addAll(intersection);
        return group;
    }

    @Transactional(readOnly = true)
    public Group getGroupById(long id) {
        return mngGroupMapper.getGroupById(id);
    }

    @Transactional(readOnly = true)
    public Group getPrimaryGroupByRole(Group.Role role) throws ServiceException {
        return mngGroupMapper.getPrimaryGroupByRole(role);
    }

    @Transactional(readOnly = true)
    public List<Group> getAllGroups() throws ServiceException {
        List<Group> groupList = mngGroupMapper.getAllGroups();
        for (Group group : groupList) {
            Collections.sort(group.getUsers());
        }
        return groupList;
    }

    @Transactional(readOnly = true)
    public Integer getGroupsCount() throws ServiceException {
        return mngGroupMapper.getGroupsCount();
    }

    @Transactional(rollbackFor = Exception.class)
    public Group updateGroup(Group group) throws ServiceException {
        mngGroupMapper.updateGroup(group);
        addPermissionsToGroup(group);
        return group;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(long id) throws ServiceException {
        mngGroupMapper.deleteGroup(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePermissionFromGroup(long groupId, long permissionId) throws ServiceException {
        mngGroupMapper.deletePermissionFromGroup(groupId, permissionId);
    }
}