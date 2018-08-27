package com.qaprosoft.zafira.models.dto.management;

import com.qaprosoft.zafira.models.db.application.Group;
import com.qaprosoft.zafira.models.db.application.Permission;
import com.qaprosoft.zafira.models.db.application.User;
import com.qaprosoft.zafira.models.dto.AbstractType;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

public class GroupType extends AbstractType {

    private static final long serialVersionUID = 4257992439033566293L;

    @NotEmpty(message = "Name required")
    private String name;

    @NotEmpty(message = "Role required")
    private Group.Role role;
    private List<User> users;
    private Set<Permission> permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Group.Role getRole() {
        return role;
    }

    public void setRole(Group.Role role) {
        this.role = role;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
