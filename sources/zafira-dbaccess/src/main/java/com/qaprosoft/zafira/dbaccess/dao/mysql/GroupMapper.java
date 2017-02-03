package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.Group;
import java.util.List;

public interface GroupMapper {

    void createGroup(Group group);
    Group getGroupById(long id);
    List<Group> getAllGroups();
    void updateGroup(Group group);
    void deleteGroup(long id);
}
