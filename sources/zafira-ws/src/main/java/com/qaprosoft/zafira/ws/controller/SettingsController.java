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

import com.qaprosoft.zafira.services.services.thirdparty.push.XMPPService;

@Controller
@RequestMapping("settings")
public class SettingsController extends AbstractController
{
	@Autowired
	private XMPPService xmppService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "xmpp", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getXMPPSettings()
	{
		Map<String, Object> settings = new HashMap<String, Object>();
		settings.put("enabled", xmppService.isEnabled());	
		settings.put("username", xmppService.getUsername());	
		settings.put("password", xmppService.getPassword());
		settings.put("httpBind", xmppService.getHttpBind());
		return settings;
	}
}
