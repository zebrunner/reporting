package com.qaprosoft.zafira.ws.controller.api;

import javax.validation.Valid;

import com.qaprosoft.zafira.models.db.Group;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

import java.util.ArrayList;
import java.util.List;

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
		User user = userService.getUserById(getPrincipalId());
		List<Group.Role> roles = new ArrayList<>();
		UserType userType = mapper.map(user, UserType.class);
		for (Group group : user.getGroups())
		{
			roles.add(group.getRole());
		}
		userType.setRoles(roles);
		return userType;
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
	@RequestMapping(value = "search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<User> searchUsers(@Valid @RequestBody UserSearchCriteria sc)
			throws ServiceException
	{
		return userService.searchUsers(sc);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create ot update user", nickname = "createOrUpdateUser", code = 200, httpMethod = "PUT", response = UserType.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UserType createOrUpdateUser(@RequestBody @Valid UserType user,
			@RequestHeader(value = "Project", required = false) String project) throws ServiceException
	{
		return mapper.map(userService.createOrUpdateUser(mapper.map(user, User.class)), UserType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete user", nickname = "deleteUser", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable(value = "id") long id) throws ServiceException
	{
		userService.deleteUser(id);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Add user to group", nickname = "addUserToGroup", code = 200, httpMethod = "PUT", response = User.class)
	@RequestMapping(value = "group/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody User addUserToGroup(@RequestBody User user, @PathVariable(value = "id") long id)
			throws ServiceException
	{
		return userService.addUserToGroup(user, id);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Delete user from group", nickname = "deleteUserFromGroup", code = 200, httpMethod = "DELETE")
	@RequestMapping(value = "{userId}/group/{groupId}", method = RequestMethod.DELETE)
	public void deleteUserFromGroup(@PathVariable(value = "groupId") long groupId,
			@PathVariable(value = "userId") long userId) throws ServiceException
	{
		userService.deleteUserFromGroup(groupId, userId);
	}
}
