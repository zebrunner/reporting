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
package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.db.LauncherWebHookPayload;
import com.zebrunner.reporting.domain.dto.JenkinsJobsScanResultDTO;
import com.zebrunner.reporting.domain.dto.JobResult;
import com.zebrunner.reporting.domain.dto.LauncherDTO;
import com.zebrunner.reporting.domain.dto.LauncherScannerType;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.util.List;

@Api("Launchers API")
public interface LauncherDocumentedController {

    @ApiOperation(
            value = "Creates a launcher",
            notes = "Returns the created launcher. If the automation server id is not specified, the default automation server id will be attached to the launcher",
            nickname = "createLauncher",
            httpMethod = "POST",
            response = LauncherDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherDTO", paramType = "body", dataType = "LauncherDTO", required = true, value = "The launcher to create"),
            @ApiImplicitParam(name = "automationServerId", paramType = "query", dataTypeClass = Long.class, value = "The automation server id with which the launcher will be connected")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created launcher", response = LauncherDTO.class),
            @ApiResponse(code = 400, message = "Indicates that no automation servers were found (by id or default)", response = ErrorResponse.class)
    })
    LauncherDTO createLauncher(LauncherDTO launcherDTO, Long automationServerId);

    @ApiOperation(
            value = "Retrieves a launcher by its id",
            notes = "Returns the found launcher",
            nickname = "getLauncherById",
            httpMethod = "GET",
            response = LauncherDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The launcher id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found launcher", response = LauncherDTO.class),
            @ApiResponse(code = 404, message = "Indicates that the launcher does not exist", response = ErrorResponse.class)
    })
    LauncherDTO getLauncherById(Long id);

    @ApiOperation(
            value = "Retrieves all launchers",
            notes = "Returns all found launchers",
            nickname = "getAllLaunchers",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found launchers", response = List.class)
    })
    List<LauncherDTO> getAllLaunchers();

    @ApiOperation(
            value = "Updates a launcher",
            notes = "Returns the updated launcher",
            nickname = "updateLauncher",
            httpMethod = "PUT",
            response = LauncherDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherDTO", paramType = "body", dataType = "LauncherDTO", required = true, value = "The launcher to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated launcher", response = LauncherDTO.class)
    })
    LauncherDTO updateLauncher(LauncherDTO launcherDTO);

    @ApiOperation(
            value = "Deletes a launcher by its id",
            nickname = "deleteLauncherById",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The launcher id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The launcher was deleted successfully")
    })
    void deleteLauncherById(Long id);

    @ApiOperation(
            value = "Builds a launcher job",
            notes = "Builds a launcher job using the specified or default id of a test automation provider",
            nickname = "build",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherDTO", paramType = "body", dataType = "LauncherDTO", required = true, value = "The launcher to build"),
            @ApiImplicitParam(name = "providerId", paramType = "query", dataTypeClass = Long.class, value = "The test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The launcher job was built successfully"),
            @ApiResponse(code = 404, message = "Indicates that the SCM account does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Indicates that the launcher job is null, or job parameters do not contain mandatory arguments, or the default test automation provider does not exist", response = ErrorResponse.class)
    })
    void build(LauncherDTO launcherDTO, Long providerId) throws IOException;

    @ApiOperation(
            value = "Builds a launcher job by webhook",
            notes = "Returns a callback reference key",
            nickname = "buildByWebHook",
            httpMethod = "POST",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "payload", paramType = "body", dataType = "LauncherWebHookPayload", required = true, value = "The job to create or update"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The launcher id"),
            @ApiImplicitParam(name = "ref", paramType = "path", dataType = "string", required = true, value = "Launcher preset reference key"),
            @ApiImplicitParam(name = "providerId", paramType = "query", dataTypeClass = Long.class, value = "Test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns a callback reference key", response = String.class),
            @ApiResponse(code = 404, message = "Indicates that the SCM account does not exist, or the launcher preset does not exist by ref", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Indicates that the launcher job is null, or job parameters do not contain mandatory arguments, or the test automation provider does not exist (by id or default)", response = ErrorResponse.class)
    })
    String buildByWebHook(LauncherWebHookPayload payload, Long id, String ref, Long providerId) throws IOException;

    @ApiOperation(
            value = "Exchanges the automation server queue item URL for the build number",
            notes = "The build number must be able to abort a CI job, if needed, in the future",
            nickname = "getBuildNumber",
            httpMethod = "GET",
            response = Integer.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "queueItemUrl", paramType = "query", dataType = "string", required = true, value = "The CI job queue URL"),
            @ApiImplicitParam(name = "automationServerId", paramType = "query", dataTypeClass = Long.class, value = "The test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the exchanged build number", response = Integer.class),
            @ApiResponse(code = 400, message = "Indicates that the test automation provider does not exist (by id or default)", response = ErrorResponse.class)
    })
    Integer getBuildNumber(String queueItemUrl, Long automationServerId);

    @ApiOperation(
            value = "Builds a scanner job which parses an SCM repository and creates launchers automatically",
            notes = "Returns an object with the queue URL of a started job",
            nickname = "runScanner",
            httpMethod = "POST",
            response = JobResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherScannerType", paramType = "body", dataType = "LauncherScannerType", required = true, value = "Information about the SCM account"),
            @ApiImplicitParam(name = "automationServerId", paramType = "query", dataTypeClass = Long.class, value = "The test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns an object with the queue URL of a started job", response = JobResult.class),
            @ApiResponse(code = 400, message = "Indicates that the SCM account does not exist, or the test automation provider does not exist (by id or default)", response = ErrorResponse.class)
    })
    JobResult runScanner(LauncherScannerType launcherScannerType, Long automationServerId);

    @ApiOperation(
            value = "Aborts a scanner job by the build number",
            nickname = "cancelScanner",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "buildNumber", paramType = "path", dataTypeClass = Integer.class, required = true, value = " The CI job build number"),
            @ApiImplicitParam(name = "scmAccountId", paramType = "query", dataTypeClass = Long.class, required = true, value = "The id of the SCM account. Is used to retrieve repository URL"),
            @ApiImplicitParam(name = "rescan", paramType = "query", dataType = "boolean", required = true, value = "A flag indicating that the scanner job was built for rescanning"),
            @ApiImplicitParam(name = "automationServerId", paramType = "query", dataTypeClass = Long.class, value = "The test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created or updated job"),
            @ApiResponse(code = 400, message = "Indicates that the SCM account does not exist, or test automation provider does not exist (by id or default)", response = ErrorResponse.class)
    })
    void cancelScanner(int buildNumber, Long scmAccountId, boolean rescan, Long automationServerId);

    @ApiOperation(
            value = "Jenkins callback endpoint",
            notes = "Creates launchers using data received from Jenkins",
            nickname = "scanLaunchersFromJenkins",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "jenkinsJobsScanResultDTO", paramType = "body", dataType = "JenkinsJobsScanResultDTO", required = true, value = "Scanned data from an SCM repository")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created launchers", response = List.class),
            @ApiResponse(code = 404, message = "Indicates that the SCM account cannot be found by the repository name", response = ErrorResponse.class)
    })
    List<LauncherDTO> scanLaunchersFromJenkins(JenkinsJobsScanResultDTO jenkinsJobsScanResultDTO);

}
