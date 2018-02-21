/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.ws.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.qaprosoft.zafira.services.services.DashboardService;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.jmx.AmazonService;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.qaprosoft.zafira.models.db.UserPreference;
import com.qaprosoft.zafira.models.dto.user.PasswordType;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.UserPreferenceService;
import com.qaprosoft.zafira.services.services.UserService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
	private SettingsService settingsService;

	@Autowired
	DashboardService dashboardService;

	@Autowired
	private UserPreferenceService userPreferenceService;

	@Autowired
	private AmazonService amazonService;

	@Autowired
	private Mapper mapper;

	@ResponseStatusDetails
	@ApiOperation(value = "Get user profile", nickname = "getUserProfile", code = 200, httpMethod = "GET", response = UserType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UserType getUserProfile() throws ServiceException
	{
		User user = userService.getUserById(getPrincipalId());
		UserType userType = mapper.map(user, UserType.class);
		userType.setRoles(user.getRoles());
		userType.setPreferences(user.getPreferences());
		userType.setPermissions(user.getPermissions());
		return userType;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get extended user profile", nickname = "getExtendedUserProfile", code = 200, httpMethod = "GET", response = Map.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "profile/extended", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getExtendedUserProfile() throws ServiceException
	{
		Map<String, Object> extendedUserProfile = new HashMap<>();
		User user = userService.getUserById(getPrincipalId());
		UserType userType = mapper.map(user, UserType.class);
		userType.setRoles(user.getRoles());
		userType.setPreferences(user.getPreferences());
		userType.setPermissions(user.getPermissions());
		extendedUserProfile.put("user", userType);
		extendedUserProfile.put("companyLogo", settingsService.getSettingByName("COMPANY_LOGO_URL"));
		extendedUserProfile.put("performanceDashboardId", dashboardService.getDashboardByTitle("User Performance").getId());
		extendedUserProfile.put("personalDashboardId", dashboardService.getDashboardByTitle("Personal").getId());
		extendedUserProfile.put("defaultDashboardId", dashboardService.getDefaultDashboardByUserId(user.getId()).getId());
		return extendedUserProfile;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update user profile", nickname = "updateUserProfile", code = 200, httpMethod = "PUT", response = UserType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "profile", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UserType updateUserProfile(@Valid @RequestBody UserType user) throws ServiceException
	{
		checkCurrentUserAccess(user.getId());
		UserType userType = mapper.map(userService.updateUser(mapper.map(user, User.class)), UserType.class);
		userType.setRoles(user.getRoles());
		userType.setPreferences(user.getPreferences());
		userType.setPhotoURL(user.getPhotoURL());
		return userType;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete user profile photo", nickname = "deleteUserProfilePhoto", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "profile/photo", method = RequestMethod.DELETE)
	public void deleteUserProfilePhoto() throws ServiceException
	{
		User user = userService.getUserById(getPrincipalId());
		amazonService.removeFile(user.getPhotoURL());
		user.setPhotoURL(StringUtils.EMPTY);
		userService.updateUser(user);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update user password", nickname = "updateUserPassword", code = 200, httpMethod = "PUT")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "password", method = RequestMethod.PUT)
	public void updateUserPassword(@Valid @RequestBody PasswordType password) throws ServiceException
	{
		checkCurrentUserAccess(password.getUserId());
		userService.updateUserPassword(password.getUserId(), password.getPassword());
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Search users", nickname = "searchUsers", code = 200, httpMethod = "POST", response = SearchResult.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<User> searchUsers(@Valid @RequestBody UserSearchCriteria sc)
			throws ServiceException
	{
		return userService.searchUsers(sc);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create ot update user", nickname = "createOrUpdateUser", code = 200, httpMethod = "PUT", response = UserType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UserType createOrUpdateUser(@RequestBody @Valid UserType user,
			@RequestHeader(value = "Project", required = false) String project) throws ServiceException
	{
		return mapper.map(userService.createOrUpdateUser(mapper.map(user, User.class)), UserType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete user", nickname = "deleteUser", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable(value = "id") long id) throws ServiceException
	{
		userService.deleteUser(id);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Add user to group", nickname = "addUserToGroup", code = 200, httpMethod = "PUT", response = User.class)
	@PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
	@RequestMapping(value = "group/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody User addUserToGroup(@RequestBody User user, @PathVariable(value = "id") long id)
			throws ServiceException
	{
		return userService.addUserToGroup(user, id);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Delete user from group", nickname = "deleteUserFromGroup", code = 200, httpMethod = "DELETE")
	@PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
	@RequestMapping(value = "{userId}/group/{groupId}", method = RequestMethod.DELETE)
	public void deleteUserFromGroup(@PathVariable(value = "groupId") long groupId,
			@PathVariable(value = "userId") long userId) throws ServiceException
	{
		userService.deleteUserFromGroup(groupId, userId);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get default user preferences", nickname = "getDefaultUserPreferences", code = 200, httpMethod = "GET", response = List.class)
	@RequestMapping(value = "preferences", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<UserPreference> getDefaultUserPreferences() throws ServiceException
	{
		return userPreferenceService.getAllUserPreferences(userService.getUserByUsername("anonymous").getId());
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update user preferences", nickname = "createDashboardAttribute", code = 200, httpMethod = "PUT", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{userId}/preferences", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<UserPreference> createUserPreference(@PathVariable(value = "userId") long userId,
			@RequestBody List<UserPreference> preferences) throws ServiceException
	{

		for (UserPreference preference : preferences)
		{
			userPreferenceService.createOrUpdateUserPreference(preference);
		}
		return userPreferenceService.getAllUserPreferences(userId);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Reset user preferences to default", nickname = "resetUserPreferencesToDefault", code = 200, httpMethod = "PUT", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "preferences/default", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<UserPreference> resetUserPreferencesToDefault() throws ServiceException
	{
		return userPreferenceService.resetUserPreferencesToDefault(getPrincipalId());
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete user preferences", nickname = "deleteUserPreferences", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{userId}/preferences", method = RequestMethod.DELETE)
	public void deleteUserPreferences(@PathVariable(value = "userId") long userId) throws ServiceException
	{
		userPreferenceService.deleteUserPreferencesByUserId(userId);
	}
}
