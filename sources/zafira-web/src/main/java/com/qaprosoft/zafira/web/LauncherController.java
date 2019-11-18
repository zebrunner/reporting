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
 ******************************************************************************/
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.db.Launcher;
import com.qaprosoft.zafira.models.db.LauncherWebHookPayload;
import com.qaprosoft.zafira.models.dto.JobResult;
import com.qaprosoft.zafira.models.dto.LauncherScannerType;
import com.qaprosoft.zafira.models.dto.LauncherType;
import com.qaprosoft.zafira.models.dto.ScannedRepoLaunchersType;
import com.qaprosoft.zafira.models.push.LauncherPush;
import com.qaprosoft.zafira.models.push.LauncherRunPush;
import com.qaprosoft.zafira.service.LauncherService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Api("Launchers API")
@CrossOrigin
@RequestMapping(path = "api/launchers", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class LauncherController extends AbstractController {

    private final LauncherService launcherService;
    private final Mapper mapper;
    private final SimpMessagingTemplate websocketTemplate;

    public LauncherController(LauncherService launcherService, Mapper mapper, SimpMessagingTemplate websocketTemplate) {
        this.launcherService = launcherService;
        this.mapper = mapper;
        this.websocketTemplate = websocketTemplate;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create launcher", nickname = "createLauncher", httpMethod = "POST", response = LauncherType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping()
    public LauncherType createLauncher(@RequestBody @Valid LauncherType launcherType,
                                       @RequestParam(name = "automationServerId", required = false) Long automationServerId) {
        Launcher launcher = mapper.map(launcherType, Launcher.class);
        Long principalId = getPrincipalId();
        launcher = launcherService.createLauncher(launcher, principalId, automationServerId);
        return mapper.map(launcher, LauncherType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get launcher by id", nickname = "getLauncherById", httpMethod = "GET", response = LauncherType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/{id}")
    public LauncherType getLauncherById(@PathVariable("id") Long id) {
        Launcher launcher = launcherService.getLauncherById(id);
        return mapper.map(launcher, LauncherType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all launchers", nickname = "getAllLaunchers", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping()
    public List<LauncherType> getAllLaunchers() {
        List<Launcher> launchers = launcherService.getAllLaunchers();
        return launchers.stream()
                        .map(launcher -> mapper.map(launcher, LauncherType.class))
                        .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update launcher", nickname = "updateLauncher", httpMethod = "PUT", response = LauncherType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PutMapping()
    public LauncherType updateLauncher(@RequestBody @Valid LauncherType launcherType) {
        Launcher launcher = mapper.map(launcherType, Launcher.class);
        launcher = launcherService.updateLauncher(launcher);
        return mapper.map(launcher, LauncherType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Delete launcher by id", nickname = "deleteLauncherById", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/{id}")
    public void deleteLauncherById(@PathVariable("id") Long id) {
        launcherService.deleteLauncherById(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Build job with launcher", nickname = "build", httpMethod = "POST")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @PostMapping("/build")
    public void build(@RequestBody @Valid LauncherType launcherType,
                      @RequestParam(name = "providerId", required = false) Long providerId) throws IOException {
        Launcher launcher = mapper.map(launcherType, Launcher.class);
        String ciRunId = launcherService.buildLauncherJob(launcher, getPrincipalId(), providerId);
        websocketTemplate.convertAndSend(getLauncherRunsWebsocketPath(), new LauncherRunPush(launcher, ciRunId));
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Build job by webHook", nickname = "buildByWebHook", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @PostMapping("/{id}/build/{ref}")
    public String buildByWebHook(@RequestBody @Valid LauncherWebHookPayload payload,
                                 @PathVariable("id") Long id,
                                 @PathVariable("ref") String ref,
                                 @RequestParam(name = "providerId", required = false) Long providerId
    ) throws IOException {
        return launcherService.buildLauncherJobByPresetRef(id, ref, payload, getPrincipalId(), providerId);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get build number", nickname = "getBuildNumber", httpMethod = "GET", response = Integer.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/build/number")
    public Integer getBuildNumber(@RequestParam("queueItemUrl") String queueItemUrl,
                                  @RequestParam(name = "automationServerId", required = false) Long automationServerId) {
        return launcherService.getBuildNumber(queueItemUrl, automationServerId);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Scan launchers with jenkins", nickname = "runScanner", httpMethod = "POST", response = JobResult.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping("/scanner")
    public JobResult runScanner(@RequestBody @Valid LauncherScannerType launcherScannerType,
                                @RequestParam(name = "automationServerId", required = false) Long automationServerId) {
        return launcherService.buildScannerJob(
                getPrincipalId(),
                launcherScannerType.getBranch(),
                launcherScannerType.getScmAccountId(),
                launcherScannerType.isRescan(),
                automationServerId
        );
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Cancel launcher scanner", nickname = "cancelScanner", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/scanner/{buildNumber}")
    public void cancelScanner(@PathVariable("buildNumber") int buildNumber,
                              @RequestParam("scmAccountId") Long scmAccountId,
                              @RequestParam("rescan") boolean rescan,
                              @RequestParam(name = "automationServerId", required = false) Long automationServerId) {
        launcherService.abortScannerJob(scmAccountId, buildNumber, rescan, automationServerId);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create launchers from Jenkins", nickname = "createLaunchersFromJenkins", httpMethod = "POST", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping("/create")
    public List<LauncherType> createLaunchersFromJenkins(@RequestBody @Valid ScannedRepoLaunchersType scannedRepoLaunchersType) {
        Long principalId = getPrincipalId();
        List<Launcher> launchers = launcherService.createLaunchersForJob(scannedRepoLaunchersType, principalId);
        List<LauncherType> launcherTypes = launchers.stream()
                                                    .map(launcher -> mapper.map(launcher, LauncherType.class))
                                                    .collect(Collectors.toList());
        websocketTemplate.convertAndSend(getLaunchersWebsocketPath(), new LauncherPush(launcherTypes, scannedRepoLaunchersType.getUserId(), scannedRepoLaunchersType.isSuccess()));
        return launcherTypes;
    }

}
