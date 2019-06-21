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
package com.qaprosoft.zafira.models.dto.auth;

import com.qaprosoft.zafira.models.db.Permission;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.Set;

public class TenantAuth implements Serializable {

    private static final long serialVersionUID = -1075126053459147979L;

    @NotEmpty
    private String tenantName;

    @NotEmpty
    private String token;

    @NotEmpty
    private Set<Permission.Name> permissions;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Set<Permission.Name> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission.Name> permissions) {
        this.permissions = permissions;
    }

}
