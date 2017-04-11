package com.qaprosoft.zafira.ws.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import springfox.documentation.annotations.ApiIgnore;

import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.JenkinsService;
import com.qaprosoft.zafira.services.services.JiraService;
import com.qaprosoft.zafira.services.services.ProjectService;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.VersionService;
import com.qaprosoft.zafira.services.services.slack.SlackService;

@Controller
@ApiIgnore
@RequestMapping("config")
public class ConfigurationController extends AbstractController
{
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
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getVersion() throws ServiceException
	{
		Map<String, Object> config = new HashMap<>();
		config.put("service", versionService.getServiceVersion());
		config.put("client", versionService.getClientVersion());
		return config;
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "projects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Project> getAllProjects() throws ServiceException
	{
		return projectService.getAllProjects();
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "jenkins", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getJenkinsConfig() throws ServiceException
	{
		Map<String, Object> config = new HashMap<>();
		config.put("connected", jenkinsService.isConnected());
		return config;
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "jira", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getJiraConfig() throws ServiceException
	{
		Map<String, Object> config = new HashMap<>();
		config.put("connected", jiraService.isConnected());
		return config;
	}
	
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