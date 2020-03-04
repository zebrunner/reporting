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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.UserMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Group.Role;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.User.Status;
import com.qaprosoft.zafira.service.cache.UserCacheableService;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import com.qaprosoft.zafira.service.integration.tool.impl.StorageProviderService;
import com.qaprosoft.zafira.service.management.TenancyService;
import com.qaprosoft.zafira.service.util.DateTimeUtil;
import com.qaprosoft.zafira.service.util.TenancyDbInitial;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.jasypt.util.password.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.qaprosoft.zafira.models.db.User.Source.INTERNAL;
import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.CHANGE_PASSWORD_IS_NOT_POSSIBLE;
import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.TOKEN_RESET_IS_NOT_POSSIBLE;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.USER_NOT_FOUND;

@Service
public class UserService implements TenancyDbInitial {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String ERR_MSG_UNABLE_TO_RESET_TOKEN = "Unable to reset token, user is null or not internal";
    private static final String ERR_MSG_USER_WITH_THIS_ID_DOES_NOT_EXIST = "User with id %d doesn't exist";
    private static final String ERR_MSG_USER_WITH_THIS_USERNAME_DOES_NOT_EXIST = "User with username %s doesn't exist";
    private static final String ERR_MSG_USER_WITH_THIS_EMAIL_DOES_NOT_EXIST = "User with email %s doesn't exist";
    private static final String UNABLE_TO_CHANGE_PASSWORD = "Unable to change password for user %s";

    @Value("${zafira.admin.username}")
    private String adminUsername;

    @Value("${zafira.admin.password}")
    private String adminPassword;

    @Value("${zafira.admin.group}")
    private String adminGroup;

    @Value("${zafira.multitenant}")
    private String isMultitenant;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PasswordEncryptor passwordEncryptor;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private TenancyService tenancyService;

    @Autowired
    private StorageProviderService storageProviderService;

    @Autowired
    private UserCacheableService userCacheableService;

    @PostConstruct
    public void postConstruct() {
        tenancyService.iterateItems(this::initDb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initDb() {
        if (!StringUtils.isBlank(adminUsername) && !StringUtils.isBlank(adminPassword)) {
            try {
                User user = getUserByUsername(adminUsername);
                if (user == null) {
                    user = new User(adminUsername);
                    user.setSource(INTERNAL);
                    user.setStatus(Status.ACTIVE);
                    user.setPassword(passwordEncryptor.encryptPassword(adminPassword));
                    createUser(user);

                    Group group = groupService.getPrimaryGroupByRole(Role.ROLE_ADMIN);// groupService.getGroupByName(adminGroup);
                    addUserToGroup(user, group.getId());
                    user.getGroups().add(group);
                    userPreferenceService.createDefaultUserPreferences(user.getId());
                }
            } catch (Exception e) {
                LOGGER.error("Unable to init admin: " + e.getMessage(), e);
            }
        }
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        return userMapper.getUserById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProfilePhoto(Long userId) {
        User user = getNotNullUserById(userId);
        storageProviderService.removeFile(user.getPhotoURL());
        user.setPhotoURL(StringUtils.EMPTY);
        updateUser(user);
    }

    @Transactional(readOnly = true)
    public User getUserByIdTrusted(long id) {
        return userCacheableService.getUserByIdTrusted(id);
    }

    @Transactional(readOnly = true)
    public User getNotNullUserById(long id) {
        User user = getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, ERR_MSG_USER_WITH_THIS_ID_DOES_NOT_EXIST, id);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userMapper.getUserByUserName(username);
    }

    @Transactional(readOnly = true)
    public User getNotNullUserByUsername(String username) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, ERR_MSG_USER_WITH_THIS_USERNAME_DOES_NOT_EXIST, username);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        boolean isEmail = new EmailValidator().isValid(usernameOrEmail, null);
        return isEmail ? getUserByEmail(usernameOrEmail) : getUserByUsername(usernameOrEmail);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getNotNullUserByEmail(String email) {
        User user = userMapper.getUserByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, ERR_MSG_USER_WITH_THIS_EMAIL_DOES_NOT_EXIST, email);
        }
        return userMapper.getUserByEmail(email);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginDate(long userId) {
        userMapper.updateLastLoginDate(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createUser(User user) {
        userMapper.createUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        return userCacheableService.updateUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateUserProfile(User user) {
        User dbUser = getNotNullUserById(user.getId());
        dbUser.setFirstName(user.getFirstName());
        dbUser.setLastName(user.getLastName());
        return updateUser(dbUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserPassword(Long userId, String oldPassword, String password, boolean forceUpdate) {
        User user = getNotNullUserById(userId);
        if (!forceUpdate && (oldPassword == null || !passwordEncryptor.checkPassword(oldPassword, user.getPassword()))) {
            throw new IllegalOperationException(CHANGE_PASSWORD_IS_NOT_POSSIBLE, UNABLE_TO_CHANGE_PASSWORD, user.getUsername());
        }
        updateUserPassword(user, password);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserPassword(User user, String password) {
        user.setPassword(passwordEncryptor.encryptPassword(password));
        updateUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateResetToken(String token, Long userId) {
        userMapper.updateResetToken(token, userId);
    }

    @Transactional(readOnly = true)
    public User getUserByResetToken(String token) {
        User user = userMapper.getUserByResetToken(token);
        if (user == null || !user.getSource().equals(User.Source.INTERNAL)) {
            throw new IllegalOperationException(TOKEN_RESET_IS_NOT_POSSIBLE, ERR_MSG_UNABLE_TO_RESET_TOKEN);
        }
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public User createOrUpdateUser(User newUser, Long groupId) {
        User user = getUserByUsername(newUser.getUsername());
        if (user == null) {
            if (!StringUtils.isEmpty(newUser.getPassword())) {
                newUser.setPassword(passwordEncryptor.encryptPassword(newUser.getPassword()));
            }
            newUser.setSource(newUser.getSource() != null ? newUser.getSource() : INTERNAL);
            newUser.setStatus(User.Status.ACTIVE);
            createUser(newUser);
            Group group = groupId != null ? groupService.getGroupById(groupId) : groupService.getPrimaryGroupByRole(Role.ROLE_USER);
            if (group != null) {
                addUserToGroup(newUser, group.getId());
                newUser.getGroups().add(group);
            }
            userPreferenceService.createDefaultUserPreferences(newUser.getId());
        } else {
            newUser.setId(user.getId());
            updateUser(newUser);
        }
        return newUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateStatus(User user) {
        return userCacheableService.updateStatus(user);
    }

    public User createOrUpdateUser(User newUser) {
        return createOrUpdateUser(newUser, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public User addUserToGroup(User user, long groupId) {
        return userCacheableService.addUserToGroup(user, groupId);
    }

    @Transactional(rollbackFor = Exception.class)
    public User deleteUserFromGroup(long groupId, long userId) {
        return userCacheableService.deleteUserFromGroup(groupId, userId);
    }

    @Transactional(readOnly = true)
    public SearchResult<User> searchUsers(UserSearchCriteria sc, Boolean publicDetails) {
        DateTimeUtil.actualizeSearchCriteriaDate(sc);

        List<User> users = userMapper.searchUsers(sc, publicDetails);
        int count = userMapper.getUserSearchCount(sc, publicDetails);

        return SearchResult.<User>builder()
                .page(sc.getPage())
                .pageSize(sc.getPageSize())
                .sortOrder(sc.getSortOrder())
                .results(users)
                .totalResults(count)
                .build();
    }

    public String getAdminUsername() {
        return this.adminUsername;
    }

}
