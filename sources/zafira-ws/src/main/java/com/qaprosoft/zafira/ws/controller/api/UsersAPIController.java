package com.qaprosoft.zafira.ws.controller.api;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.user.PasswordType;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.UserService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Users API")
@CrossOrigin
@RequestMapping("api/users")
public class UsersAPIController extends AbstractController
{
	@Autowired
	private UserService userService;
	
	@Autowired
	private Mapper mapper;
	
	@ResponseStatusDetails
	@ApiOperation(value = "Get user profile", nickname = "getUserProfile", code = 200, httpMethod = "GET", response = UserType.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UserType getUserProfile() throws ServiceException
	{
		return mapper.map(userService.getUserById(getPrincipalId()), UserType.class);
	}
	
	@ResponseStatusDetails
	@ApiOperation(value = "Update user profile", nickname = "updateUserProfile", code = 200, httpMethod = "PUT", response = UserType.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "profile", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UserType updateUserProfile(@Valid @RequestBody UserType user) throws ServiceException
	{
		checkCurrentUserAccess(user.getId());
		return mapper.map(userService.updateUser(mapper.map(user, User.class)), UserType.class);
	}
	
	@ResponseStatusDetails
	@ApiOperation(value = "Update user password", nickname = "updateUserPassword", code = 200, httpMethod = "PUT")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "password", method = RequestMethod.PUT)
	public void updateUserPassword(@Valid @RequestBody PasswordType password) throws ServiceException
	{
		userService.updateUserPassword(getPrincipalId(), password.getPassword());
	}
	
	@ResponseStatusDetails
	@ApiOperation(value = "Search users", nickname = "searchUsers", code = 200, httpMethod = "POST", response = SearchResult.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<User> searchUsers(@Valid @RequestBody UserSearchCriteria sc) throws ServiceException
	{
		return userService.searchUsers(sc);
	}
}
