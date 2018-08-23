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
package com.qaprosoft.zafira.services.services.management;

import com.qaprosoft.zafira.dbaccess.dao.mysql.management.UserMapper;
import com.qaprosoft.zafira.models.db.management.User;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.UserNotFoundException;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncryptor passwordEncryptor;

    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) throws ServiceException {
        userMapper.createUser(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) throws ServiceException {
        return userMapper.getUserById(id);
    }

    @Transactional(readOnly = true)
    public User getNotNullUserById(long id) throws ServiceException {
        User user = getUserById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) throws ServiceException {
        return userMapper.getUserByUserName(username);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() throws ServiceException {
        return userMapper.getAllUsers();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginDate(long userId) {
        userMapper.updateLastLoginDate(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) throws ServiceException {
        userMapper.updateUser(user);
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(User user) throws ServiceException {
        userMapper.deleteUserById(user.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(long id) throws ServiceException {
        userMapper.deleteUserById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public User addUserToGroup(User user, long groupId) throws ServiceException {
        userMapper.addUserToGroup(user.getId(), groupId);
        return userMapper.getUserById(user.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public User deleteUserFromGroup(long groupId, long userId) throws ServiceException {
        userMapper.deleteUserFromGroup(userId, groupId);
        return userMapper.getUserById(userId);
    }

    public boolean checkPassword(String plain, String encrypted) {
        return passwordEncryptor.checkPassword(plain, encrypted);
    }
}
