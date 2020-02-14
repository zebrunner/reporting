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
package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.UserPreference;
import com.qaprosoft.zafira.models.dto.UserPreferenceDTO;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import com.qaprosoft.zafira.models.dto.user.ChangePasswordDTO;
import com.qaprosoft.zafira.models.dto.user.UserType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Map;

@Api("Users API")
public interface UserDocumentedController {

    @ApiOperation(
            value = "Retrieves a user profile",
            notes = "Retrieves a user profile by the username or auth token if the username is not specified",
            nickname = "getUserProfile",
            httpMethod = "GET",
            response = UserType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "username", paramType = "query", dataType = "string", value = "The user name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found user", response = UserType.class),
            @ApiResponse(code = 404, message = "Indicates that the user does not exist", response = ErrorResponse.class)
    })
    UserType getUserProfile(String username);

    @ApiOperation(
            value = "Retrieves an extended user profile",
            notes = "An extended user profile includes user info, user default dashboards ids",
            nickname = "getExtendedUserProfile",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns collected extended user profile", response = Map.class)
    })
    Map<String, Object> getExtendedUserProfile();

    @ApiOperation(
            value = "Updates a user profile",
            notes = "Returns the updated user profile",
            nickname = "updateUserProfile",
            httpMethod = "PUT",
            response = UserType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userType", paramType = "body", dataType = "UserType", required = true, value = "The user to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated user profile", response = UserType.class),
            @ApiResponse(code = 404, message = "Indicates that user does not exist", response = ErrorResponse.class)
    })
    UserType updateUserProfile(UserType userType);

    @ApiOperation(
            value = "Deletes a user profile photo",
            notes = "Deletes a user profile photo from the storage",
            nickname = "deleteUserProfilePhoto",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The user profile photo was deleted successfully"),
            @ApiResponse(code = 400, message = "Indicates that the storage provider is not specified", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that the user does not exist", response = ErrorResponse.class)
    })
    void deleteUserProfilePhoto();

    @ApiOperation(
            value = "Updates a user password",
            notes = "Only the profile owner and admin have access to update user password",
            nickname = "updateUserPassword",
            httpMethod = "PUT"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "password", paramType = "body", dataType = "ChangePasswordDTO", required = true, value = "The reset password object")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "User password was updated successfully"),
            @ApiResponse(code = 400, message = "Indicates that it’s not the profile owner or admin who tries to update the password", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that user does not exist", response = ErrorResponse.class)
    })
    void updateUserPassword(ChangePasswordDTO password);

    @ApiOperation(
            value = "Searches for users by specified criteria",
            notes = "Returns found users",
            nickname = "searchUsers",
            httpMethod = "POST",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "searchCriteria", paramType = "body", dataType = "UserSearchCriteria", required = true, value = "Search criteria"),
            @ApiImplicitParam(name = "isPublic", paramType = "query", dataType = "boolean", value = "Indicates that the search will include only user public info")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found users", response = SearchResult.class)
    })
    SearchResult<User> searchUsers(UserSearchCriteria searchCriteria, boolean isPublic);

    @ApiOperation(
            value = "Creates or updates a user",
            notes = "Returns the created or updated user",
            nickname = "createOrUpdateUser",
            httpMethod = "PUT",
            response = UserType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userType", paramType = "body", dataType = "UserType", required = true, value = "The user to create or update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created or updated user", response = UserType.class)
    })
    UserType createOrUpdateUser(UserType userType);

    @ApiOperation(
            value = "Updates a user status",
            notes = "Activates or deactivates a user",
            nickname = "updateStatus",
            httpMethod = "PUT",
            response = UserType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userType", paramType = "body", dataType = "UserType", required = true, value = "The user to activate or deactivate")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the user with an updated status", response = UserType.class)
    })
    UserType updateStatus(UserType userType);

    @ApiOperation(
            value = "Adds a user to a group",
            notes = "Adds a user to a group by its id",
            nickname = "addUserToGroup",
            httpMethod = "PUT",
            response = User.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "user", paramType = "body", dataType = "User", required = true, value = "The user to add"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "The id of a group to attach")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the user with the group they are added to", response = User.class)
    })
    User addUserToGroup(User user, long id);

    @ApiOperation(
            value = "Deletes a user from a group",
            notes = "Detaches a user from a group",
            nickname = "deleteUserFromGroup",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "groupId", paramType = "path", dataType = "number", required = true, value = "The group id"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "number", required = true, value = "The user id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The user was detached successfully")
    })
    void deleteUserFromGroup(long groupId, long userId);

    @ApiOperation(
            value = "Retrieves default system user preferences",
            notes = "Returns found preferences",
            nickname = "getDefaultUserPreferences",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found preferences", response = List.class)
    })
    List<UserPreference> getDefaultUserPreferences();

    @ApiOperation(
            value = "Creates user preferences",
            notes = "Returns created preferences",
            nickname = "createUserPreference",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "number", required = true, value = "The user id"),
            @ApiImplicitParam(name = "preferences", paramType = "body", dataType = "array", required = true, value = "The preferences to attach")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns attached preferences", response = List.class)
    })
    List<UserPreference> createUserPreference(long userId, List<UserPreference> preferences);

    @ApiOperation(
            value = "Updates single user preference",
            notes = "Returns updated integration",
            nickname = "createUserPreference",
            httpMethod = "PUT",
            response = UserPreferenceDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The path reference id"),
            @ApiImplicitParam(name = "name", paramType = "query", dataType = "string", required = true, value = "User preference name"),
            @ApiImplicitParam(name = "value", paramType = "query", dataType = "string", required = true, value = "User preference value")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated user preference", response = UserPreferenceDTO.class)
    })
    UserPreferenceDTO createUserPreference(long userId, UserPreference.Name name, String value);

    @ApiOperation(
            value = "Resets current user preferences to default",
            notes = "Returns default preferences",
            nickname = "resetUserPreferencesToDefault",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns default preferences", response = List.class)
    })
    List<UserPreference> resetUserPreferencesToDefault();

    @ApiOperation(
            value = "Deletes user preferences",
            notes = "Deletes user preferences by the user id",
            nickname = "deleteUserPreferences",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The user id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "User preferences were deleted successfully")
    })
    void deleteUserPreferences(long userId);

}
