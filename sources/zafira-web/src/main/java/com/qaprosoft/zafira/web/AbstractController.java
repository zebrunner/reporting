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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Permission;
import com.qaprosoft.zafira.models.dto.auth.JwtUserType;
import com.qaprosoft.zafira.models.dto.auth.UserGrantedAuthority;
import com.qaprosoft.zafira.models.push.AbstractPush;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static com.qaprosoft.zafira.models.push.AbstractPush.Type.LAUNCHER;
import static com.qaprosoft.zafira.models.push.AbstractPush.Type.LAUNCHER_RUN;
import static com.qaprosoft.zafira.models.push.AbstractPush.Type.TEST;
import static com.qaprosoft.zafira.models.push.AbstractPush.Type.TEST_RUN;
import static com.qaprosoft.zafira.models.push.AbstractPush.Type.TEST_RUN_STATISTICS;

public abstract class AbstractController {

    protected String getStatisticsWebsocketPath() {
        return buildWebSocketPath(TEST_RUN_STATISTICS, TenancyContext.getTenantName());
    }

    protected String getTestRunsWebsocketPath() {
        return buildWebSocketPath(TEST_RUN, TenancyContext.getTenantName());
    }

    protected String getTestsWebsocketPath(Long testRunId) {
        return buildWebSocketPath(TEST, TenancyContext.getTenantName(), testRunId);
    }

    protected String getLaunchersWebsocketPath() {
        return buildWebSocketPath(LAUNCHER, TenancyContext.getTenantName());
    }

    protected String getLauncherRunsWebsocketPath() {
        return buildWebSocketPath(LAUNCHER_RUN, TenancyContext.getTenantName());
    }

    private String buildWebSocketPath(AbstractPush.Type type, Object... parameters) {
        return type.buildWebsocketPath(parameters);
    }

    private JwtUserType getPrincipal() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user instanceof JwtUserType ? (JwtUserType) user : null;
    }

    protected Long getPrincipalId() {
        JwtUserType user = getPrincipal();
        return user != null ? user.getId() : 0;
    }

    protected String getPrincipalName() {
        UserDetails user = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return user != null ? user.getUsername() : "";
    }

    protected boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    protected boolean hasPermission(Permission.Name name) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .flatMap(grantedAuthority -> ((UserGrantedAuthority) grantedAuthority).getPermissions().stream())
                .anyMatch(permission -> permission.equalsIgnoreCase(name.name()));
    }

    protected void checkCurrentUserAccess(long userId) throws ForbiddenOperationException {
        if (!isAdmin() && userId != getPrincipalId()) {
            throw new ForbiddenOperationException();
        }
    }

}
