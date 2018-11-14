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
package com.qaprosoft.zafira.ws.controller.application;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@Api(value = "Invites API")
@CrossOrigin
@RequestMapping("api/invitations")
public class InvitationAPIController extends AbstractController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Invite users", nickname = "inviteUsers", httpMethod = "POST", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('INVITE_USERS')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Invitation> inviteUsers(@Valid @RequestBody InvitationListType invitations) throws ServiceException {
        return invitationService.createInvitations(getPrincipalId(), invitations.getInvitationTypes().stream().map(invitationType -> mapper.map(invitationType, Invitation.class)).toArray(Invitation[]::new));
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Retry invite user", nickname = "retryInviteUser", httpMethod = "POST", response = Invitation.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('INVITE_USERS')")
    @RequestMapping(value = "retry", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Invitation retryInviteUser(@Valid @RequestBody InvitationType invitation) throws ServiceException {
        return invitationService.retryInvitation(getPrincipalId(), invitation.getEmail());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get invitation", nickname = "getInvitation", httpMethod = "GET", response = InvitationType.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody InvitationType getInvitation(@RequestParam(value = "token") String token) throws ServiceException {
        Invitation invitation = invitationService.getInvitationByToken(token);
        if(invitation == null || ! invitation.isValid()) {
            throw new ForbiddenOperationException();
        }
        return mapper.map(invitation, InvitationType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all invitations", nickname = "getAllInvitations", httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAnyPermission('INVITE_USERS', 'MODIFY_INVITATIONS')")
    @RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Invitation> getAllInvitations() throws ServiceException {
        return invitationService.getAllInvitations();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete invitation", nickname = "deleteInvitationById", httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INVITATIONS')")
    @RequestMapping(value = "{idOrEmail}", method = RequestMethod.DELETE)
    public void deleteInvitation(@PathVariable(value = "idOrEmail") String idOrEmail) throws ServiceException {
        if(isNumber(idOrEmail)) {
            invitationService.deleteInvitation(Long.valueOf(idOrEmail));
        } else {
            invitationService.deleteInvitation(idOrEmail);
        }
    }
}
