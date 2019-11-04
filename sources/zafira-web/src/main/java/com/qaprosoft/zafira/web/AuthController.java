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

import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.AccessTokenType;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.models.dto.auth.CredentialsType;
import com.qaprosoft.zafira.models.dto.auth.EmailType;
import com.qaprosoft.zafira.models.dto.auth.RefreshTokenType;
import com.qaprosoft.zafira.models.dto.auth.TenantAuth;
import com.qaprosoft.zafira.models.dto.auth.TenantType;
import com.qaprosoft.zafira.models.dto.user.PasswordType;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.service.AuthService;
import com.qaprosoft.zafira.service.exception.ForbiddenOperationException;
import com.qaprosoft.zafira.service.exception.InvalidCredentialsException;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
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

@Api("Auth API")
@CrossOrigin
@RequestMapping(path = "api/auth", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
public class AuthController extends AbstractController {

    private final AuthService authService;
    private final AuthenticationManager authenticationInternalManager;
    private final AuthenticationManager authenticationLdapManager;
    private final Mapper mapper;

    public AuthController(AuthService authService, AuthenticationManager authenticationInternalManager, AuthenticationManager authenticationLdapManager, Mapper mapper) {
        this.authService = authService;
        this.authenticationInternalManager = authenticationInternalManager;
        this.authenticationLdapManager = authenticationLdapManager;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get current tenant", nickname = "getTenant", httpMethod = "GET", response = String.class)
    @GetMapping("/tenant")
    public TenantType getTenant() {
        return authService.getTenantInfo();
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Check tenant permissions", nickname = "checkPermissions", httpMethod = "POST")
    @PostMapping("/tenant/verification")
    public ResponseEntity<Void> checkPermissions(@Valid @RequestBody TenantAuth tenantAuth) {
        boolean result = authService.checkTenantPermissions(tenantAuth);
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
            User user = authService.getUser(credentials.getUsername());

            final AuthenticationManager authenticationManager = user == null
                    || user.getSource().equals(User.Source.LDAP) ? authenticationLdapManager : authenticationInternalManager;

            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));

            authToken = authService.buildAuthToken(credentials.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return authToken;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Sign up", nickname = "signup", httpMethod = "POST")
    @PostMapping("/signup")
    public void signup(@RequestHeader("Access-Token") String token, @Valid @RequestBody UserType userType) {
        User user = mapper.map(userType, User.class);
        authService.registerUser(user, token);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Refreshes auth token", nickname = "refreshToken", httpMethod = "POST", response = AuthTokenType.class)
    @PostMapping("/refresh")
    public AuthTokenType refresh(@RequestBody @Valid RefreshTokenType refreshToken)
            throws BadCredentialsException, ForbiddenOperationException {
        AuthTokenType authTokenType;
        try {
            authTokenType = authService.refreshAuthToken(refreshToken.getRefreshToken());
        } catch (InvalidCredentialsException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
        return authTokenType;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Forgot password", nickname = "forgotPassword", httpMethod = "POST")
    @PostMapping("/password/forgot")
    public void forgotPassword(@Valid @RequestBody EmailType emailType) {
        authService.sendForgotPasswordEmail(emailType);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get forgot password type by token", nickname = "getForgotPasswordType", httpMethod = "GET")
    @GetMapping("/password/forgot")
    public void getForgotPasswordType(@RequestParam("token") String token) throws ForbiddenOperationException {
        authService.getUserByResetToken(token);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Reset password", nickname = "resetPassword", httpMethod = "PUT")
    @PutMapping("/password")
    public void resetPassword(@RequestHeader("Access-Token") String token, @Valid @RequestBody PasswordType passwordType) {
        authService.resetPassword(token, passwordType.getPassword());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Generates access token", nickname = "accessToken", httpMethod = "GET", response = AuthTokenType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/access")
    public AccessTokenType accessToken() {
        String token = authService.generateAccessToken(getPrincipalId());
        return new AccessTokenType(token);
    }

}
