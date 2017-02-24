package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.UserType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.UserService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Controller
@Api(value = "Users operations")
@RequestMapping("users")
public class UsersController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private UserService userService;

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@Secured({"ROLE_ADMIN"})
	public ModelAndView openIndexPage()
	{
		return new ModelAndView("users/index");
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "profile", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView openProfilePage()
	{
		return new ModelAndView("users/profile");
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create user", nickname = "createUser", code = 200, httpMethod = "POST",
			notes = "Creates a new user.", response = UserType.class, responseContainer = "UserType")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({"ROLE_ADMIN"})
	public @ResponseBody UserType createUser(@RequestBody @Valid UserType user, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		return mapper.map(userService.createOrUpdateUser(mapper.map(user, User.class)), UserType.class);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<User> searchUsers(@RequestBody UserSearchCriteria sc) throws ServiceException
	{
		return userService.searchUsers(sc);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody User getUser(@PathVariable(value="id") long id) throws ServiceException
	{
		return userService.getUserById(id);
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="current", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object getCurrentUser() throws ServiceException
	{
		return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	@Secured({"ROLE_ADMIN"})
	public void deleteUser(@PathVariable(value="id") long id) throws ServiceException
	{
		userService.deleteUser(id);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody User updateUser(@RequestBody User user, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		return userService.createOrUpdateUser(user);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "group/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({"ROLE_ADMIN"})
	public @ResponseBody User addUserToGroup(@RequestBody User user, @PathVariable(value = "id") long id) throws ServiceException
	{
		return userService.addUserToGroup(user, id);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="group/{groupId}/{userId}", method = RequestMethod.DELETE)
	@Secured({"ROLE_ADMIN"})
	public void deleteUserFromGroup(@PathVariable(value = "groupId") long groupId, @PathVariable(value = "userId") long userId) throws ServiceException
	{
		userService.deleteUserFromGroup(groupId, userId);
	}
}
