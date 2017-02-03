package com.qaprosoft.zafira.models.db;

public class Group extends AbstractEntity {

    private static final long serialVersionUID = -1122566583572312653L;

    private String name;
    private Role role;

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
}
