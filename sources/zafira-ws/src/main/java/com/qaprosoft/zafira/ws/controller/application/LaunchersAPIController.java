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
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.models.db.Launcher;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.JobResult;
import com.qaprosoft.zafira.models.dto.LauncherScannerType;
import com.qaprosoft.zafira.models.dto.LauncherType;
import com.qaprosoft.zafira.models.dto.ScannedRepoLaunchersType;
import com.qaprosoft.zafira.models.push.LauncherPush;
import com.qaprosoft.zafira.models.push.LauncherRunPush;
import com.qaprosoft.zafira.services.services.application.LauncherService;
import com.qaprosoft.zafira.services.services.application.UserService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
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
public class LaunchersAPIController extends AbstractController {

    private final LauncherService launcherService;
    private final UserService userService;
    private final Mapper mapper;
    private final SimpMessagingTemplate websocketTemplate;

    public LaunchersAPIController(LauncherService launcherService, UserService userService, Mapper mapper, SimpMessagingTemplate websocketTemplate) {
        this.launcherService = launcherService;
        this.userService = userService;
        this.mapper = mapper;
        this.websocketTemplate = websocketTemplate;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create launcher", nickname = "createLauncher", httpMethod = "POST", response = LauncherType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping()
    public LauncherType createLauncher(@RequestBody @Valid LauncherType launcherType) {
        User owner = new User(getPrincipalId());
        launcherType.setAutoScan(false);
        return mapper.map(launcherService.createLauncher(mapper.map(launcherType, Launcher.class), owner), LauncherType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get launcher by id", nickname = "getLauncherById", httpMethod = "GET", response = LauncherType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/{id}")
    public LauncherType getLauncherById(@PathVariable("id") Long id) {
        return mapper.map(launcherService.getLauncherById(id), LauncherType.class);
    }

    @ResponseStatusDetails
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

    @ResponseStatusDetails
    @ApiOperation(value = "Update launcher", nickname = "updateLauncher", httpMethod = "PUT", response = LauncherType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PutMapping()
    public LauncherType updateLauncher(@RequestBody @Valid LauncherType launcherType) {
        return mapper.map(launcherService.updateLauncher(mapper.map(launcherType, Launcher.class)), LauncherType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete launcher by id", nickname = "deleteLauncherById", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/{id}")
    public void deleteLauncherById(@PathVariable("id") Long id) {
        launcherService.deleteLauncherById(id);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Build job with launcher", nickname = "build", httpMethod = "POST")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @PostMapping("/build")
    public void build(@RequestBody @Valid LauncherType launcherType) throws IOException {
        Launcher launcher = mapper.map(launcherType, Launcher.class);
        User principal = userService.getNotNullUserById(getPrincipalId());
        String ciRunId = launcherService.buildLauncherJob(launcher, principal);
        websocketTemplate.convertAndSend(getLauncherRunsWebsocketPath(), new LauncherRunPush(launcher, ciRunId));
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get build number", nickname = "getBuildNumber", httpMethod = "GET", response = Integer.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/build/number")
    public Integer getBuildNumber(@RequestParam("queueItemUrl") String queueItemUrl) {
        return launcherService.getBuildNumber(queueItemUrl);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Scan launchers with jenkins", nickname = "runScanner", httpMethod = "POST", response = JobResult.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping("/scanner")
    public JobResult runScanner(@RequestBody @Valid LauncherScannerType launcherScannerType) {
        User user = userService.getNotNullUserById(getPrincipalId());
        return launcherService.buildScannerJob(
                user,
                launcherScannerType.getBranch(),
                launcherScannerType.getScmAccountId(),
                launcherScannerType.isRescan()
        );
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Cancel launcher scanner", nickname = "cancelScanner", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/scanner/{buildNumber}")
    public void cancelScanner(@PathVariable("buildNumber") int buildNumber, @RequestParam("scmAccountId") Long scmAccountId, @RequestParam("rescan") boolean rescan) {
        launcherService.abortScannerJob(scmAccountId, buildNumber, rescan);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create launchers from Jenkins", nickname = "createLaunchersFromJenkins", httpMethod = "POST", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping("/create")
    public List<LauncherType> createLaunchersFromJenkins(@RequestBody @Valid ScannedRepoLaunchersType scannedRepoLaunchersType) {
        List<Launcher> launchers = launcherService.createLaunchersForJob(scannedRepoLaunchersType, new User(getPrincipalId()));
        List<LauncherType> launcherTypes = launchers.stream().map(launcher -> mapper.map(launcher, LauncherType.class)).collect(Collectors.toList());
        websocketTemplate.convertAndSend(getLaunchersWebsocketPath(), new LauncherPush(launcherTypes, scannedRepoLaunchersType.getUserId(), scannedRepoLaunchersType.isSuccess()));
        return launchers.stream()
                        .map(launcher -> mapper.map(launcher, LauncherType.class))
                        .collect(Collectors.toList());
    }

}
