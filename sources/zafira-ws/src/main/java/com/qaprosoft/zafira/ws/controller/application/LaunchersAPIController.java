/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.models.db.Launcher;
import com.qaprosoft.zafira.models.dto.LauncherType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.LauncherService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Api(value = "Launchers API")
@CrossOrigin
@RequestMapping("api/launchers")
public class LaunchersAPIController extends AbstractController {

    @Autowired
    private LauncherService launcherService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Create launcher", nickname = "createLauncher", httpMethod = "POST", response = LauncherType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody LauncherType createLauncher(@RequestBody @Valid LauncherType launcherType) throws ServiceException {
        return mapper.map(launcherService.createLauncher(mapper.map(launcherType, Launcher.class)), LauncherType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get launcher by id", nickname = "getLauncherById", httpMethod = "GET", response = LauncherType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody LauncherType getLauncherById(@PathVariable(value = "id") Long id) throws ServiceException {
        return mapper.map(launcherService.getLauncherById(id), LauncherType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all launchers", nickname = "getAllLaunchers", httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<LauncherType> getAllLaunchers() throws ServiceException {
        return launcherService.getAllLaunchers().stream().map(launcher -> mapper.map(launcher, LauncherType.class)).collect(Collectors.toList());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update launcher", nickname = "updateLauncher", httpMethod = "PUT", response = LauncherType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody LauncherType updateLauncher(@RequestBody @Valid LauncherType launcherType) throws ServiceException {
        return mapper.map(launcherService.updateLauncher(mapper.map(launcherType, Launcher.class)), LauncherType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete launcher by id", nickname = "deleteLauncherById", httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void deleteLauncherById(@PathVariable(value = "id") Long id) throws ServiceException {
        launcherService.deleteLauncherById(id);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Build job with launcher", nickname = "build", httpMethod = "POST")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "build", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void build(@RequestBody @Valid LauncherType launcherType) throws ServiceException, IOException {
        launcherService.buildLauncherJob(mapper.map(launcherType, Launcher.class));
    }
}
