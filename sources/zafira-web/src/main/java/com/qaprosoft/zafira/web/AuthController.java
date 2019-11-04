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
import com.qaprosoft.zafira.models.db.Invitation;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.AccessTokenType;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.models.dto.auth.CredentialsType;
import com.qaprosoft.zafira.models.dto.auth.EmailType;
import com.qaprosoft.zafira.models.dto.auth.RefreshTokenType;
import com.qaprosoft.zafira.models.dto.auth.TenantAuth;
import com.qaprosoft.zafira.models.dto.auth.TenantType;
import com.qaprosoft.zafira.models.dto.user.PasswordChangingType;
import com.qaprosoft.zafira.models.dto.user.PasswordType;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.service.ForgotPasswordService;
import com.qaprosoft.zafira.service.GroupService;
import com.qaprosoft.zafira.service.InvitationService;
import com.qaprosoft.zafira.service.JWTService;
import com.qaprosoft.zafira.service.UserService;
import com.qaprosoft.zafira.service.exception.ForbiddenOperationException;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.InvalidCredentialsException;
import com.qaprosoft.zafira.service.exception.UserNotFoundException;
import com.qaprosoft.zafira.service.integration.tool.impl.AccessManagementService;
import com.qaprosoft.zafira.service.util.URLResolver;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.USER_CAN_NOT_BE_CREATED;

@Api("Auth API")
@CrossOrigin
@RequestMapping(path = "api/auth", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
public class AuthController extends AbstractController {

    private static final String ERR_MSG_USER_ALREADY_EXISTS = "User with such username already exists";

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccessManagementService accessManagementService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private AuthenticationManager authenticationInternalManager;

    @Autowired
    private AuthenticationManager authenticationLdapManager;

    @Autowired
    private URLResolver urlResolver;

    @Autowired
    private Mapper mapper;

    @Value("${zafira.admin.username}")
    private String adminUsername;

    @Value("${zafira.multitenant}")
    private boolean isMultitenant;

    @Value("${zafira.use-artifact-proxy:false}")
    private boolean useArtifactsProxy;

    @ApiResponseStatuses
    @ApiOperation(value = "Get current tenant", nickname = "getTenant", httpMethod = "GET", response = String.class)
    @GetMapping("/tenant")
    public TenantType getTenant() {
        TenantType tenantType = new TenantType(TenancyContext.getTenantName(), urlResolver.getServiceURL(), useArtifactsProxy);
        tenantType.setMultitenant(isMultitenant);
        return tenantType;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Check tenant permissions", nickname = "checkPermissions", httpMethod = "POST")
    @PostMapping("/tenant/verification")
    public ResponseEntity<Void> checkPermissions(@Valid @RequestBody TenantAuth tenantAuth) {
        boolean result = jwtService.checkPermissions(tenantAuth.getTenantName(), tenantAuth.getToken(), tenantAuth.getPermissions());
        HttpStatus httpStatus = result ? HttpStatus.OK : HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(httpStatus);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Generates auth token", nickname = "login", httpMethod = "POST", response = AuthTokenType.class)
    @PostMapping("/login")
    public AuthTokenType login(@Valid @RequestBody CredentialsType credentials) throws BadCredentialsException {
        AuthTokenType authToken;
        try {
            Authentication authentication;
            User user = userService.getUserByUsernameOrEmail(credentials.getUsername());

            final AuthenticationManager authenticationManager = user == null
                    || user.getSource().equals(User.Source.LDAP) ? authenticationLdapManager : authenticationInternalManager;

            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));

            user = userService.getUserByUsernameOrEmail(credentials.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            final String tenant = TenancyContext.getTenantName();

            authToken = new AuthTokenType("Bearer", jwtService.generateAuthToken(user, tenant),
                    jwtService.generateRefreshToken(user, tenant), jwtService.getExpiration(), tenant);

            userService.updateLastLoginDate(user.getId());
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return authToken;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Sign up", nickname = "signup", httpMethod = "POST")
    @PostMapping("/signup")
    public void signup(@RequestHeader("Access-Token") String token, @Valid @RequestBody UserType userType) {
        if (userService.getUserByUsername(userType.getUsername()) != null) {
            throw new IllegalOperationException(USER_CAN_NOT_BE_CREATED, ERR_MSG_USER_ALREADY_EXISTS);
        }
        Invitation invitation = invitationService.getInvitationByToken(token);
        if (invitation == null) {
            throw new ForbiddenOperationException();
        }
        if (invitation.getSource().equals(User.Source.LDAP)) {
            boolean ldapEnabled = accessManagementService.isEnabledAndConnected(null);
            boolean userExists = accessManagementService.isUserExists(userType.getUsername());
            if (ldapEnabled && !userExists) {
                throw new ForbiddenOperationException();
            }
        }
        userType.setSource(invitation.getSource());
        invitation.setStatus(Invitation.Status.ACCEPTED);
        invitationService.updateInvitation(invitation);
        userService.createOrUpdateUser(mapper.map(userType, User.class), groupService.getGroupById(invitation.getGroupId()));
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Refreshes auth token", nickname = "refreshToken", httpMethod = "POST", response = AuthTokenType.class)
    @PostMapping("/refresh")
    public AuthTokenType refresh(@RequestBody @Valid RefreshTokenType refreshToken)
            throws BadCredentialsException, ForbiddenOperationException {
        AuthTokenType authToken;
        try {
            User jwtUser = jwtService.parseRefreshToken(refreshToken.getRefreshToken());

            User user = userService.getUserById(jwtUser.getId());
            if (user == null) {
                throw new UserNotFoundException();
            }

            if (User.Status.INACTIVE.equals(user.getStatus())) {
                throw new BadCredentialsException("Unable to refresh auth token");
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

    @ApiResponseStatuses
    @ApiOperation(value = "Forgot password", nickname = "forgotPassword", httpMethod = "POST")
    @PostMapping("/password/forgot")
    public void forgotPassword(@Valid @RequestBody EmailType emailType) {
        User user = userService.getUserByEmail(emailType.getEmail());
        if (user != null) {
            forgotPasswordService.sendForgotPasswordEmail(emailType, user);
        }
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get forgot password type by token", nickname = "getForgotPasswordType", httpMethod = "GET")
    @GetMapping("/password/forgot")
    public void getForgotPasswordType(@RequestParam("token") String token) throws ForbiddenOperationException {
        User user = userService.getUserByResetToken(token);
        if (user == null || !user.getSource().equals(User.Source.INTERNAL)) {
            throw new ForbiddenOperationException();
        }
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Reset password", nickname = "resetPassword", httpMethod = "PUT")
    @PutMapping("/password")
    public void resetPassword(@RequestHeader("Access-Token") String token, @Valid @RequestBody PasswordType passwordType) {
        User user = userService.getUserByResetToken(token);
        if (user != null && user.getSource().equals(User.Source.INTERNAL)) {
            PasswordChangingType passwordChangingType = new PasswordChangingType();
            passwordChangingType.setPassword(passwordType.getPassword());
            passwordChangingType.setUserId(user.getId());
            userService.updateUserPassword(null, passwordChangingType);
            userService.updateResetToken(null, user.getId());
        }
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Generates access token", nickname = "accessToken", httpMethod = "GET", response = AuthTokenType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/access")
    public AccessTokenType accessToken() {
        String token = jwtService.generateAccessToken(userService.getNotNullUserById(getPrincipalId()), TenancyContext.getTenantName());
        return new AccessTokenType(token);
    }

}
