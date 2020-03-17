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
package com.zebrunner.reporting.web;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchCriteria;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.domain.db.Invitation;
import com.zebrunner.reporting.domain.dto.auth.InvitationListType;
import com.zebrunner.reporting.domain.dto.auth.InvitationType;
import com.zebrunner.reporting.service.InvitationService;
import com.zebrunner.reporting.web.documented.InvitationDocumentedController;
import org.dozer.Mapper;
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
import java.util.stream.Collectors;

@CrossOrigin
@RequestMapping(path = "api/invitations", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class InvitationController extends AbstractController implements InvitationDocumentedController {

    private final InvitationService invitationService;
    private final Mapper mapper;

    public InvitationController(InvitationService invitationService, Mapper mapper) {
        this.invitationService = invitationService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('INVITE_USERS')")
    @PostMapping()
    @Override
    public List<Invitation> inviteUsers(@Valid @RequestBody InvitationListType invitationList) {
        List<InvitationType> invitationTypes = invitationList.getInvitationTypes();
        List<Invitation> invitations = invitationTypes.stream()
                                                  .map(invitationType -> mapper.map(invitationType, Invitation.class))
                                                  .collect(Collectors.toList());
        return invitationService.createInvitations(getPrincipalId(), invitations);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('INVITE_USERS')")
    @PostMapping("/retry")
    @Override
    public Invitation retryInviteUser(@Valid @RequestBody InvitationType invitation) {
        return invitationService.retryInvitation(getPrincipalId(), invitation.getEmail());
    }

    @GetMapping("/info")
    @Override
    public InvitationType getInvitation(@RequestParam("token") String token) {
        Invitation invitation = invitationService.getInvitationByToken(token);
        return mapper.map(invitation, InvitationType.class);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAnyPermission('INVITE_USERS', 'MODIFY_INVITATIONS')")
    @GetMapping("/all")
    @Override
    public List<Invitation> getAllInvitations() {
        return invitationService.getAllInvitations();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAnyPermission('INVITE_USERS', 'MODIFY_INVITATIONS')")
    @GetMapping(value = "/search")
    @Override
    public SearchResult<Invitation> search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "pageSize", required = false) String pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder,
            SearchCriteria sc
    ) {
        return invitationService.search(sc);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INVITATIONS')")
    @DeleteMapping("/{idOrEmail}")
    @Override
    public void deleteInvitation(@PathVariable("idOrEmail") String idOrEmail) {
        if (idOrEmail.matches("\\d+")) { // check if number
            invitationService.deleteInvitation(Long.valueOf(idOrEmail));
        } else { // otherwise treat as email
            invitationService.deleteInvitation(idOrEmail);
        }
    }

}
