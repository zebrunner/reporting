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
package com.qaprosoft.zafira.web.security.ldap;

import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.JwtUserType;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.services.application.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class LDAPUserDetailsContextMapper implements UserDetailsContextMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LDAPUserDetailsContextMapper.class);

    private static final String MSG_ILLEGAL_USER_LOGIN = "User %s is not an invited Zafira user";
    private static final String WRN_MSG_NON_EXISTING_ZAFIRA_USER = "Existing LDAP user %s can't be logged in because he does not exists in Zafira";

    private final UserService userService;

    public LDAPUserDetailsContextMapper(UserService userService) {
        this.userService = userService;
    }

    /**
     * Mapping LDAP user to Zafira user. If LDAP user is not a Zafira user (meaning that he was found in LDAP
     * but was not invited to Zafira)
     */
    @Override
    public UserDetails mapUserFromContext(DirContextOperations operations, String username, Collection<? extends GrantedAuthority> authorities) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            LOGGER.warn(String.format(WRN_MSG_NON_EXISTING_ZAFIRA_USER, username));
            throw new ForbiddenOperationException(String.format(MSG_ILLEGAL_USER_LOGIN, username));
        } else {
            return new JwtUserType(user.getId(), username, user.getPassword(), user.getGroups());
        }
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter adapter) {
        // Do nothing
    }
}
