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
import com.qaprosoft.zafira.models.dto.UserPreferenceDTO;
import com.qaprosoft.zafira.models.dto.user.ChangePasswordDTO;
import com.qaprosoft.zafira.models.dto.user.UserDTO;
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
    public UserDTO getUserProfile(@RequestParam(value = "username", required = false) String username) {
        User user = StringUtils.isEmpty(username) ? userService.getNotNullUserById(getPrincipalId())
                                                  : userService.getNotNullUserByUsername(username);
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        userDTO.setRoles(user.getRoles());
        userDTO.setPreferences(user.getPreferences());
        userDTO.setPermissions(user.getPermissions());
        return userDTO;
    }

    @GetMapping("/profile/extended")
    @Override
    public Map<String, Object> getExtendedUserProfile() {
        Map<String, Object> extendedUserProfile = new HashMap<>();
        User user = userService.getUserById(getPrincipalId());
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        userDTO.setRoles(user.getRoles());
        userDTO.setPreferences(user.getPreferences());
        userDTO.setPermissions(user.getPermissions());
        extendedUserProfile.put("user", userDTO);
        dashboardService.setDefaultDashboard(extendedUserProfile, "", "defaultDashboardId");
        dashboardService.setDefaultDashboard(extendedUserProfile, "User Performance", "performanceDashboardId");
        dashboardService.setDefaultDashboard(extendedUserProfile, "Personal", "personalDashboardId");
        dashboardService.setDefaultDashboard(extendedUserProfile, "Stability", "stabilityDashboardId");
        return extendedUserProfile;
    }

    @PutMapping("/profile")
    @Override
    public UserDTO updateUserProfile(@Valid @RequestBody UserDTO userDTO) {
        checkCurrentUserAccess(userDTO.getId());
        User user = mapper.map(userDTO, User.class);
        user = userService.updateUserProfile(user);
        userDTO = mapper.map(user, UserDTO.class);
        userDTO.setRoles(userDTO.getRoles());
        userDTO.setPreferences(userDTO.getPreferences());
        userDTO.setPhotoURL(userDTO.getPhotoURL());
        return userDTO;
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
    public UserDTO createOrUpdateUser(@RequestBody @Valid UserDTO userDTO) {
        User user = mapper.map(userDTO, User.class);
        user = userService.createOrUpdateUser(user);
        return mapper.map(user, UserDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
    @PutMapping("/status")
    @Override
    public UserDTO updateStatus(@RequestBody @Valid UserDTO userDTO) {
        User user = mapper.map(userDTO, User.class);
        user = userService.updateStatus(user);
        return mapper.map(user, UserDTO.class);
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

    @PutMapping(value = "{userId}/preference")
    @Override
    public UserPreferenceDTO createUserPreference(@PathVariable("userId") long userId,
                                                  @RequestParam UserPreference.Name name,
                                                  @RequestParam String value) {
        userPreferenceService.createOrUpdateUserPreference(new UserPreference(name, value, userId));
        UserPreference userPreference = userPreferenceService.getUserPreferenceByNameAndUserId(name.name(), userId);
        return mapper.map(userPreference, UserPreferenceDTO.class);
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
