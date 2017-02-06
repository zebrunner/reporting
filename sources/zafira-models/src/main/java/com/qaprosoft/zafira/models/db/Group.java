package com.qaprosoft.zafira.models.db;

import java.util.List;

public class Group extends AbstractEntity {

    private static final long serialVersionUID = -1122566583572312653L;

    private String name;
    private Role role;
    private List<User> userList;

    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
