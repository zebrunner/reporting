package com.qaprosoft.zafira.ws.controller;

import java.util.HashMap;
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
import com.qaprosoft.zafira.services.services.VersionService;
import com.qaprosoft.zafira.services.services.thirdparty.push.Channel;
import com.qaprosoft.zafira.services.services.thirdparty.push.PubNubService;

@Controller
@RequestMapping("config")
public class ConfigurationController extends AbstractController
{
	@Autowired
	private PubNubService pubNubService;
	
	@Autowired
	private VersionService versionService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "pubnub", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> getPubNubConfig() throws ServiceException
	{
		Map<String, String> config = new HashMap<String, String>();
		if(pubNubService.isEnabled())
		{
			config.put("udid", "zafira");
			config.put("publishKey", pubNubService.getPublishKey());
			config.put("subscribeKey", pubNubService.getSubscribeKey());
			config.put("testRunsChannel", Channel.TEST_RUN_EVENTS.toString());
			config.put("testsChannel", Channel.TEST_EVENTS.toString());
		}
		return config;
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> getVersion() throws ServiceException
	{
		Map<String, String> config = new HashMap<String, String>();
		config.put("service", versionService.getServiceVersion());
		config.put("client", versionService.getClientVersion());
		return config;
	}
}
