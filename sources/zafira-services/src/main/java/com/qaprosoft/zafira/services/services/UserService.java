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
package com.qaprosoft.zafira.services.services;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.UserMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Group.Role;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.UserNotFoundException;

import static com.qaprosoft.zafira.services.util.DateFormatter.*;

@Service
public class UserService
{
	private static final Logger LOGGER = Logger.getLogger(UserService.class);
	
	@Value("${zafira.admin.username}")
	private String adminUsername;
	
	@Value("${zafira.admin.password}")
	private String adminPassword;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private GroupService groupService;

	@Autowired
	private PasswordEncryptor passwordEncryptor;

	@Autowired
 	private UserPreferenceService userPreferenceService;

	@PostConstruct
	public void init()
	{
		try
		{
			if(!StringUtils.isEmpty(adminUsername) && !StringUtils.isEmpty(adminPassword))
			{
				User user = getUserByUsername(adminUsername);
				if(user == null)
				{
					user = new User(adminUsername);
					user.setPassword(passwordEncryptor.encryptPassword(adminPassword));
					createUser(user);
					
					Group group = groupService.getPrimaryGroupByRole(Role.ROLE_ADMIN);
					if(group != null)
					{
						addUserToGroup(user, group.getId());
						user.getGroups().add(group);
					}
					userPreferenceService.createDefaultUserPreferences(user.getId());
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Unable to init admin: " + e.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public User getUserById(long id) throws ServiceException
	{
		return userMapper.getUserById(id);
	}
	
	@Transactional(readOnly = true)
	public User getNotNullUserById(long id) throws ServiceException
	{
		User user = getUserById(id);
		if(user == null)
		{
			throw new UserNotFoundException();
		}
		return user;
	}

	@Transactional(readOnly = true)
	public User getUserByUsername(String username) throws ServiceException
	{
		return userMapper.getUserByUserName(username);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void updateLastLoginDate(long userId)
	{
		userMapper.updateLastLoginDate(userId);
	}

	@Transactional(rollbackFor = Exception.class)
	public void createUser(User user) throws ServiceException
	{
		userMapper.createUser(user);
	}

	@CacheEvict(value = "users", allEntries=true)
	@Transactional(rollbackFor = Exception.class)
	public User updateUser(User user) throws ServiceException
	{
		userMapper.updateUser(user);
		return user;
	}
	
	@CacheEvict(value = "users", allEntries=true)
	@Transactional(rollbackFor = Exception.class)
	public void updateUserPassword(long id, String password) throws ServiceException
	{
		User user = getNotNullUserById(id);
		user.setPassword(passwordEncryptor.encryptPassword(password));
		updateUser(user);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public User createOrUpdateUser(User newUser) throws ServiceException
	{
		User user = getUserByUsername(newUser.getUsername());
		if (user == null)
		{
			if (!StringUtils.isEmpty(newUser.getPassword()))
			{
				newUser.setPassword(passwordEncryptor.encryptPassword(newUser.getPassword()));
			}
			createUser(newUser);
			Group group = groupService.getPrimaryGroupByRole(Role.ROLE_USER);
			if(group != null)
			{
				addUserToGroup(newUser, group.getId());
				newUser.getGroups().add(group);
			}

		} else
		{
			newUser.setId(user.getId());
			updateUser(newUser);
		}
		return newUser;
	}

	@CacheEvict(value = "users", allEntries=true)
	@Transactional(rollbackFor = Exception.class)
	public void deleteUser(User user) throws ServiceException
	{
		userMapper.deleteUser(user);
	}

	@CacheEvict(value = "users", allEntries=true)
	@Transactional(rollbackFor = Exception.class)
	public void deleteUser(long id) throws ServiceException
	{
		userMapper.deleteUserById(id);
	}

	@CacheEvict(value = "users", allEntries=true)
	@Transactional(rollbackFor = Exception.class)
	public User addUserToGroup(User user, long groupId) throws ServiceException
	{
		userMapper.addUserToGroup(user.getId(), groupId);
		return userMapper.getUserById(user.getId());
	}

	@CacheEvict(value = "users", allEntries=true)
	@Transactional(rollbackFor = Exception.class)
	public User deleteUserFromGroup(long groupId, long userId) throws ServiceException
	{
		userMapper.deleteUserFromGroup(userId, groupId);
		return userMapper.getUserById(userId);
	}

	@Transactional(readOnly = true)
	public SearchResult<User> searchUsers(UserSearchCriteria sc) throws ServiceException
	{
		actualizeSearchCriteriaDate(sc);
		SearchResult<User> results = new SearchResult<User>();
		results.setPage(sc.getPage());
		results.setPageSize(sc.getPageSize());
		results.setSortOrder(sc.getSortOrder());
		results.setResults(userMapper.searchUsers(sc));
		results.setTotalResults(userMapper.getUserSearchCount(sc));
		return results;
	}
	
	public boolean checkPassword(String plain, String encrypted)
	{
		return passwordEncryptor.checkPassword(plain, encrypted);
	}
}
