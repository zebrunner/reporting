package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.service.exception.AuthException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.qaprosoft.zafira.service.exception.AuthException.AuthErrorDetail.ADMIN_CREDENTIALS_INVALID;
import static com.qaprosoft.zafira.service.exception.AuthException.AuthErrorDetail.INVALID_USER_CREDENTIALS;
import static com.qaprosoft.zafira.service.exception.AuthException.AuthErrorDetail.USER_BELONGS_TO_OTHER_TENANT;
import static com.qaprosoft.zafira.service.exception.AuthException.AuthErrorDetail.USER_INACTIVE;

@Service
public class AuthService {

    private static final String ERR_MSG_INVALID_CREDENTIALS = "Invalid credentials for user %s";
    private static final String ERR_MSG_USER_IS_INACTIVE = "User id %d is inactive";
    private static final String ERR_MSG_USER_IS_FROM_OTHER_TENANT = "User with id %d is from tenant %s";
    private static final String ERR_MSG_USERNAME_OR_PASSWORD_IS_INVALID = "Username or password for user %s is invalid";

    private final AuthenticationManager authenticationInternalManager;
    private final AuthenticationManager authenticationLdapManager;
    private final UserService userService;
    private final String adminUsername;

    public AuthService(AuthenticationManager authenticationInternalManager,
                       AuthenticationManager authenticationLdapManager,
                       UserService userService,
                       @Value("${zafira.admin.username}") String adminUsername) {
        this.authenticationInternalManager = authenticationInternalManager;
        this.authenticationLdapManager = authenticationLdapManager;
        this.userService = userService;
        this.adminUsername = adminUsername;
    }

    public Authentication getAuthentication(String username, String password, User user) {
        Authentication authentication;
        try {
            final AuthenticationManager authenticationManager = user == null
                    || user.getSource().equals(User.Source.LDAP) ? authenticationLdapManager : authenticationInternalManager;

            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new AuthException(INVALID_USER_CREDENTIALS, e, ERR_MSG_INVALID_CREDENTIALS, username);
        }
        user = userService.getUserByUsernameOrEmail(username);
        userService.updateLastLoginDate(user.getId());
        return authentication;
    }

    public User getAuthenticatedUser(User jwtUser, String tenant) {
        User user = userService.getNotNullUserById(jwtUser.getId());
        if (User.Status.INACTIVE.equals(user.getStatus())) {
            throw new AuthException(USER_INACTIVE, ERR_MSG_USER_IS_INACTIVE, jwtUser.getId());
        }
        if (!tenant.equals(jwtUser.getTenant())) {
            throw new AuthException(USER_BELONGS_TO_OTHER_TENANT, ERR_MSG_USER_IS_FROM_OTHER_TENANT, jwtUser.getId(), jwtUser.getTenant());
        }
        // TODO: Do not verify password for demo user as far as it breaks demo JWT token
        if (!StringUtils.equals(adminUsername, user.getUsername()) && !StringUtils.equals(user.getPassword(), jwtUser.getPassword())) {
            throw new AuthException(ADMIN_CREDENTIALS_INVALID, ERR_MSG_USERNAME_OR_PASSWORD_IS_INVALID, adminUsername);
        }
        userService.updateLastLoginDate(user.getId());
        return user;
    }



}
