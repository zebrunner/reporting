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
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.services.application.ProjectService;
import com.qaprosoft.zafira.services.services.application.TestRunService;
import com.qaprosoft.zafira.services.services.application.VersionService;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.AutomationServerService;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.SlackService;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.TestCaseManagementService;
import com.qaprosoft.zafira.services.util.URLResolver;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api("Configuration API")
@CrossOrigin
@RequestMapping(path = "api/config", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ConfigurationAPIController extends AbstractController {

    @Autowired
    private VersionService versionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AutomationServerService automationServerService;

    @Autowired
    private TestCaseManagementService testCaseManagementService;

    @Autowired
    private SlackService slackService;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private URLResolver urlResolver;

    @ResponseStatusDetails
    @ApiOperation(value = "Get version", nickname = "getVersion", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/version")
    public Map<String, Object> getVersion() {
        return Map.of(
        "service", versionService.getServiceVersion(),
        "client", versionService.getClientVersion(),
        "service_url", urlResolver.buildWebserviceUrl()
        );
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all projects", nickname = "getAllProjects", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get jenkins config", nickname = "getJenkinsConfig", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/jenkins")
    public Map<String, Object> getJenkinsConfig() {
        boolean enabledAndConnected = automationServerService.isEnabledAndConnected(null);
        return Map.of("connected", enabledAndConnected);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get jira config", nickname = "getJiraConfig", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/jira")
    public Map<String, Object> getJiraConfig() {
        boolean enabledAndConnected = testCaseManagementService.isEnabledAndConnected(null);
        return Map.of("connected", enabledAndConnected);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Is slack available for test run", nickname = "isSlackAvailableForRun", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/slack/{id}")
    public Map<String, Object> isSlackAvailable(@PathVariable("id") long id) {
        TestRun tr = testRunService.getTestRunByIdFull(id);
        boolean available = slackService.getWebhook() != null && StringUtils.isNotEmpty(tr.getSlackChannels())
                && slackService.isEnabledAndConnected(null);
        return Map.of("available", available);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Is slack available", nickname = "isSlackAvailable", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/slack")
    public Map<String, Object> isSlackAvailable() {
        boolean available = slackService.getWebhook() != null && slackService.isEnabledAndConnected(null);
        return Map.of("available", available);
    }

}
