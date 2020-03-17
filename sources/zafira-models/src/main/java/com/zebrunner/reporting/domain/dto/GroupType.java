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
package com.zebrunner.reporting.domain.dto;

import com.zebrunner.reporting.domain.db.Group;
import com.zebrunner.reporting.domain.db.Permission;
import com.zebrunner.reporting.domain.db.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class GroupType extends AbstractType {

    private static final long serialVersionUID = 4257992439033566293L;

    @NotEmpty(message = "Name required")
    private String name;

    @NotEmpty(message = "Role required")
    private Group.Role role;
    private Boolean invitable;
    private List<User> users;
    private Set<Permission> permissions;

}
