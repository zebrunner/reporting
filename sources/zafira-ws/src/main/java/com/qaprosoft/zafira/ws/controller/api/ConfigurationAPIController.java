package com.qaprosoft.zafira.ws.controller.api;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.*;
import com.qaprosoft.zafira.services.services.slack.SlackService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(value = "Configuration API")
@CrossOrigin
@RequestMapping("api/config")
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
    @ApiOperation(value = "Get version", nickname = "getVersion", code = 200, httpMethod = "GET", response = Map.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Map<String, Object> getVersion() throws ServiceException
    {
        Map<String, Object> config = new HashMap<>();
        config.put("service", versionService.getServiceVersion());
        config.put("client", versionService.getClientVersion());
        return config;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all projects", nickname = "getAllProjects", code = 200, httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "projects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Project> getAllProjects() throws ServiceException
    {
        return projectService.getAllProjects();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get jenkins config", nickname = "getJenkinsConfig", code = 200, httpMethod = "GET", response = Map.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "jenkins", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> getJenkinsConfig() throws ServiceException
    {
        Map<String, Object> config = new HashMap<>();
        config.put("connected", jenkinsService.isConnected());
        return config;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get jira config", nickname = "getJiraConfig", code = 200, httpMethod = "GET", response = Map.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "jira", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> getJiraConfig() throws ServiceException
    {
        Map<String, Object> config = new HashMap<>();
        config.put("connected", jiraService.isConnected());
        return config;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Is slack available", nickname = "isSlackAvailable", code = 200, httpMethod = "GET", response = Map.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "slack/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> isSlackAvailable(@PathVariable(value = "id") long id)
            throws ServiceException,
            IOException, InterruptedException
    {
        Map<String, Object> config = new HashMap<>();
        TestRun tr = testRunService.getTestRunByIdFull(id);
        if (slackService.getWebhook() != null && slackService.getChannelMapping(tr) != null)
        {
            config.put("available", true);
        }else{
            config.put("available", false);
        }
        return config;
    }
}
