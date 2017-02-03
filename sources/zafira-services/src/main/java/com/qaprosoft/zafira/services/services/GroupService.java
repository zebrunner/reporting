package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.GroupMapper;
import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Transactional(rollbackFor = Exception.class)
    public Group createGroup(Group group) throws ServiceException {
        groupMapper.createGroup(group);
        return group;
    }

    @Transactional(rollbackFor = Exception.class)
    public Group getGroupById(long id) throws ServiceException {
        return groupMapper.getGroupById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Group> getAllGroups() throws ServiceException {
        return groupMapper.getAllGroups();
    }

    @Transactional(rollbackFor = Exception.class)
    public Group updateGroup(Group group) throws ServiceException {
        groupMapper.updateGroup(group);
        return group;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(long id) throws ServiceException {
        groupMapper.deleteGroup(id);
    }
}
