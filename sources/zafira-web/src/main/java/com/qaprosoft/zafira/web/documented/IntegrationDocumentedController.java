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

import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.models.dto.integration.IntegrationDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Api("Integrations API")
public interface IntegrationDocumentedController {

    @ApiOperation(
            value = "Creates integration and links it to provided type by id",
            notes = "Returns created integration",
            nickname = "create",
            httpMethod = "POST",
            response = IntegrationDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "integrationDTO", paramType = "body", dataType = "IntegrationDTO", required = true, value = "Integration to create"),
            @ApiImplicitParam(name = "integrationTypeId", paramType = "body", dataType = "number", required = true, value = "Integration type id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created group with created permissions inside", response = IntegrationDTO.class),
            @ApiResponse(code = 400, message = "Indicates that integration mandatory fields have wrong values or integration is malformed", response = ResponseEntity.class)
    })
    IntegrationDTO create(IntegrationDTO integrationDTO, Long integrationTypeId);

    @ApiOperation(
            value = "Retrieves all integrations",
            notes = "Retrieves integrations by group id or group name. If no query params found, it retrieves all integrations",
            nickname = "getAll",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "groupId", paramType = "query", dataType = "number", value = "Integration group id"),
            @ApiImplicitParam(name = "groupName", paramType = "query", dataType = "string", value = "Integration group name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found integrations", response = List.class)
    })
    List<IntegrationDTO> getAll(Long groupId, String groupName);

    @ApiOperation(
            value = "Creates amazon temporary credentials",
            notes = "Returns created temporary credentials from Amazon integration",
            nickname = "getAmazonTemporaryCredentials",
            httpMethod = "GET",
            response = SessionCredentials.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns Amazon session credentials or null if operation is not possible", response = SessionCredentials.class)
    })
    SessionCredentials getAmazonTemporaryCredentials();

    @ApiOperation(
            value = "Updates integration by id",
            notes = "Returns updated integration",
            nickname = "update",
            httpMethod = "PUT",
            response = IntegrationDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "integrationDTO", paramType = "body", dataType = "IntegrationDTO", required = true, value = "Integration to update"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Integration id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated integration", response = IntegrationDTO.class)
    })
    IntegrationDTO update(IntegrationDTO integrationDTO, Long id);

}
