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

import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.service.ConfigurationService;
import com.qaprosoft.zafira.service.project.ProjectService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Api("Configuration API")
@CrossOrigin
@RequestMapping(path = "api/config", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ConfigurationController extends AbstractController {

    private final ConfigurationService configurationService;
    private final ProjectService projectService;

    public ConfigurationController(ConfigurationService configurationService, ProjectService projectService) {
        this.configurationService = configurationService;
        this.projectService = projectService;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves the version value/number", nickname = "getVersion", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/version")
    public Map<String, Object> getVersion() {
        return configurationService.getAppConfig();
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves all projects", nickname = "getAllProjects", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves Jenkins configuration ", nickname = "getJenkinsConfig", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/jenkins")
    public Map<String, Object> getJenkinsConfig() {
        return configurationService.getJenkinsConfig();
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves Jira configuration", nickname = "getJiraConfig", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/jira")
    public Map<String, Object> getJiraConfig() {
        return configurationService.getJiraConfig();
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves Slack configuration", nickname = "getSlackConfig", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping({"/slack", "/slack/{id}"})
    public Map<String, Object> getSlackConfig(@PathVariable(value = "id", required = false) Optional<Long> testRunId) {
        Map<String, Object> config;
        if (testRunId.isPresent()) {
            config = configurationService.getSlackConfigByTestRunId(testRunId.get());
        } else {
            config = configurationService.getSlackConfig();
        }
        return config;
    }

}
