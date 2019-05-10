/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
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
package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.InvitationMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Invitation;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.services.exceptions.EntityAlreadyExistsException;
import com.qaprosoft.zafira.services.exceptions.EntityNotExistsException;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.emails.IEmailMessage;
import com.qaprosoft.zafira.services.services.application.emails.UserInviteEmail;
import com.qaprosoft.zafira.services.services.application.emails.UserInviteLdapEmail;
import com.qaprosoft.zafira.services.util.URLResolver;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.qaprosoft.zafira.models.db.Group.Role.ROLE_ADMIN;
import static com.qaprosoft.zafira.models.db.User.Source.LDAP;

@Service
public class InvitationService {

    private static final Logger LOGGER = Logger.getLogger(InvitationService.class);

    private final String zafiraLogoURL;
    private final URLResolver urlResolver;
    private final InvitationMapper invitationMapper;
    private final EmailService emailService;
    private final UserService userService;
    private final GroupService groupService;

    public InvitationService(@Value("${zafira.slack.image}") String zafiraLogoURL,
                             URLResolver urlResolver,
                             InvitationMapper invitationMapper,
                             EmailService emailService,
                             UserService userService,
                             GroupService groupService) {
        this.zafiraLogoURL = zafiraLogoURL;
        this.urlResolver = urlResolver;
        this.invitationMapper = invitationMapper;
        this.emailService = emailService;
        this.userService = userService;
        this.groupService = groupService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Invitation createInvitation(Long principalId, Invitation invitation, boolean checkExisting, boolean force) throws ServiceException {
        if (checkExisting) {
            checkExisting(invitation.getEmail());
        }
        Group group = groupService.getGroupById(invitation.getGroupId());
        if (group == null) {
            throw new EntityNotExistsException(Group.class, false);
        }
        if (!group.getInvitable() && !force) {
            throw new ForbiddenOperationException("Cannot invite users to not invitable group '" + group.getName() + "'");
        }
        String token = generateToken();
        invitation.setToken(token);
        invitation.setCreatedBy(userService.getUserById(principalId));
        invitation.setStatus(Invitation.Status.PENDING);
        invitationMapper.createInvitation(invitation);
        return invitation;
    }

    @SuppressWarnings("rawtypes")
    private CompletableFuture[] createInvitationsAsync(Long principalId, List<Invitation> results, Invitation... invitations) {
        return Arrays.stream(invitations).map(invitation -> CompletableFuture.supplyAsync(() -> {
            Invitation inv = null;
            try {
                inv = createInvitation(principalId, invitation, false, false);
                sendEmail(inv);
            } catch (ServiceException e) {
                LOGGER.error(e.getMessage(), e);
            }
            results.add(inv);
            return inv;
        })).toArray(CompletableFuture[]::new);
    }

    public List<Invitation> createInvitations(Long principalId, Invitation... invitations) throws ServiceException {
        List<Invitation> result = new ArrayList<>();
        for(Invitation invitation : invitations) {
            checkExisting(invitation.getEmail());
        }
        CompletableFuture.allOf(createInvitationsAsync(principalId, result, invitations)).join();
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Invitation createInitialInvitation(String email, String groupName) throws ServiceException {
        Invitation invitation = null;
        if (!StringUtils.isBlank(userService.getAdminUsername())) {
            User user = userService.getUserByUsername(userService.getAdminUsername());
            if(user == null) {
                throw new ForbiddenOperationException("Admin with username '" + userService.getAdminUsername() + "' does not exist");
            }

            Group group = groupService.getGroupByName(groupName);
            if(group == null) {
                throw new ForbiddenOperationException("Group by role '" + ROLE_ADMIN.name() + "' does not exist");
            }

            invitation = new Invitation();
            invitation.setGroupId(group.getId());
            invitation.setSource(User.Source.INTERNAL);
            invitation.setEmail(email);
            invitation = createInvitation(user.getId(), invitation, true, true);
        }
        return invitation;
    }

    @Transactional(rollbackFor = Exception.class)
    public Invitation retryInvitation(Long principalId, String email) throws ServiceException {
        Invitation invitationFromDb = getInvitationByEmail(email);
        if(invitationFromDb == null) {
            throw new EntityNotExistsException(Invitation.class, false);
        }
        if(invitationFromDb.getStatus().equals(Invitation.Status.ACCEPTED)) {
            throw new ServiceException("Cannot retry invitation due invitation is accepted yet.");
        }
        String token = generateToken();
        invitationFromDb.setToken(token);
        invitationFromDb.setCreatedBy(userService.getUserById(principalId));
        invitationFromDb = updateInvitation(invitationFromDb);
        sendEmail(invitationFromDb);
        return invitationFromDb;
    }

    @Transactional(readOnly = true)
    public Invitation getInvitationByToken(String token) {
        return invitationMapper.getInvitationByCode(token);
    }

    @Transactional(readOnly = true)
    public Invitation getInvitationByEmail(String email) {
        return invitationMapper.getInvitationByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Invitation> getAllInvitations() {
        return invitationMapper.getAllInvitations();
    }

    @Transactional(readOnly = true)
    public SearchResult<Invitation> search(SearchCriteria sc) {
        List<Invitation> invitations = invitationMapper.search(sc);
        Integer totalCount = invitationMapper.searchCount(sc);

        SearchResult<Invitation> sr = new SearchResult<>();
        sr.setPage(sc.getPage());
        sr.setPageSize(sc.getPageSize());
        sr.setSortOrder(sc.getSortOrder());

        sr.setResults(invitations);
        sr.setTotalResults(totalCount);
        return sr;
    }

    @Transactional(rollbackFor = Exception.class)
    public Invitation updateInvitation(Invitation invitation) {
        invitationMapper.updateInvitation(invitation);
        return invitation;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteInvitation(Long id) {
        invitationMapper.deleteInvitationById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteInvitation(String email) {
        invitationMapper.deleteInvitationByEmail(email);
    }

    private String generateToken() {
        return RandomStringUtils.randomAlphanumeric(50);
    }

    private void checkExisting(String email) throws ServiceException {
        if(userService.getUserByEmail(email) != null) {
            throw new EntityAlreadyExistsException("email", email, User.class, false);
        } else if(getInvitationByEmail(email) != null) {
            throw new EntityAlreadyExistsException("email", email, Invitation.class, false);
        }
    }

    private void sendEmail(Invitation invitation) throws ServiceException {
        IEmailMessage userInviteEmail = LDAP.equals(invitation.getSource()) ?
                new UserInviteEmail(invitation.getToken(), zafiraLogoURL, urlResolver.buildWebURL()) :
                new UserInviteLdapEmail(invitation.getToken(), zafiraLogoURL, urlResolver.buildWebURL());
        emailService.sendEmail(userInviteEmail, invitation.getEmail());
    }

}
