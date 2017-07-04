package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.services.util.PeriodCalculator;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

@Service
public class UserService
{
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private GroupService groupService;

	@Autowired
	private PasswordEncryptor passwordEncryptor;

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

	@Cacheable(value = "users")
	@Transactional(readOnly = true)
	public User getUserByUsername(String username) throws ServiceException
	{
		return userMapper.getUserByUserName(username);
	}
	
	@CacheEvict(value = "users", allEntries=true)
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
		if (!StringUtils.isEmpty(newUser.getPassword()))
		{
			newUser.setPassword(passwordEncryptor.encryptPassword(newUser.getPassword()));
		}
		User user = getUserByUsername(newUser.getUsername());
		if (user == null)
		{
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
		if (sc.getPeriod()!=null){
			PeriodCalculator.setPeriod(sc);
		}
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
