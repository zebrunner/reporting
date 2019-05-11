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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.models.db.Invitation;
import com.qaprosoft.zafira.models.dto.auth.InvitationListType;
import com.qaprosoft.zafira.models.dto.auth.InvitationType;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.InvitationService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api("Invites API")
@CrossOrigin
@RequestMapping(path = "api/invitations", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class InvitationAPIController extends AbstractController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Invite users", nickname = "inviteUsers", httpMethod = "POST", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('INVITE_USERS')")
    @PostMapping()
    public List<Invitation> inviteUsers(@Valid @RequestBody InvitationListType invitationList) throws ServiceException {
        Invitation[] invitations = invitationList.getInvitationTypes().stream()
                .map(invitationType -> mapper.map(invitationType, Invitation.class))
                .toArray(Invitation[]::new);
        return invitationService.createInvitations(getPrincipalId(), invitations);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Retry invite user", nickname = "retryInviteUser", httpMethod = "POST", response = Invitation.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('INVITE_USERS')")
    @PostMapping("/retry")
    public Invitation retryInviteUser(@Valid @RequestBody InvitationType invitation) throws ServiceException {
        return invitationService.retryInvitation(getPrincipalId(), invitation.getEmail());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get invitation", nickname = "getInvitation", httpMethod = "GET", response = InvitationType.class)
    @GetMapping("/info")
    public InvitationType getInvitation(@RequestParam("token") String token) throws ServiceException {
        Invitation invitation = invitationService.getInvitationByToken(token);
        if (invitation == null || !invitation.isValid()) {
            throw new ForbiddenOperationException();
        }
        return mapper.map(invitation, InvitationType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all invitations", nickname = "getAllInvitations", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAnyPermission('INVITE_USERS', 'MODIFY_INVITATIONS')")
    @GetMapping("/all")
    public List<Invitation> getAllInvitations() throws ServiceException {
        return invitationService.getAllInvitations();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Search invitations", nickname = "searchInvitations", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAnyPermission('INVITE_USERS', 'MODIFY_INVITATIONS')")
    @GetMapping(value = "/search")
    public SearchResult<Invitation> search(@RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "pageSize", required = false) String pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder,
            SearchCriteria sc) throws ServiceException {
        return invitationService.search(sc);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete invitation", nickname = "deleteInvitationById", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INVITATIONS')")
    @DeleteMapping("/{idOrEmail}")
    public void deleteInvitation(@PathVariable("idOrEmail") String idOrEmail) throws ServiceException {
        if (idOrEmail.matches("\\d+")) { // check if number
            invitationService.deleteInvitation(Long.valueOf(idOrEmail));
        } else { // otherwise treat as email
            invitationService.deleteInvitation(idOrEmail);
        }
    }

}
