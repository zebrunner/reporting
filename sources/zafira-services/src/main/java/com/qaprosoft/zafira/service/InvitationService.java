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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.InvitationMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Invitation;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.service.email.IEmailMessage;
import com.qaprosoft.zafira.service.email.UserInviteEmail;
import com.qaprosoft.zafira.service.email.UserInviteLdapEmail;
import com.qaprosoft.zafira.service.exception.ExternalSystemException;
import com.qaprosoft.zafira.service.exception.ForbiddenOperationException;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import com.qaprosoft.zafira.service.integration.tool.impl.AccessManagementService;
import com.qaprosoft.zafira.service.util.URLResolver;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.models.db.Group.Role.ROLE_ADMIN;
import static com.qaprosoft.zafira.models.db.User.Source.LDAP;
import static com.qaprosoft.zafira.service.exception.ExternalSystemException.IllegalOperationErrorDetail.LDAP_USER_DOES_NOT_EXIST;
import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.INVITATION_CAN_NOT_BE_CREATED;
import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.INVITATION_IS_INVALID;
import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.USER_CAN_NOT_BE_CREATED;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.GROUP_NOT_FOUND;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.INVITATION_NOT_FOUND;

@Service
public class InvitationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationService.class);

    private static final String ERR_MSG_INVITATION_FOR_EMAIL_NOT_FOUND = "Invitation for email %s can not be found";
    private static final String ERR_MSG_INVITATION_FOR_TOKEN_NOT_FOUND = "Invitation for tokrn %s can not be found";
    private static final String ERR_MSG_USER_ALREADY_EXISTS = "User with such email already exists";
    private static final String ERR_MSG_INVITATION_ALREADY_EXISTS = "User with such email was invited already";
    private static final String ERR_MSG_INVITATION_CAN_NOT_BE_RETRIED = "Invitation was already accepted, can not retry";
    private static final String ERR_MSG_INVITATION_STATUS_IS_INCORRECT = "Invitation status for token %s is null or incorrect";
    private static final String ERR_MSG_USER_NOT_FOUND_IN_LDAP = "User with username %s is not found in LDAP";

    private final String zafiraLogoURL;
    private final URLResolver urlResolver;
    private final InvitationMapper invitationMapper;
    private final EmailService emailService;
    private final UserService userService;
    private final GroupService groupService;
    private final AccessManagementService accessManagementService;


    public InvitationService(@Value("${zafira.slack.image-url}") String zafiraLogoURL,
                             URLResolver urlResolver,
                             InvitationMapper invitationMapper,
                             EmailService emailService,
                             UserService userService,
                             GroupService groupService,
                             AccessManagementService accessManagementService) {
        this.zafiraLogoURL = zafiraLogoURL;
        this.urlResolver = urlResolver;
        this.invitationMapper = invitationMapper;
        this.emailService = emailService;
        this.userService = userService;
        this.groupService = groupService;
        this.accessManagementService = accessManagementService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Invitation createInvitation(Long principalId, Invitation invitation, boolean checkExisting, boolean force) {
        if (checkExisting) {
            checkExisting(invitation.getEmail());
        }
        Long groupId = invitation.getGroupId();
        Group group = groupService.getGroupById(groupId);
        if (group == null) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND, "Group with id %s does not exists", groupId);
        }
        if (!group.getInvitable() && !force) {
            throw new ForbiddenOperationException("Cannot invite users to not invitable group '" + group.getName() + "'");
        }
        String token = generateToken();
        invitation.setToken(token);
        invitation.setCreatedBy(userService.getUserById(principalId));
        invitation.setStatus(Invitation.Status.PENDING);
        invitationMapper.createInvitation(invitation);

        insertInvitationUrl(invitation);
        return invitation;
    }

    @SuppressWarnings("rawtypes")
    private CompletableFuture[] createInvitationsAsync(Long principalId, List<Invitation> results, List<Invitation> invitations) {
        return invitations.stream().map(invitation -> CompletableFuture.supplyAsync(() -> {
            Invitation inv = null;
            try {
                inv = createInvitation(principalId, invitation, false, false);
                sendEmail(inv);
                // TODO by nsidorevich on 2019-09-03: ???
            } catch (RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
            }
            results.add(inv);
            return inv;
        })).toArray(CompletableFuture[]::new);
    }

    public List<Invitation> createInvitations(Long principalId, List<Invitation> invitations) {
        List<Invitation> result = new ArrayList<>();
        invitations.forEach(invitation -> checkExisting(invitation.getEmail()));
        CompletableFuture.allOf(createInvitationsAsync(principalId, result, invitations)).join();
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Invitation createInitialInvitation(String email, String groupName) {
        Invitation invitation = null;
        if (!StringUtils.isBlank(userService.getAdminUsername())) {
            User user = userService.getUserByUsername(userService.getAdminUsername());
            if (user == null) {
                throw new ForbiddenOperationException("Admin with username '" + userService.getAdminUsername() + "' does not exist");
            }

            Group group = groupService.getGroupByName(groupName);
            if (group == null) {
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
    public Invitation retryInvitation(Long inviterId, String email) {
        Invitation invitation = getInvitationByEmail(email);

        if (invitation == null) {
            throw new ResourceNotFoundException(INVITATION_NOT_FOUND, ERR_MSG_INVITATION_FOR_EMAIL_NOT_FOUND, email);
        }
        if (invitation.getStatus().equals(Invitation.Status.ACCEPTED)) {
            throw new IllegalOperationException(INVITATION_CAN_NOT_BE_CREATED, ERR_MSG_INVITATION_CAN_NOT_BE_RETRIED);
        }

        invitation.setToken(generateToken());
        invitation.setCreatedBy(userService.getUserById(inviterId));
        invitation = updateInvitation(invitation);

        sendEmail(invitation);

        insertInvitationUrl(invitation);
        return invitation;
    }

    @Transactional(readOnly = true)
    public Invitation acceptInvitation(String token, String username) {
        if (userService.getUserByUsername(username) != null) {
            throw new IllegalOperationException(USER_CAN_NOT_BE_CREATED, ERR_MSG_USER_ALREADY_EXISTS);
        }
        Invitation invitation = getInvitationByToken(token);
        if (invitation.getSource().equals(User.Source.LDAP)) {
            boolean ldapEnabled = accessManagementService.isEnabledAndConnected(null);
            boolean userExists = accessManagementService.isUserExists(username);
            if (ldapEnabled && !userExists) {
                throw new ExternalSystemException(LDAP_USER_DOES_NOT_EXIST, ERR_MSG_USER_NOT_FOUND_IN_LDAP, username);
            }
        }
        invitation.setStatus(Invitation.Status.ACCEPTED);
        updateInvitation(invitation);
        return invitation;
    }

    @Transactional(readOnly = true)
    public Invitation getInvitationByToken(String token) {
        Invitation invitation = invitationMapper.getInvitationByCode(token);
        if (invitation == null) {
            throw new ResourceNotFoundException(INVITATION_NOT_FOUND, ERR_MSG_INVITATION_FOR_TOKEN_NOT_FOUND, token);
        }
        if (!invitation.isValid()) {
            throw new IllegalOperationException(INVITATION_IS_INVALID, ERR_MSG_INVITATION_STATUS_IS_INCORRECT, token);
        }
        insertInvitationUrl(invitation);
        return invitation;
    }

    @Transactional(readOnly = true)
    public Invitation getInvitationByEmail(String email) {
        Invitation invitation = invitationMapper.getInvitationByEmail(email);
        insertInvitationUrl(invitation);
        return invitation;
    }

    @Transactional(readOnly = true)
    public List<Invitation> getAllInvitations() {
        return invitationMapper.getAllInvitations().stream()
                               .peek(this::insertInvitationUrl)
                               .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SearchResult<Invitation> search(SearchCriteria sc) {
        List<Invitation> invitations = invitationMapper.search(sc);
        invitations = invitations.stream()
                                 .peek(this::insertInvitationUrl)
                                 .collect(Collectors.toList());
        Integer count = invitationMapper.searchCount(sc);

        return SearchResult.<Invitation>builder()
                .page(sc.getPage())
                .pageSize(sc.getPageSize())
                .sortOrder(sc.getSortOrder())
                .results(invitations)
                .totalResults(count)
                .build();
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

    private void checkExisting(String email) {
        if (userService.getUserByEmail(email) != null) {
            throw new IllegalOperationException(INVITATION_CAN_NOT_BE_CREATED, ERR_MSG_USER_ALREADY_EXISTS);
        } else if (getInvitationByEmail(email) != null) {
            throw new IllegalOperationException(INVITATION_CAN_NOT_BE_CREATED, ERR_MSG_INVITATION_ALREADY_EXISTS);
        }
    }

    private void sendEmail(Invitation invitation) {
        IEmailMessage userInviteEmail = LDAP.equals(invitation.getSource())
                ? new UserInviteEmail(invitation.getUrl(), zafiraLogoURL, urlResolver.buildWebURL())
                : new UserInviteLdapEmail(invitation.getUrl(), zafiraLogoURL, urlResolver.buildWebURL());
        emailService.sendEmail(userInviteEmail, invitation.getEmail());
    }

    private void insertInvitationUrl(Invitation invitation) {
        if (invitation != null) {
            invitation.setUrl(urlResolver.buildInvitationUrl(invitation.getToken()));
        }
    }

}
