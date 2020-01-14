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
            value = "Retrieves user profile",
            notes = "Retrieves user profile by username or by auth token if username does not specified",
            nickname = "getUserProfile",
            httpMethod = "GET",
            response = UserType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "username", paramType = "query", dataType = "string", value = "User username")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found user", response = UserType.class),
            @ApiResponse(code = 404, message = "Indicates that user does not exist", response = ErrorResponse.class)
    })
    UserType getUserProfile(String username);

    @ApiOperation(
            value = "Retrieve extended user profile",
            notes = "Extended user profile includes user info, user default dashboards ids",
            nickname = "getExtendedUserProfile",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns collected extended user profile", response = Map.class)
    })
    Map<String, Object> getExtendedUserProfile();

    @ApiOperation(
            value = "Updates user profile",
            notes = "Returns updated user profile",
            nickname = "updateUserProfile",
            httpMethod = "PUT",
            response = UserType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "userType", paramType = "body", dataType = "UserType", required = true, value = "User to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated user profile", response = UserType.class),
            @ApiResponse(code = 404, message = "Indicates that user does not exist", response = ErrorResponse.class)
    })
    UserType updateUserProfile(UserType userType);

    @ApiOperation(
            value = "Deletes user profile photo",
            notes = "Deletes user profile photo from storage",
            nickname = "deleteUserProfilePhoto",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "User profile photo was deleted successfully"),
            @ApiResponse(code = 400, message = "Indicates that storage provider does not specified", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that user does not exist", response = ErrorResponse.class)
    })
    void deleteUserProfilePhoto();

    @ApiOperation(
            value = "Updates user password",
            notes = "Access to update user password have profile owner and admin",
            nickname = "updateUserPassword",
            httpMethod = "PUT"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "password", paramType = "body", dataType = "ChangePasswordDTO", required = true, value = "Reset password object")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "User password was updated successfully"),
            @ApiResponse(code = 400, message = "Indicates that not profile owner or admin tries to update password", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that user does not exist", response = ErrorResponse.class)
    })
    void updateUserPassword(ChangePasswordDTO password);

    @ApiOperation(
            value = "Search users using criteria",
            notes = "Returns found users",
            nickname = "searchUsers",
            httpMethod = "POST",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "searchCriteria", paramType = "body", dataType = "UserSearchCriteria", required = true, value = "Search criteria"),
            @ApiImplicitParam(name = "isPublic", paramType = "query", dataType = "boolean", value = "Indicates that need to search users public info only")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found users", response = SearchResult.class)
    })
    SearchResult<User> searchUsers(UserSearchCriteria searchCriteria, boolean isPublic);

    @ApiOperation(
            value = "Creates or updates user",
            notes = "Returns created or updated user",
            nickname = "createOrUpdateUser",
            httpMethod = "PUT",
            response = UserType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "userType", paramType = "body", dataType = "UserType", required = true, value = "User to create or update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created or updated user", response = UserType.class)
    })
    UserType createOrUpdateUser(UserType userType);

    @ApiOperation(
            value = "Updates user status",
            notes = "Activates or deactivates user",
            nickname = "updateStatus",
            httpMethod = "PUT",
            response = UserType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "userType", paramType = "body", dataType = "UserType", required = true, value = "User to activate or deactivate")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns user with updated status", response = UserType.class)
    })
    UserType updateStatus(UserType userType);

    @ApiOperation(
            value = "Adds user to group",
            notes = "Adds user to group by id",
            nickname = "addUserToGroup",
            httpMethod = "PUT",
            response = User.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "user", paramType = "body", dataType = "User", required = true, value = "User to add"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Group id to attache")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns user with added group", response = User.class)
    })
    User addUserToGroup(User user, long id);

    @ApiOperation(
            value = "Deletes user from group",
            notes = "Detaches user from group",
            nickname = "deleteUserFromGroup",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "groupId", paramType = "path", dataType = "number", required = true, value = "Group id"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "number", required = true, value = "User id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "User was detached successfully")
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
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
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
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "number", required = true, value = "User id"),
            @ApiImplicitParam(name = "preferences", paramType = "body", dataType = "array", required = true, value = "Preferences to attache")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns attached preferences", response = List.class)
    })
    List<UserPreference> createUserPreference(long userId, List<UserPreference> preferences);

    @ApiOperation(
            value = "Resets current user preferences to default",
            notes = "Returns default preferences",
            nickname = "resetUserPreferencesToDefault",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns default preferences", response = List.class)
    })
    List<UserPreference> resetUserPreferencesToDefault();

    @ApiOperation(
            value = "Deletes user preferences",
            notes = "Deleted user preferences by user id",
            nickname = "deleteUserPreferences",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "number", required = true, value = "User id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "User preferences was deleted successfully")
    })
    void deleteUserPreferences(long userId);

}
