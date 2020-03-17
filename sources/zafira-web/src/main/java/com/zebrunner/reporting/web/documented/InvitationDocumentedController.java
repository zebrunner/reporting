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
package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchCriteria;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.domain.db.Invitation;
import com.zebrunner.reporting.domain.dto.auth.InvitationListType;
import com.zebrunner.reporting.domain.dto.auth.InvitationType;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
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
            value = "Invites a batch of users to sign up",
            notes = "Returns created invitations",
            nickname = "inviteUsers",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "invitationList", paramType = "body", dataType = "InvitationListType", required = true, value = "A list of users to invite")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created invitations", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that the username or the invitation already exist", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Indicates that the user cannot be invited to a private group", response = ErrorResponse.class)
    })
    List<Invitation> inviteUsers(InvitationListType invitationList);

    @ApiOperation(
            value = "Resends an invitation email",
            notes = "Resends an invitation email. The old link expires (after that)",
            nickname = "retryInviteUser",
            httpMethod = "POST",
            response = Invitation.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "invitation", paramType = "body", dataType = "InvitationType", required = true, value = "The invitation to resend")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated invitation", response = Invitation.class),
            @ApiResponse(code = 400, message = "Indicates that the invitation has already been accepted", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that the previous invitation cannot be found", response = ErrorResponse.class)
    })
    Invitation retryInviteUser(InvitationType invitation);

    @ApiOperation(
            value = "Retrieves an invitation by the invitation token (pending only)",
            notes = "A token is generated while creating an invitation",
            nickname = "getInvitation",
            httpMethod = "GET",
            response = InvitationType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "token", paramType = "query", dataType = "string", required = true, value = "The invitation token")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found invitation", response = InvitationType.class),
            @ApiResponse(code = 400, message = "Indicates that the invitation has already been accepted", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that the previous invitation cannot be found", response = ErrorResponse.class)
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
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found invitations", response = List.class)
    })
    List<Invitation> getAllInvitations();

    @ApiOperation(
            value = "Retrieves a paginated set of invitations by using a keyword",
            notes = "Returns found invitations",
            nickname = "search",
            httpMethod = "GET",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "query", paramType = "query", dataType = "string", value = "A search keyword. The search is made by the source, status or part of the email"),
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "string", value = "The page number"),
            @ApiImplicitParam(name = "pageSize", paramType = "query", dataType = "string", value = "The number of invitations per page"),
            @ApiImplicitParam(name = "orderBy", paramType = "query", dataType = "string", value = "The order by id only"),
            @ApiImplicitParam(name = "sortOrder", paramType = "query", dataType = "string", value = "The sorting direction (ASC or DESC)"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found invitations", response = SearchResult.class)
    })
    SearchResult<Invitation> search(String query, String page, String pageSize, String orderBy, String sortOrder, SearchCriteria sc);

    @ApiOperation(
            value = "Deletes an invitation by the user id or email",
            notes = "Deletes an invitation by the user id or email. The invited user cannot use the invitation link anymore",
            nickname = "deleteInvitation",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "idOrEmail", paramType = "path", required = true, value = "The id or email of an invited user")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The invitation was deleted successfully")
    })
    void deleteInvitation(String idOrEmail);

}
