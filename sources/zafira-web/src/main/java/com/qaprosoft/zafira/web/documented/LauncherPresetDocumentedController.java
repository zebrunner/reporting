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
            value = "Creates launcher preset",
            notes = "Returns created launcher preset",
            nickname = "createLauncherPreset",
            httpMethod = "POST",
            response = LauncherPresetDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherPresetDTO", paramType = "body", dataType = "LauncherPresetDTO", required = true, value = "Launcher preset to create"),
            @ApiImplicitParam(name = "launcherId", paramType = "path", dataType = "number", required = true, value = "Launcher id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created launcher preset", response = LauncherPresetDTO.class),
            @ApiResponse(code = 400, message = "Indicates that launcher preset with provided name is already exist", response = ErrorResponse.class)
    })
    LauncherPresetDTO createLauncherPreset(LauncherPresetDTO launcherPresetDTO, Long launcherId);

    @ApiOperation(
            value = "Builds webhook url",
            notes = "Webhook url needs to build launcher job async from external systems",
            nickname = "buildWebHookUrl",
            httpMethod = "GET",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Launcher preset id"),
            @ApiImplicitParam(name = "launcherId", paramType = "path", dataType = "number", required = true, value = "Launcher id"),
            @ApiImplicitParam(name = "providerId", paramType = "query", dataType = "number", value = "Test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created launcher", response = String.class),
            @ApiResponse(code = 400, message = "Indicates that no automation servers were found (by id or default)", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that launcher preset does not exist by id", response = ErrorResponse.class)
    })
    String buildWebHookUrl(Long id, Long launcherId, Long providerId);

    @ApiOperation(
            value = "Updates launcher preset",
            notes = "Returns updated launcher preset",
            nickname = "updateLauncherPreset",
            httpMethod = "PUT",
            response = LauncherPresetDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherPresetDTO", paramType = "body", dataType = "LauncherPresetDTO", required = true, value = "Launcher preset to update"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", value = "Launcher preset id"),
            @ApiImplicitParam(name = "launcherId", paramType = "path", dataType = "number", value = "Launcher id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated launcher preset", response = LauncherPresetDTO.class),
            @ApiResponse(code = 400, message = "Indicates that launcher preset with provided name is already exist", response = ErrorResponse.class)
    })
    LauncherPresetDTO updateLauncherPreset(LauncherPresetDTO launcherPresetDTO, Long id, Long launcherId);

}
