/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Group extends AbstractEntity {
    private static final long serialVersionUID = -1122566583572312653L;

    private String name;
    private Role role;
    private Boolean invitable;
    private List<User> users;
    private Set<Permission> permissions;

    public Group(String name, Role role, Set<Permission> permissions) {
        this.name = name;
        this.role = role;
        this.permissions = permissions;
    }

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }

    @JsonIgnore
    public Set<String> getPermissionNames() {
        return this.permissions.stream()
                               .map(permission -> permission.getName().name())
                               .collect(Collectors.toSet());
    }

    @JsonIgnore
    public boolean hasPermissions() {
        return this.permissions != null && this.permissions.size() > 0;
    }
}
