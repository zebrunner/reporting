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

import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import com.qaprosoft.zafira.models.entity.integration.IntegrationInfo;
import com.qaprosoft.zafira.service.integration.IntegrationService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api("Integrations info API")
@CrossOrigin
@RequestMapping(path = "api/integrations-info", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class IntegrationInfoController extends AbstractController {

    private final IntegrationService integrationService;

    public IntegrationInfoController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get integration connections info", nickname = "getIntegrationsInfo", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping()
    public Map<String, Map<String, List<IntegrationInfo>>> getIntegrationsInfo() {
        return integrationService.retrieveInfo();
    }

    @ApiOperation(
        value = "Get integration connections info by id",
        notes = "Returns core integration attributes by integration id and group it belongs",
        nickname = "getIntegrationsInfoById",
        httpMethod = "GET",
        response = IntegrationInfo.class
    )
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
        @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Integration id"),
        @ApiImplicitParam(name = "groupName", paramType = "query", dataType = "string", required = true, value = "Integration group name")
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "Returns found integration", response = IntegrationInfo.class),
        @ApiResponse(code = 404, message = "Indicates that integration can not be found and it's information can not be supplied", response = ErrorResponse.class)
    })
    @GetMapping("/{id}")
    public IntegrationInfo getIntegrationsInfoById(@PathVariable("id") Long id, @RequestParam("groupName") String groupName) {
        return integrationService.retrieveInfoByIntegrationId(groupName, id);
    }
}
