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
package com.qaprosoft.zafira.ws.controller.management;

import com.qaprosoft.zafira.models.db.management.User;
import com.qaprosoft.zafira.models.dto.application.user.PasswordType;
import com.qaprosoft.zafira.models.dto.management.UserType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.jmx.AmazonService;
import com.qaprosoft.zafira.services.services.management.MngUserService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Api(value = "Users management API")
@CrossOrigin
@RequestMapping("api/mng/users")
public class MngUsersAPIController extends AbstractController {

    @Autowired
    private MngUserService mngUserService;

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
    public @ResponseBody UserType getUserProfile() throws ServiceException {
        User user = mngUserService.getUserById(getPrincipalId());
        UserType userType = mapper.map(user, UserType.class);
        userType.setRoles(user.getRoles());
        userType.setPermissions(user.getPermissions());
        return userType;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update user profile", nickname = "updateUserProfile", code = 200, httpMethod = "PUT", response = UserType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "profile", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody UserType updateUserProfile(@Valid @RequestBody UserType user) throws ServiceException {
        checkCurrentUserAccess(user.getId());
        UserType userType = mapper.map(mngUserService.updateUser(mapper.map(user, User.class)), UserType.class);
        userType.setRoles(user.getRoles());
        userType.setPhotoURL(user.getPhotoURL());
        return userType;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete user profile photo", nickname = "deleteUserProfilePhoto", code = 200, httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "profile/photo", method = RequestMethod.DELETE)
    public void deleteUserProfilePhoto() throws ServiceException {
        User user = mngUserService.getUserById(getPrincipalId());
        amazonService.removeFile(user.getPhotoURL());
        user.setPhotoURL(StringUtils.EMPTY);
        mngUserService.updateUser(user);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update user password", nickname = "updateUserPassword", code = 200, httpMethod = "PUT")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "password", method = RequestMethod.PUT)
    public void updateUserPassword(@Valid @RequestBody PasswordType password) throws ServiceException {
        checkCurrentUserAccess(password.getUserId());
        mngUserService.updateUserPassword(password.getUserId(), password.getPassword());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all users", nickname = "getAllUsers", code = 200, httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<UserType> getAllUsers() throws ServiceException {
        return mngUserService.getAllUsers().stream().map(user -> mapper.map(user, UserType.class)).collect(Collectors.toList());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create user", nickname = "createUser", code = 200, httpMethod = "POST", response = UserType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody UserType createUser(@RequestBody @Valid UserType user) throws ServiceException {
        return mapper.map(mngUserService.createUser(mapper.map(user, User.class)), UserType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete user", nickname = "deleteUser", code = 200, httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable(value = "id") long id) throws ServiceException {
        mngUserService.deleteUser(id);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Add user to group", nickname = "addUserToGroup", code = 200, httpMethod = "PUT", response = User.class)
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "group/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody UserType addUserToGroup(@RequestBody User user, @PathVariable(value = "id") long id) throws ServiceException {
        return mapper.map(mngUserService.addUserToGroup(user, id), UserType.class);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Delete user from group", nickname = "deleteUserFromGroup", code = 200, httpMethod = "DELETE")
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "{userId}/group/{groupId}", method = RequestMethod.DELETE)
    public void deleteUserFromGroup(@PathVariable(value = "groupId") long groupId, @PathVariable(value = "userId") long userId)
            throws ServiceException {
        mngUserService.deleteUserFromGroup(groupId, userId);
    }
}
