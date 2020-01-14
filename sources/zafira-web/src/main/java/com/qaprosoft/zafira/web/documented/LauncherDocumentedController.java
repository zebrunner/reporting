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

import com.qaprosoft.zafira.models.db.LauncherWebHookPayload;
import com.qaprosoft.zafira.models.dto.JenkinsJobsScanResultDTO;
import com.qaprosoft.zafira.models.dto.JobResult;
import com.qaprosoft.zafira.models.dto.LauncherDTO;
import com.qaprosoft.zafira.models.dto.LauncherScannerType;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
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
            value = "Creates launcher",
            notes = "Returns created launcher. If automation server id is not provided, default automation server id will be attached to launcher",
            nickname = "createLauncher",
            httpMethod = "POST",
            response = LauncherDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherDTO", paramType = "body", dataType = "LauncherDTO", required = true, value = "Launcher to create"),
            @ApiImplicitParam(name = "automationServerId", paramType = "query", dataType = "number", value = "Automation server id with which launcher will be connected")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created launcher", response = LauncherDTO.class),
            @ApiResponse(code = 400, message = "Indicates that no automation servers were found (by id or default)", response = ErrorResponse.class)
    })
    LauncherDTO createLauncher(LauncherDTO launcherDTO, Long automationServerId);

    @ApiOperation(
            value = "Retrieves launcher by id",
            notes = "Returns found launcher",
            nickname = "getLauncherById",
            httpMethod = "GET",
            response = LauncherDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Launcher id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created or updated job", response = LauncherDTO.class),
            @ApiResponse(code = 404, message = "Indicates that launcher does not exist", response = ErrorResponse.class)
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
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found launchers", response = List.class)
    })
    List<LauncherDTO> getAllLaunchers();

    @ApiOperation(
            value = "Updates launcher",
            notes = "Returns updated launcher",
            nickname = "updateLauncher",
            httpMethod = "PUT",
            response = LauncherDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherDTO", paramType = "body", dataType = "LauncherDTO", required = true, value = "Launcher to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated launcher", response = LauncherDTO.class)
    })
    LauncherDTO updateLauncher(LauncherDTO launcherDTO);

    @ApiOperation(
            value = "Deletes launcher by id",
            nickname = "deleteLauncherById",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Launcher id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Launcher was deleted successfully")
    })
    void deleteLauncherById(Long id);

    @ApiOperation(
            value = "Builds launcher job",
            notes = "Builds launcher job using provided or default test automation provider id",
            nickname = "build",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherDTO", paramType = "body", dataType = "LauncherDTO", required = true, value = "Launcher to build"),
            @ApiImplicitParam(name = "providerId", paramType = "query", dataType = "number", value = "Test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Launcher job was built successfully"),
            @ApiResponse(code = 404, message = "Indicates that scm account does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Indicates that launcher job is null or job parameters not contains mandatory arguments or default test automation provider does not exist", response = ErrorResponse.class)
    })
    void build(LauncherDTO launcherDTO, Long providerId) throws IOException;

    @ApiOperation(
            value = "Builds launcher job by webhook",
            notes = "Returns callback reference key",
            nickname = "buildByWebHook",
            httpMethod = "POST",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "payload", paramType = "body", dataType = "LauncherWebHookPayload", required = true, value = "Job to create or update"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Launcher id"),
            @ApiImplicitParam(name = "ref", paramType = "path", dataType = "string", required = true, value = "Launcher preset reference key"),
            @ApiImplicitParam(name = "providerId", paramType = "query", dataType = "number", value = "Test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns callback reference key", response = String.class),
            @ApiResponse(code = 404, message = "Indicates that scm account does not exist or launcher preset does not exist by ref", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Indicates that launcher job is null or job parameters not contains mandatory arguments or test automation provider does not exist (by id or default)", response = ErrorResponse.class)
    })
    String buildByWebHook(LauncherWebHookPayload payload, Long id, String ref, Long providerId) throws IOException;

    @ApiOperation(
            value = "Exchanges automation server queue item url for build number",
            notes = "Build number needs to make possible to abort CI job if need in the feature",
            nickname = "getBuildNumber",
            httpMethod = "GET",
            response = Integer.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "queueItemUrl", paramType = "query", dataType = "string", required = true, value = "CI job queue url"),
            @ApiImplicitParam(name = "automationServerId", paramType = "query", dataType = "number", value = "Test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns exchanged build number", response = Integer.class),
            @ApiResponse(code = 400, message = "Indicates that test automation provider does not exist (by id or default)", response = ErrorResponse.class)
    })
    Integer getBuildNumber(String queueItemUrl, Long automationServerId);

    @ApiOperation(
            value = "Builds scanner job which parses scm repository and creates launchers automatically",
            notes = "Returns object with queueUrl of started job",
            nickname = "runScanner",
            httpMethod = "POST",
            response = JobResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "launcherScannerType", paramType = "body", dataType = "LauncherScannerType", required = true, value = "Scm account info"),
            @ApiImplicitParam(name = "automationServerId", paramType = "query", dataType = "number", value = "Test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created or updated job", response = JobResult.class),
            @ApiResponse(code = 400, message = "Indicates that scm account does not exist or test automation provider does not exist (by id or default)", response = ErrorResponse.class)
    })
    JobResult runScanner(LauncherScannerType launcherScannerType, Long automationServerId);

    @ApiOperation(
            value = "Aborts scanner job by build number",
            nickname = "cancelScanner",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "buildNumber", paramType = "path", dataType = "number", required = true, value = "CI job build number"),
            @ApiImplicitParam(name = "scmAccountId", paramType = "query", dataType = "number", required = true, value = "Scm account id (to retrieve repository url)"),
            @ApiImplicitParam(name = "rescan", paramType = "query", dataType = "boolean", required = true, value = "Flag indicating that scanner job was built for rescan"),
            @ApiImplicitParam(name = "automationServerId", paramType = "query", dataType = "number", value = "Test automation provider id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created or updated job"),
            @ApiResponse(code = 400, message = "Indicates that scm account does not exist or test automation provider does not exist (by id or default)", response = ErrorResponse.class)
    })
    void cancelScanner(int buildNumber, Long scmAccountId, boolean rescan, Long automationServerId);

    @ApiOperation(
            value = "Jenkins callback endpoint",
            notes = "Creates launchers from data that sent from jenkins",
            nickname = "scanLaunchersFromJenkins",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "jenkinsJobsScanResultDTO", paramType = "body", dataType = "JenkinsJobsScanResultDTO", required = true, value = "Scanned scm repository data")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created launchers", response = List.class),
            @ApiResponse(code = 404, message = "Indicates that scm account cannot be found by repository name", response = ErrorResponse.class)
    })
    List<LauncherDTO> scanLaunchersFromJenkins(JenkinsJobsScanResultDTO jenkinsJobsScanResultDTO);

}
