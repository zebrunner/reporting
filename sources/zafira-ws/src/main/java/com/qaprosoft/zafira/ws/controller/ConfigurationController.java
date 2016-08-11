package com.qaprosoft.zafira.ws.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestSuiteService;
import com.qaprosoft.zafira.services.services.VersionService;

@Controller
@RequestMapping("config")
public class ConfigurationController extends AbstractController
{
	@Autowired
	private VersionService versionService;
	
	@Autowired
	private TestSuiteService testSuiteService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> getVersion() throws ServiceException
	{
		Map<String, String> config = new HashMap<String, String>();
		config.put("service", versionService.getServiceVersion());
		config.put("client", versionService.getClientVersion());
		return config;
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "projects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getAllProjects() throws ServiceException
	{
		return testSuiteService.getAllProjects();
	}
}
