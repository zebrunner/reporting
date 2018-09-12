package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.InvitationMapper;
import com.qaprosoft.zafira.models.db.Invitation;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.services.exceptions.EntityIsAlreadyExistsException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.emails.UserInviteEmail;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class InvitationService {

    private static final Logger LOGGER = Logger.getLogger(InvitationService.class);

    @Value("${zafira.webservice.url}")
    private String wsURL;

    @Autowired
    private InvitationMapper invitationMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public Invitation createInvitation(Invitation invitation, boolean checkExisting) throws ServiceException {
        if(checkExisting) {
            checkExisting(invitation.getEmail());
        }
        String token = generateToken();
        invitation.setToken(token);
        invitation.setExpiresIn(getExpiresIn());
        invitationMapper.createInvitation(invitation);
        UserInviteEmail userInviteEmail = new UserInviteEmail(token, "https://topolio.s3-eu-west-1.amazonaws.com/uploads/5b9671a3991bb/1536586284.jpg", "https://topolio.s3-eu-west-1.amazonaws.com/uploads/5b9671a3991bb/1536586284.jpg", "https://ua.qaprosoft.cloud/zafira", wsURL);
        emailService.sendEmail(userInviteEmail, invitation.getEmail());
        return invitation;
    }

    private CompletableFuture[] createInvitationsAsync(List<Invitation> results, Invitation... invitations) {
        return Arrays.stream(invitations).map(invitation -> CompletableFuture.supplyAsync(() -> {
            Invitation inv = null;
            try {
                inv = createInvitation(invitation, false);
            } catch (ServiceException e) {
                LOGGER.error(e.getMessage(), e);
            }
            results.add(inv);
            return inv;
        })).toArray(CompletableFuture[]::new);
    }

    public List<Invitation> createInvitations(Invitation... invitations) throws Exception {
        List<Invitation> result = new ArrayList<>();
        for(Invitation invitation : invitations) {
            checkExisting(invitation.getEmail());
        }
        CompletableFuture.allOf(createInvitationsAsync(result, invitations)).join();
        return result;
    }

    @Transactional(readOnly = true)
    public Invitation getInvitationByToken(String token) {
        return invitationMapper.getInvitationByCode(token);
    }

    @Transactional(readOnly = true)
    public List<Invitation> getAllInvitations() {
        return invitationMapper.getAllInvitations();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteInvitation(Long id) {
        invitationMapper.deleteInvitation(id);
    }

    private Date getExpiresIn() {
        return DateUtils.addHours(getCurrentUTCDate(), 48);
    }

    private Date getCurrentUTCDate() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
    }

    private String generateToken() {
        String token;
        while(getInvitationByToken(token = RandomStringUtils.randomAlphanumeric(50)) != null) {}
        return token;
    }

    private void checkExisting(String email) throws ServiceException {
        if(userService.getUserByEmail(email) != null) {
            throw new EntityIsAlreadyExistsException("email", email, User.class);
        }
    }
}
