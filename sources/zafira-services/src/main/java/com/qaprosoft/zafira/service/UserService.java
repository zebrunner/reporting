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
import com.qaprosoft.zafira.models.dto.user.PasswordChangingType;
import com.qaprosoft.zafira.service.exception.ForbiddenOperationException;
import com.qaprosoft.zafira.service.exception.UserNotFoundException;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import java.util.List;

import static com.qaprosoft.zafira.models.db.User.Source.INTERNAL;

@Service
public class UserService implements TenancyDbInitial {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

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
        User user = getUserById(userId);
        storageProviderService.removeFile(user.getPhotoURL());
        user.setPhotoURL(StringUtils.EMPTY);
        updateUser(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", condition = "#id != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #id")
    public User getUserByIdTrusted(long id) {
        return userMapper.getUserById(id);
    }

    @Transactional(readOnly = true)
    public User getNotNullUserById(long id) {
        User user = getUserById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userMapper.getUserByUserName(username);
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

    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginDate(long userId) {
        userMapper.updateLastLoginDate(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createUser(User user) {
        userMapper.createUser(user);
    }

    @CacheEvict(value = "users", condition = "#user.id != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #user.id")
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        userMapper.updateUser(user);
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserPassword(PasswordChangingType password, boolean forceUpdate) {
        User user = getNotNullUserById(password.getUserId());
        if (!forceUpdate && (password.getOldPassword() == null || !passwordEncryptor.checkPassword(password.getOldPassword(), user.getPassword()))) {
            throw new ForbiddenOperationException();
        }
        updateUserPassword(user, password);
    }

    @CacheEvict(value = "users", condition = "#password.userId != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #password.userId")
    @Transactional(rollbackFor = Exception.class)
    public void updateUserPassword(User user, PasswordChangingType password) {
        user = user != null ? user : getNotNullUserById(password.getUserId());
        user.setPassword(passwordEncryptor.encryptPassword(password.getPassword()));
        updateUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateResetToken(String token, Long userId) {
        userMapper.updateResetToken(token, userId);
    }

    @Transactional(readOnly = true)
    public User getUserByResetToken(String token) {
        return userMapper.getUserByResetToken(token);
    }

    @Transactional(rollbackFor = Exception.class)
    public User createOrUpdateUser(User newUser, Group group) {
        User user = getUserByUsername(newUser.getUsername());
        if (user == null) {
            if (!StringUtils.isEmpty(newUser.getPassword())) {
                newUser.setPassword(passwordEncryptor.encryptPassword(newUser.getPassword()));
            }
            newUser.setSource(newUser.getSource() != null ? newUser.getSource() : INTERNAL);
            newUser.setStatus(User.Status.ACTIVE);
            createUser(newUser);
            group = group != null ? group : groupService.getPrimaryGroupByRole(Role.ROLE_USER);
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
    @CacheEvict(value = "users", condition = "#user.id != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #user.id")
    public User updateStatus(User user) {
        userMapper.updateStatus(user.getStatus(), user.getId());
        return user;
    }

    public User createOrUpdateUser(User newUser) {
        return createOrUpdateUser(newUser, null);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", condition = "#user.id != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #user.id"),
            @CacheEvict(value = "groups", condition = "#groupId != 0", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #groupId")
    })
    @Transactional(rollbackFor = Exception.class)
    public User addUserToGroup(User user, long groupId) {
        userMapper.addUserToGroup(user.getId(), groupId);
        return userMapper.getUserById(user.getId());
    }

    @Caching(evict = {
            @CacheEvict(value = "users", condition = "#userId != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #userId"),
            @CacheEvict(value = "groups", condition = "#groupId != 0", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #groupId")
    })
    @Transactional(rollbackFor = Exception.class)
    public User deleteUserFromGroup(long groupId, long userId) {
        userMapper.deleteUserFromGroup(userId, groupId);
        return userMapper.getUserById(userId);
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
