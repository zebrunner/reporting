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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.models.db.Invitation;
import com.qaprosoft.zafira.models.dto.auth.InvitationListType;
import com.qaprosoft.zafira.models.dto.auth.InvitationType;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@Api("Invites API")
public interface InvitationDocumentedController {

    @ApiOperation(
            value = "Batch invites users to signup",
            notes = "Returns created invites",
            nickname = "inviteUsers",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "invitationList", paramType = "body", dataType = "InvitationListType", required = true, value = "List to invite")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created invitations", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that username or invitation already exist", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Indicates that user cannot be invited into private group", response = ErrorResponse.class)
    })
    List<Invitation> inviteUsers(InvitationListType invitationList);

    @ApiOperation(
            value = "Re-sends invitation email",
            notes = "Re-sends invitation email. Old link will be expired",
            nickname = "retryInviteUser",
            httpMethod = "POST",
            response = Invitation.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "invitation", paramType = "body", dataType = "InvitationType", required = true, value = "Invitation to re-send")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated invitation", response = Invitation.class),
            @ApiResponse(code = 400, message = "Indicates that invitation was accepted already", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that previous invitation cannot be found", response = ErrorResponse.class)
    })
    Invitation retryInviteUser(InvitationType invitation);

    @ApiOperation(
            value = "Retrieves invitation by invitation token (pending only)",
            notes = "Token is generated on invitation creation",
            nickname = "getInvitation",
            httpMethod = "GET",
            response = InvitationType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "token", paramType = "query", dataType = "string", required = true, value = "Invitation token")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found invitation", response = InvitationType.class),
            @ApiResponse(code = 400, message = "Indicates that invitation was accepted already", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that previous invitation cannot be found", response = ErrorResponse.class)
    })
    InvitationType getInvitation(String token);

    @ApiOperation(
            value = "Retrieves all invitations",
            notes = "Returns found invitations",
            nickname = "getAllInvitations",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found invitations", response = List.class)
    })
    List<Invitation> getAllInvitations();

    @ApiOperation(
            value = "Retrieves invitations using keyword with pagination",
            notes = "Returns found invitations",
            nickname = "search",
            httpMethod = "GET",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "query", paramType = "query", dataType = "string", value = "Search keyword. Used for search by email part, source or status"),
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "number", value = "Page number"),
            @ApiImplicitParam(name = "pageSize", paramType = "query", dataType = "number", value = "Invitations per page count"),
            @ApiImplicitParam(name = "orderBy", paramType = "query", dataType = "string", value = "Order by id only"),
            @ApiImplicitParam(name = "sortOrder", paramType = "query", dataType = "string", value = "Sorting direction (ASC or DESC)"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found invitastions", response = SearchResult.class)
    })
    SearchResult<Invitation> search(String query, String page, String pageSize, String orderBy, String sortOrder, SearchCriteria sc);

    @ApiOperation(
            value = "Deletes invitations by id or email",
            notes = "Recognizes delete strategy by path variable type. Invited user cannot make use invitation link anymore",
            nickname = "deleteInvitation",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invitation was deleted successfully")
    })
    void deleteInvitation(String idOrEmail);

}
