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
package com.qaprosoft.zafira.models.dto;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Permission;
import com.qaprosoft.zafira.models.db.User;
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
