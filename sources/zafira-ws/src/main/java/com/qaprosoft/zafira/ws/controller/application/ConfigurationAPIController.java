/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.ProjectService;
import com.qaprosoft.zafira.services.services.application.TestRunService;
import com.qaprosoft.zafira.services.services.application.VersionService;
import com.qaprosoft.zafira.services.services.application.integration.impl.JenkinsService;
import com.qaprosoft.zafira.services.services.application.integration.impl.JiraService;
import com.qaprosoft.zafira.services.services.application.integration.impl.SlackService;
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

import java.util.HashMap;
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
    private JenkinsService jenkinsService;

    @Autowired
    private JiraService jiraService;

    @Autowired
    private SlackService slackService;

    @Autowired
    private TestRunService testRunService;

    @ResponseStatusDetails
    @ApiOperation(value = "Get version", nickname = "getVersion", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/version")
    public Map<String, Object> getVersion() throws ServiceException {
        Map<String, Object> config = new HashMap<>();
        config.put("service", versionService.getServiceVersion());
        config.put("client", versionService.getClientVersion());
        return config;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all projects", nickname = "getAllProjects", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/projects")
    public List<Project> getAllProjects() throws ServiceException {
        return projectService.getAllProjects();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get jenkins config", nickname = "getJenkinsConfig", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/jenkins")
    public Map<String, Object> getJenkinsConfig() throws ServiceException {
        Map<String, Object> config = new HashMap<>();
        config.put("connected", jenkinsService.isEnabledAndConnected());
        return config;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get jira config", nickname = "getJiraConfig", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/jira")
    public Map<String, Object> getJiraConfig() throws ServiceException {
        Map<String, Object> config = new HashMap<>();
        config.put("connected", jiraService.isEnabledAndConnected());
        return config;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Is slack available for test run", nickname = "isSlackAvailableForRun", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/slack/{id}")
    public Map<String, Object> isSlackAvailable(@PathVariable("id") long id) throws ServiceException {
        Map<String, Object> config = new HashMap<>();
        TestRun tr = testRunService.getTestRunByIdFull(id);
        boolean available = slackService.getWebhook() != null && StringUtils.isNotEmpty(tr.getSlackChannels()) && slackService.isEnabledAndConnected();
        config.put("available", available);
        return config;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Is slack available", nickname = "isSlackAvailable", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/slack")
    public Map<String, Object> isSlackAvailable() throws ServiceException {
        Map<String, Object> config = new HashMap<>();
        boolean available = slackService.getWebhook() != null && slackService.isEnabledAndConnected();
        config.put("available", available);
        return config;
    }

}
