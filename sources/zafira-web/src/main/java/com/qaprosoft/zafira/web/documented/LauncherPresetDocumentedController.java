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

import com.qaprosoft.zafira.models.dto.LauncherPresetDTO;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Launcher presets API")
public interface LauncherPresetDocumentedController {

    @ApiOperation(
            value = "Creates a launcher preset",
            notes = "Returns the created launcher preset",
            nickname = "createLauncherPreset",
            httpMethod = "POST",
            response = LauncherPresetDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherPresetDTO", paramType = "body", dataType = "LauncherPresetDTO", required = true, value = "The launcher preset to create"),
            @ApiImplicitParam(name = "launcherId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The launcher id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created launcher preset", response = LauncherPresetDTO.class),
            @ApiResponse(code = 400, message = "Indicates that a launcher preset with the specified name already exists", response = ErrorResponse.class)
    })
    LauncherPresetDTO createLauncherPreset(LauncherPresetDTO launcherPresetDTO, Long launcherId);

    @ApiOperation(
            value = "Builds a webhook URL",
            notes = "Webhook url needs to build launcher job async from external systems",
            nickname = "buildWebHookUrl",
            httpMethod = "GET",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The launcher preset id"),
            @ApiImplicitParam(name = "launcherId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The launcher id"),
            @ApiImplicitParam(name = "providerId", paramType = "query", dataTypeClass = Long.class, value = "The test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created launcher", response = String.class),
            @ApiResponse(code = 400, message = "Indicates that no automation servers were found (by id or default)", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that the launcher preset cannot be found by id", response = ErrorResponse.class)
    })
    String buildWebHookUrl(Long id, Long launcherId, Long providerId);

    @ApiOperation(
            value = "Updates a launcher preset",
            notes = "Returns the updated launcher preset",
            nickname = "updateLauncherPreset",
            httpMethod = "PUT",
            response = LauncherPresetDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherPresetDTO", paramType = "body", dataType = "LauncherPresetDTO", required = true, value = "The launcher preset to update"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, value = "The launcher preset id"),
            @ApiImplicitParam(name = "launcherId", paramType = "path", dataTypeClass = Long.class, value = "The launcher id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated launcher preset", response = LauncherPresetDTO.class),
            @ApiResponse(code = 400, message = "Indicates that a launcher preset with the specified name already exists", response = ErrorResponse.class)
    })
    LauncherPresetDTO updateLauncherPreset(LauncherPresetDTO launcherPresetDTO, Long id, Long launcherId);

}
