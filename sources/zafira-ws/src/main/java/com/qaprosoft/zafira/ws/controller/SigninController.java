package com.qaprosoft.zafira.ws.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.qaprosoft.zafira.ws.dto.forms.SigninForm;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class SigninController
{
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "signin", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView openSigninPage()
	{
		return new ModelAndView("signin", "signinForm", new SigninForm(false));
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@RequestMapping(value = "/signin/failed", method = RequestMethod.GET)
	public ModelAndView signinFailed(ModelMap model)
	{
		return new ModelAndView("signin", "signinForm", new SigninForm(true));
	}
}