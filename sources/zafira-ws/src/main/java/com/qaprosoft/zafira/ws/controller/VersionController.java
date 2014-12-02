package com.qaprosoft.zafira.ws.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.VersionService;

@Controller
@RequestMapping("version")
public class VersionController extends AbstractController
{
	@Autowired
	private VersionService versionService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String getVersion(HttpSession session, Model model) throws ServiceException
	{
		model.addAttribute("client_version", versionService.getClientVersion());
		model.addAttribute("service_version", versionService.getServiceVersion());
		return "version";
	}
}
