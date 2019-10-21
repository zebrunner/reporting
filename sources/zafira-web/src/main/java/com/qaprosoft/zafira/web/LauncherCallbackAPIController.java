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

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.service.LauncherCallbackService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("Launcher callbacks API")
@CrossOrigin
@RequestMapping(path = "api/launcher-callbacks", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class LauncherCallbackAPIController extends AbstractController {

    private final LauncherCallbackService launcherCallbackService;
    private final Mapper mapper;

    public LauncherCallbackAPIController(LauncherCallbackService launcherCallbackService, Mapper mapper) {
        this.launcherCallbackService = launcherCallbackService;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get info", nickname = "getInfo", httpMethod = "GET", response = TestRunType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/{ref}")
    public TestRunType getInfo(@PathVariable("ref") String ref) {
        TestRun testRun = launcherCallbackService.buildInfo(ref);
        return mapper.map(testRun, TestRunType.class);
    }
}
