/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.UserPreference;
import com.qaprosoft.zafira.models.dto.user.PasswordChangingType;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.UserNotFoundException;
import com.qaprosoft.zafira.services.services.application.DashboardService;
import com.qaprosoft.zafira.services.services.application.UserPreferenceService;
import com.qaprosoft.zafira.services.services.application.UserService;
import com.qaprosoft.zafira.services.services.application.integration.impl.AmazonService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("Users API")
@CrossOrigin
@RequestMapping(path = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UsersAPIController extends AbstractController {

    @Autowired
    private UserService userService;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private AmazonService amazonService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Get user profile", nickname = "getUserProfile", httpMethod = "GET", response = UserType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/profile")
    public UserType getUserProfile(@RequestParam(value = "username", required = false) String username) throws ServiceException {
        User user = StringUtils.isEmpty(username) ? userService.getUserById(getPrincipalId())
                : userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException();
        }
        UserType userType = mapper.map(user, UserType.class);
        userType.setRoles(user.getRoles());
        userType.setPreferences(user.getPreferences());
        userType.setPermissions(user.getPermissions());
        return userType;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get extended user profile", nickname = "getExtendedUserProfile", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/profile/extended")
    public Map<String, Object> getExtendedUserProfile() throws ServiceException {
        Map<String, Object> extendedUserProfile = new HashMap<>();
        User user = userService.getUserById(getPrincipalId());
        UserType userType = mapper.map(user, UserType.class);
        userType.setRoles(user.getRoles());
        userType.setPreferences(user.getPreferences());
        userType.setPermissions(user.getPermissions());
        extendedUserProfile.put("user", userType);
        dashboardService.setDefaultDashboard(extendedUserProfile, "", "defaultDashboardId");
        dashboardService.setDefaultDashboard(extendedUserProfile, "User Performance", "performanceDashboardId");
        dashboardService.setDefaultDashboard(extendedUserProfile, "Personal", "personalDashboardId");
        dashboardService.setDefaultDashboard(extendedUserProfile, "Stability", "stabilityDashboardId");
        return extendedUserProfile;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update user profile", nickname = "updateUserProfile", httpMethod = "PUT", response = UserType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PutMapping("/profile")
    public UserType updateUserProfile(@Valid @RequestBody UserType user) throws ServiceException {
        checkCurrentUserAccess(user.getId());
        UserType userType = mapper.map(userService.updateUser(mapper.map(user, User.class)), UserType.class);
        userType.setRoles(user.getRoles());
        userType.setPreferences(user.getPreferences());
        userType.setPhotoURL(user.getPhotoURL());
        return userType;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete user profile photo", nickname = "deleteUserProfilePhoto", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @DeleteMapping("/profile/photo")
    public void deleteUserProfilePhoto() throws ServiceException {
        User user = userService.getUserById(getPrincipalId());
        amazonService.removeFile(user.getPhotoURL());
        user.setPhotoURL(StringUtils.EMPTY);
        userService.updateUser(user);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update user password", nickname = "updateUserPassword", httpMethod = "PUT")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PutMapping("/password")
    public void updateUserPassword(@Valid @RequestBody PasswordChangingType password) throws ServiceException {
        checkCurrentUserAccess(password.getUserId());
        userService.updateUserPassword(password, isAdmin() && password.getOldPassword() == null);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Search users", nickname = "searchUsers", httpMethod = "POST", response = SearchResult.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("#isPublic or (hasRole('ROLE_ADMIN') and hasAnyPermission('VIEW_USERS', 'MODIFY_USERS'))")
    @PostMapping("/search")
    public SearchResult<User> searchUsers(
            @Valid @RequestBody UserSearchCriteria searchCriteria,
            @RequestParam(value = "public", required = false) boolean isPublic) throws ServiceException {
        return userService.searchUsers(searchCriteria, isPublic);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create ot update user", nickname = "createOrUpdateUser", httpMethod = "PUT", response = UserType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
    @PutMapping()
    public UserType createOrUpdateUser(@RequestBody @Valid UserType user) throws ServiceException {
        return mapper.map(userService.createOrUpdateUser(mapper.map(user, User.class)), UserType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update user status", nickname = "updateStatus", httpMethod = "PUT", response = UserType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
    @PutMapping("/status")
    public UserType updateStatus(@RequestBody @Valid UserType user) throws ServiceException {
        return mapper.map(userService.updateStatus(mapper.map(user, User.class)), UserType.class);
    }

    @ResponseStatusDetails
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Add user to group", nickname = "addUserToGroup", httpMethod = "PUT", response = User.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @PutMapping("/group/{id}")
    public User addUserToGroup(@RequestBody User user, @PathVariable("id") long id) throws ServiceException {
        return userService.addUserToGroup(user, id);
    }

    @ResponseStatusDetails
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Delete user from group", nickname = "deleteUserFromGroup", httpMethod = "DELETE")
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @DeleteMapping("/{userId}/group/{groupId}")
    public void deleteUserFromGroup(@PathVariable("groupId") long groupId, @PathVariable("userId") long userId)
            throws ServiceException {
        userService.deleteUserFromGroup(groupId, userId);
    }

    @ResponseStatusDetails
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get default user preferences", nickname = "getDefaultUserPreferences", httpMethod = "GET", response = List.class)
    @GetMapping("/preferences")
    public List<UserPreference> getDefaultUserPreferences() throws ServiceException {
        return userPreferenceService.getAllUserPreferences(userService.getUserByUsername("anonymous").getId());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update user preferences", nickname = "createDashboardAttribute", httpMethod = "PUT", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "{userId}/preferences", method = RequestMethod.PUT)
    public List<UserPreference> createUserPreference(
            @PathVariable("userId") long userId,
            @RequestBody List<UserPreference> preferences) throws ServiceException {
        for (UserPreference preference : preferences) {
            userPreferenceService.createOrUpdateUserPreference(preference);
        }
        return userPreferenceService.getAllUserPreferences(userId);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Reset user preferences to default", nickname = "resetUserPreferencesToDefault", httpMethod = "PUT", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PutMapping("/preferences/default")
    public List<UserPreference> resetUserPreferencesToDefault() throws ServiceException {
        return userPreferenceService.resetUserPreferencesToDefault(getPrincipalId());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete user preferences", nickname = "deleteUserPreferences", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @DeleteMapping("/{userId}/preferences")
    public void deleteUserPreferences(@PathVariable("userId") long userId) throws ServiceException {
        userPreferenceService.deleteUserPreferencesByUserId(userId);
    }

}
