package com.qaprosoft.zafira.models.db.management;

import com.qaprosoft.zafira.models.db.AbstractEntity;

import java.util.*;
import java.util.stream.Collectors;

public class User extends AbstractEntity implements Comparable<User> {

    private static final long serialVersionUID = -7000082539080635972L;

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String photoURL;
    private List<Group> groups = new ArrayList<>();
    private Date lastLogin;
    private String tenant;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public List<Group.Role> getRoles() {
        return groups.stream().map(Group::getRole).distinct().collect(Collectors.toList());
    }

    public Set<Permission> getPermissions() {
        return this.groups.stream().flatMap(group -> group.getPermissions().stream())
                .collect(Collectors.toSet());
    }

    public List<Group> getGrantedGroups() {
        this.groups.forEach(group -> {
            group.setUsers(null);
            group.setId(null);
            group.setCreatedAt(null);
            group.setModifiedAt(null);
            group.getPermissions().forEach(permission -> permission.setId(null));
        });
        return this.groups;
    }

    @Override
    public int compareTo(User user) {
        return username.compareTo(user.getUsername());
    }
}
