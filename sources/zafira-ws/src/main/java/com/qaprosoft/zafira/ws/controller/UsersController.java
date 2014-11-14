package com.qaprosoft.zafira.ws.controller;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.dbaccess.model.User;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.UserService;
import com.qaprosoft.zafira.ws.dto.UserType;

@Controller
@RequestMapping("users")
public class UsersController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private UserService userService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UserType createUser(@RequestBody @Valid UserType user) throws ServiceException
	{
		return mapper.map(userService.createOrUpdateUser(mapper.map(user, User.class)), UserType.class);
	}
}
