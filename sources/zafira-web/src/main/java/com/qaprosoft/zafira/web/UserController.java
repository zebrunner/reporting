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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.UserPreference;
import com.qaprosoft.zafira.models.dto.user.ChangePasswordDTO;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.service.DashboardService;
import com.qaprosoft.zafira.service.UserPreferenceService;
import com.qaprosoft.zafira.service.UserService;
import com.qaprosoft.zafira.web.documented.UserDocumentedController;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RequestMapping(path = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UserController extends AbstractController implements UserDocumentedController {

    private final UserService userService;
    private final DashboardService dashboardService;
    private final UserPreferenceService userPreferenceService;
    private final Mapper mapper;

    public UserController(UserService userService, DashboardService dashboardService,
                          UserPreferenceService userPreferenceService, Mapper mapper) {
        this.userService = userService;
        this.dashboardService = dashboardService;
        this.userPreferenceService = userPreferenceService;
        this.mapper = mapper;
    }

    @GetMapping("/profile")
    @Override
    public UserType getUserProfile(@RequestParam(value = "username", required = false) String username) {
        User user = StringUtils.isEmpty(username) ? userService.getNotNullUserById(getPrincipalId())
                                                  : userService.getNotNullUserByUsername(username);
        UserType userType = mapper.map(user, UserType.class);
        userType.setRoles(user.getRoles());
        userType.setPreferences(user.getPreferences());
        userType.setPermissions(user.getPermissions());
        return userType;
    }

    @GetMapping("/profile/extended")
    @Override
    public Map<String, Object> getExtendedUserProfile() {
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

    @PutMapping("/profile")
    @Override
    public UserType updateUserProfile(@Valid @RequestBody UserType userType) {
        checkCurrentUserAccess(userType.getId());
        User user = mapper.map(userType, User.class);
        user = userService.updateUserProfile(user);
        userType = mapper.map(user, UserType.class);
        userType.setRoles(userType.getRoles());
        userType.setPreferences(userType.getPreferences());
        userType.setPhotoURL(userType.getPhotoURL());
        return userType;
    }

    @DeleteMapping("/profile/photo")
    @Deprecated
    @Override
    public void deleteUserProfilePhoto() {
        userService.deleteProfilePhoto(getPrincipalId());
    }

    @PutMapping("/password")
    @Override
    public void updateUserPassword(@Valid @RequestBody ChangePasswordDTO password) {
        checkCurrentUserAccess(password.getUserId());
        boolean forceUpdate = isAdmin() && password.getOldPassword() == null;
        userService.updateUserPassword(password.getUserId(), password.getOldPassword(), password.getPassword(), forceUpdate);
    }

    @PreAuthorize("#isPublic or (hasRole('ROLE_ADMIN') and hasAnyPermission('VIEW_USERS', 'MODIFY_USERS'))")
    @PostMapping("/search")
    @Override
    public SearchResult<User> searchUsers(
            @Valid @RequestBody UserSearchCriteria searchCriteria,
            @RequestParam(value = "public", required = false) boolean isPublic
    ) {
        return userService.searchUsers(searchCriteria, isPublic);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
    @PutMapping()
    @Override
    public UserType createOrUpdateUser(@RequestBody @Valid UserType userType) {
        User user = mapper.map(userType, User.class);
        user = userService.createOrUpdateUser(user);
        return mapper.map(user, UserType.class);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
    @PutMapping("/status")
    @Override
    public UserType updateStatus(@RequestBody @Valid UserType userType) {
        User user = mapper.map(userType, User.class);
        user = userService.updateStatus(user);
        return mapper.map(user, UserType.class);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @PutMapping("/group/{id}")
    @Override
    public User addUserToGroup(@RequestBody User user, @PathVariable("id") long id) {
        return userService.addUserToGroup(user, id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @DeleteMapping("/{userId}/group/{groupId}")
    @Override
    public void deleteUserFromGroup(@PathVariable("groupId") long groupId, @PathVariable("userId") long userId) {
        userService.deleteUserFromGroup(groupId, userId);
    }

    @GetMapping("/preferences")
    @Override
    public List<UserPreference> getDefaultUserPreferences() {
        User user = userService.getUserByUsername("anonymous");
        return userPreferenceService.getAllUserPreferences(user.getId());
    }

    @PutMapping(value = "{userId}/preferences")
    @Override
    public List<UserPreference> createUserPreference(@PathVariable("userId") long userId, @RequestBody List<UserPreference> preferences) {
        preferences.forEach(userPreferenceService::createOrUpdateUserPreference);
        return userPreferenceService.getAllUserPreferences(userId);
    }

    @PutMapping("/preferences/default")
    @Override
    public List<UserPreference> resetUserPreferencesToDefault() {
        return userPreferenceService.resetUserPreferencesToDefault(getPrincipalId());
    }

    @DeleteMapping("/{userId}/preferences")
    @Override
    public void deleteUserPreferences(@PathVariable("userId") long userId) {
        userPreferenceService.deleteUserPreferencesByUserId(userId);
    }

}
