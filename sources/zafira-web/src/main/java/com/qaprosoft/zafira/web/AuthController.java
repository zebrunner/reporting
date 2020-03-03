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
import com.qaprosoft.zafira.models.dto.auth.AccessTokenDTO;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenDTO;
import com.qaprosoft.zafira.models.dto.auth.CredentialsDTO;
import com.qaprosoft.zafira.models.dto.auth.EmailDTO;
import com.qaprosoft.zafira.models.dto.auth.RefreshTokenDTO;
import com.qaprosoft.zafira.models.dto.auth.TenancyInfoDTO;
import com.qaprosoft.zafira.models.dto.auth.TenantAuth;
import com.qaprosoft.zafira.models.dto.user.PasswordDTO;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.service.AuthService;
import com.qaprosoft.zafira.service.InvitationService;
import com.qaprosoft.zafira.service.JWTService;
import com.qaprosoft.zafira.service.ResetPasswordService;
import com.qaprosoft.zafira.service.UserService;
import com.qaprosoft.zafira.service.management.TenancyService;
import com.qaprosoft.zafira.service.util.URLResolver;
import com.qaprosoft.zafira.web.documented.AuthDocumentedController;
import org.dozer.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@CrossOrigin
@RequestMapping(path = "api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AuthController extends AbstractController implements AuthDocumentedController {

    private static final String FIRST_LOGIN_HEADER_NAME = "First-Login";

    private final AuthService authService;

    private final JWTService jwtService;

    private final ResetPasswordService resetPasswordService;

    private final UserService userService;

    private final InvitationService invitationService;

    private final URLResolver urlResolver;

    private final Mapper mapper;

    public AuthController(AuthService authService,
                          JWTService jwtService,
                          ResetPasswordService resetPasswordService,
                          UserService userService,
                          InvitationService invitationService,
                          URLResolver urlResolver,
                          Mapper mapper,
                          TenancyService tenancyService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.resetPasswordService = resetPasswordService;
        this.userService = userService;
        this.invitationService = invitationService;
        this.urlResolver = urlResolver;
        this.mapper = mapper;
        this.tenancyService = tenancyService;
    }

    private final TenancyService tenancyService;

    @GetMapping("/tenant")
    @Override
    public TenancyInfoDTO getTenancyInfo() {
        return new TenancyInfoDTO(TenancyContext.getTenantName(), urlResolver.getServiceURL(), tenancyService.isUseArtifactsProxy(), tenancyService.getIsMultitenant());
    }

    @PostMapping("/tenant/verification")
    @Override
    public ResponseEntity<Void> checkPermissions(@Valid @RequestBody TenantAuth tenantAuth) {
        boolean result = jwtService.checkPermissions(tenantAuth.getTenantName(), tenantAuth.getToken(), tenantAuth.getPermissions());
        HttpStatus httpStatus = result ? HttpStatus.OK : HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(httpStatus);
    }

    @PostMapping("/login")
    @Override
    public AuthTokenDTO login(@Valid @RequestBody CredentialsDTO credentialsDTO, HttpServletResponse response) {
        User user = userService.getUserByUsernameOrEmail(credentialsDTO.getUsername());
        Authentication authentication = authService.getAuthentication(credentialsDTO.getUsername(), credentialsDTO.getPassword(), user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (user.getLastLogin() == null) {
            response.addHeader("Access-Control-Expose-Headers", FIRST_LOGIN_HEADER_NAME);
            response.setHeader(FIRST_LOGIN_HEADER_NAME, Boolean.toString(true));
        }

        final String tenant = TenancyContext.getTenantName();
        return new AuthTokenDTO("Bearer", jwtService.generateAuthToken(user, tenant),
                jwtService.generateRefreshToken(user, tenant), jwtService.getExpiration(), tenant);
    }

    @PostMapping("/signup")
    @Override
    public void signup(@RequestHeader("Access-Token") String token, @Valid @RequestBody UserType userType) {
        Invitation invitation = invitationService.acceptInvitation(token, userType.getUsername());
        userType.setSource(invitation.getSource());
        userService.createOrUpdateUser(mapper.map(userType, User.class), invitation.getGroupId());
    }

    @PostMapping("/refresh")
    @Override
    public AuthTokenDTO refresh(@RequestBody @Valid RefreshTokenDTO refreshToken) {
        final String tenant = TenancyContext.getTenantName();
        User jwtUser = jwtService.parseRefreshToken(refreshToken.getRefreshToken());
        User user = authService.getAuthenticatedUser(jwtUser, tenant);
        return new AuthTokenDTO("Bearer", jwtService.generateAuthToken(user, tenant),
                jwtService.generateRefreshToken(user, tenant), jwtService.getExpiration(), tenant);
    }

    @PostMapping("/password/forgot")
    @Override
    public void sendResetPasswordEmail(@Valid @RequestBody EmailDTO emailDTO) {
        resetPasswordService.sendResetPasswordEmail(emailDTO.getEmail());
    }

    @GetMapping("/password/forgot")
    @Override
    public void checkIfTokenResetIsPossible(@RequestParam("token") String token) {
        userService.getUserByResetToken(token);
    }

    @PutMapping("/password")
    @Override
    public void resetPassword(@RequestHeader("Access-Token") String token, @Valid @RequestBody PasswordDTO passwordDTO) {
        resetPasswordService.resetPassword(token, passwordDTO.getPassword());
    }

    @GetMapping("/access")
    @Override
    public AccessTokenDTO accessToken() {
        String token = jwtService.generateAccessToken(userService.getNotNullUserById(getPrincipalId()), TenancyContext.getTenantName());
        return new AccessTokenDTO(token);
    }

}
