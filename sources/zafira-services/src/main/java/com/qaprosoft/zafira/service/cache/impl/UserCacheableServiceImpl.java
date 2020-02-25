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
package com.qaprosoft.zafira.service.cache.impl;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.UserMapper;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.service.cache.UserCacheableService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCacheableServiceImpl implements UserCacheableService {

    private static final String USER_CACHE_NAME = "users";
    private static final String GROUP_CACHE_NAME = "groups";

    private final UserMapper userMapper;

    public UserCacheableServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = USER_CACHE_NAME, condition = "#id != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #id")
    public User getUserByIdTrusted(long id) {
        return userMapper.getUserById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = USER_CACHE_NAME, condition = "#user.id != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #user.id")
    public User updateUser(User user) {
        userMapper.updateUser(user);
        return user;
    }

    @Override
    @Transactional
    @CacheEvict(value = USER_CACHE_NAME, condition = "#user.id != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #user.id")
    public User updateStatus(User user) {
        userMapper.updateStatus(user.getStatus(), user.getId());
        return user;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = USER_CACHE_NAME, condition = "#user.id != null", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #user.id"),
            @CacheEvict(value = GROUP_CACHE_NAME, condition = "#groupId != 0", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #groupId")
    })
    public User addUserToGroup(User user, long groupId) {
        userMapper.addUserToGroup(user.getId(), groupId);
        return userMapper.getUserById(user.getId());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = USER_CACHE_NAME, condition = "#userId != 0", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #userId"),
            @CacheEvict(value = GROUP_CACHE_NAME, condition = "#groupId != 0", key = "new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #groupId")
    })
    public User deleteUserFromGroup(long groupId, long userId) {
        userMapper.deleteUserFromGroup(userId, groupId);
        return userMapper.getUserById(userId);
    }
}
