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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Invitation;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.models.dto.auth.EmailType;
import com.qaprosoft.zafira.models.dto.auth.TenantAuth;
import com.qaprosoft.zafira.models.dto.auth.TenantType;
import com.qaprosoft.zafira.models.dto.user.PasswordChangingType;
import com.qaprosoft.zafira.service.exception.ForbiddenOperationException;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.InvalidCredentialsException;
import com.qaprosoft.zafira.service.exception.UserNotFoundException;
import com.qaprosoft.zafira.service.integration.tool.impl.AccessManagementService;
import com.qaprosoft.zafira.service.util.URLResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.USER_CAN_NOT_BE_CREATED;

@Service
public class AuthService {

    private static final String ERR_MSG_USER_ALREADY_EXISTS = "User with such username already exists";

    private final URLResolver urlResolver;
    private final JWTService jwtService;
    private final UserService userService;
    private final GroupService groupService;
    private final ForgotPasswordService forgotPasswordService;
    private final InvitationService invitationService;
    private final AccessManagementService accessManagementService;
    private final String adminUsername;
    private final boolean isMultitenant;
    private final boolean useArtifactsProxy;

    public AuthService(
            URLResolver urlResolver,
            JWTService jwtService,
            UserService userService,
            GroupService groupService,
            ForgotPasswordService forgotPasswordService,
            InvitationService invitationService,
            AccessManagementService accessManagementService,
            @Value("${zafira.admin.username}") String adminUsername,
            @Value("${zafira.multitenant}") boolean isMultitenant,
            @Value("${zafira.use-artifact-proxy:false}") boolean useArtifactsProxy) {
        this.urlResolver = urlResolver;
        this.jwtService = jwtService;
        this.userService = userService;
        this.groupService = groupService;
        this.forgotPasswordService = forgotPasswordService;
        this.invitationService = invitationService;
        this.accessManagementService = accessManagementService;
        this.adminUsername = adminUsername;
        this.isMultitenant = isMultitenant;
        this.useArtifactsProxy = useArtifactsProxy;
    }

    public TenantType getTenantInfo() {
        TenantType tenantType = new TenantType(TenancyContext.getTenantName(), urlResolver.getServiceURL(), useArtifactsProxy);
        tenantType.setMultitenant(isMultitenant);
        return tenantType;
    }

    public boolean checkTenantPermissions(TenantAuth tenantAuth) {
        return jwtService.checkPermissions(tenantAuth.getTenantName(), tenantAuth.getToken(), tenantAuth.getPermissions());
    }

    @Transactional(rollbackFor = Exception.class)
    public AuthTokenType buildAuthToken(String username) {
        User user = getUser(username);
        final String tenant = TenancyContext.getTenantName();

        AuthTokenType authToken = new AuthTokenType("Bearer", jwtService.generateAuthToken(user, tenant),
                jwtService.generateRefreshToken(user, tenant), jwtService.getExpiration(), tenant);

        userService.updateLastLoginDate(user.getId());
        return authToken;
    }

    @Transactional(rollbackFor = Exception.class)
    public void registerUser(User user, String invitationToken) {
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            throw new IllegalOperationException(USER_CAN_NOT_BE_CREATED, ERR_MSG_USER_ALREADY_EXISTS);
        }
        Invitation invitation = invitationService.getInvitationByToken(invitationToken);
        if (invitation == null) {
            throw new ForbiddenOperationException();
        }
        if (invitation.getSource().equals(User.Source.LDAP)) {
            boolean ldapEnabled = accessManagementService.isEnabledAndConnected(null);
            boolean userExists = accessManagementService.isUserExists(user.getUsername());
            if (ldapEnabled && !userExists) {
                throw new ForbiddenOperationException();
            }
        }
        invitation.setStatus(Invitation.Status.ACCEPTED);
        invitationService.updateInvitation(invitation);

        Group group = groupService.getGroupById(invitation.getGroupId());
        user.setSource(invitation.getSource());
        userService.createOrUpdateUser(user, group);
    }

    @Transactional(rollbackFor = Exception.class)
    public AuthTokenType refreshAuthToken(String token) {
        AuthTokenType authToken;
        try {
            User jwtUser = jwtService.parseRefreshToken(token);

            User user = userService.getUserById(jwtUser.getId());
            if (user == null) {
                throw new UserNotFoundException();
            }

            if (User.Status.INACTIVE.equals(user.getStatus())) {
                throw new InvalidCredentialsException("Unable to refresh auth token");
            }

            if (!TenancyContext.getTenantName().equals(jwtUser.getTenant())) {
                throw new InvalidCredentialsException("Invalid tenant");
            }

            // TODO: Do not verify password for demo user as far as it breaks demo JWT token
            if (!StringUtils.equals(adminUsername, user.getUsername()) && !StringUtils.equals(user.getPassword(), jwtUser.getPassword())) {
                throw new InvalidCredentialsException();
            }

            final String tenant = TenancyContext.getTenantName();

            authToken = new AuthTokenType("Bearer", jwtService.generateAuthToken(user, tenant),
                    jwtService.generateRefreshToken(user, tenant), jwtService.getExpiration(),
                    TenancyContext.getTenantName());

            userService.updateLastLoginDate(user.getId());
        } catch (Exception e) {
            throw new ForbiddenOperationException(e);
        }
        return authToken;
    }

    @Transactional(readOnly = true)
    public void sendForgotPasswordEmail(EmailType emailType) {
        User user = userService.getUserByEmail(emailType.getEmail());
        if (user != null) {
            forgotPasswordService.sendForgotPasswordEmail(emailType, user);
        }
    }

    @Transactional(readOnly = true)
    public User getUserByResetToken(String token) {
        User user = userService.getUserByResetToken(token);
        if (user == null || !User.Source.INTERNAL.equals(user.getSource())) {
            throw new ForbiddenOperationException();
        }
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public User resetPassword(String token, String password) {
        User user = userService.getUserByResetToken(token);
        if (user != null && User.Source.INTERNAL.equals(user.getSource())) {
            PasswordChangingType passwordChangingType = new PasswordChangingType();
            passwordChangingType.setPassword(password);
            passwordChangingType.setUserId(user.getId());
            userService.updateUserPassword(null, passwordChangingType);
            userService.updateResetToken(null, user.getId());
        }
        return user;
    }

    public String generateAccessToken(Long userId) {
        return jwtService.generateAccessToken(userService.getNotNullUserById(userId), TenancyContext.getTenantName());
    }

    public User getUser(String username) {
        return userService.getUserByUsernameOrEmail(username);
    }
}
