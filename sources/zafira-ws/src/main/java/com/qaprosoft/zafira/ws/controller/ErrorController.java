package com.qaprosoft.zafira.ws.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@Controller
@ApiIgnore
@RequestMapping("errors")
public class ErrorController
{
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView error403(Principal principal)
	{
		return new ModelAndView("errors/403");
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/404", method = RequestMethod.GET)
	public ModelAndView error404(Principal principal)
	{
		return new ModelAndView("errors/404");
	}
}