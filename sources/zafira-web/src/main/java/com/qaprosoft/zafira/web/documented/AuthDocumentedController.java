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
package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.models.dto.auth.AccessTokenDTO;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenDTO;
import com.qaprosoft.zafira.models.dto.auth.CredentialsDTO;
import com.qaprosoft.zafira.models.dto.auth.EmailDTO;
import com.qaprosoft.zafira.models.dto.auth.RefreshTokenDTO;
import com.qaprosoft.zafira.models.dto.auth.TenancyInfoDTO;
import com.qaprosoft.zafira.models.dto.auth.TenantAuth;
import com.qaprosoft.zafira.models.dto.user.PasswordDTO;
import com.qaprosoft.zafira.models.dto.user.UserType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

@Api("Auth API")
public interface AuthDocumentedController {

    @ApiOperation(
            value = "Gets tenant`s info",
            notes = "Returns base tenant info like tenant name, service url etc.",
            nickname = "getTenancyInfo",
            httpMethod = "GET",
            response = TenancyInfoDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns base tenant`s info", response = TenancyInfoDTO.class)
    })
    TenancyInfoDTO getTenancyInfo();

    @ApiOperation(
            value = "Check tenant permissions",
            notes = "Checks that token has all permissions",
            nickname = "checkPermissions",
            httpMethod = "POST",
            response = ResponseEntity.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantAuth", paramType = "body", dataType = "TenantAuth", required = true, value = "Tenant auth data to check")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Jwt token has all permissions", response = ResponseEntity.class),
            @ApiResponse(code = 403, message = "Jwt token does not have required permissions", response = ResponseEntity.class)
    })
    ResponseEntity<Void> checkPermissions(TenantAuth tenantAuth);

    @ApiOperation(
            value = "Generates auth token",
            notes = "Returns generated auth token, that will be used in authenticated api calls",
            nickname = "login",
            httpMethod = "POST",
            response = AuthTokenDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "credentialsDTO", paramType = "body", dataType = "CredentialsDTO", required = true, value = "Credentials for user authentication")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns auth token", response = AuthTokenDTO.class),
            @ApiResponse(code = 401, message = "Indicates that user credentials are invalid", response = ResponseEntity.class)
    })
    AuthTokenDTO login(CredentialsDTO credentialsDTO);

    @ApiOperation(
            value = "Registers new user in application",
            notes = "Creates user in application",
            nickname = "signup",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", paramType = "header", dataType = "string", required = true, value = "Token that was sent to invite new user"),
            @ApiImplicitParam(name = "userType", paramType = "body", dataType = "UserType", required = true, value = "User info to register")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Creates user in application"),
            @ApiResponse(code = 400, message = "Indicates that user already exists")
    })
    void signup(String token, UserType userType);

    @ApiOperation(
            value = "Refreshes auth token",
            notes = "Returns refreshed auth token",
            nickname = "refresh",
            httpMethod = "POST",
            response = AuthTokenDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refreshToken", paramType = "body", dataType = "RefreshTokenDTO", required = true, value = "Token to refresh")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns new auth token", response = AuthTokenDTO.class),
            @ApiResponse(code = 401, message = "Indicates that token cannot be refreshed", response = ResponseEntity.class)
    })
    AuthTokenDTO refresh(RefreshTokenDTO refreshToken);

    @ApiOperation(
            value = "Sends reset password email",
            notes = "Generates reset password token and sends email",
            nickname = "sendResetPasswordEmail",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "emailDTO", paramType = "body", dataType = "EmailDTO", required = true, value = "Email to send")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Email was sent successfully"),
            @ApiResponse(code = 404, message = "Indicates that an account does not exist", response = ResponseEntity.class),
    })
    void sendResetPasswordEmail(EmailDTO emailDTO);

    @ApiOperation(
            value = "Checks that sent reset password token is valid",
            notes = "Checks that account exists and account source is internal",
            nickname = "checkIfTokenResetIsPossible",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", paramType = "body", dataType = "string", required = true, value = "Reset password token to check")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Reset token is valid and reset password operation is possible"),
            @ApiResponse(code = 400, message = "Indicates that reset password operation is not possible", response = ResponseEntity.class)
    })
    void checkIfTokenResetIsPossible(String token);

    @ApiOperation(
            value = "Resets old password and changes it with new one",
            notes = "Checks that reset password operation is possible and changes password",
            nickname = "resetPassword",
            httpMethod = "PUT"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Access-Token", paramType = "header", required = true, value = "Reset password token"),
            @ApiImplicitParam(name = "passwordDTO", paramType = "body", dataType = "PasswordDTO", required = true, value = "Password to change")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password was updated successfully"),
            @ApiResponse(code = 400, message = "Indicates that reset password operation is not possible", response = ResponseEntity.class)
    })
    void resetPassword(String token, PasswordDTO passwordDTO);

    @ApiOperation(
            value = "Generates API access token",
            notes = "Returns token that provide an access to API",
            nickname = "accessToken",
            httpMethod = "GET",
            response = AccessTokenDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns generated temporary access token", response = AccessTokenDTO.class),
            @ApiResponse(code = 404, message = "Indicates that user who wants to generate auth token cannot be recognized", response = ResponseEntity.class)
    })
    AccessTokenDTO accessToken();

}
