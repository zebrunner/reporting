package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.UserMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService
{
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncryptor passwordEncryptor;
	
	@Transactional(rollbackFor = Exception.class)
	public void createUser(User user) throws ServiceException
	{
		userMapper.createUser(user);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public User createOrUpdateUser(User newUser) throws ServiceException
	{
		if(!StringUtils.isEmpty(newUser.getPassword()))
		{
			newUser.setPassword(passwordEncryptor.encryptPassword(newUser.getPassword()));
		}
		User user = getUserByUserName(newUser.getUserName());
		if(user == null)
		{
			createUser(newUser);
		}
		else
		{
			newUser.setId(user.getId());
			updateUser(newUser);
		}
		return newUser;
	}
	
	@Transactional(readOnly = true)
	public User getUserById(long id) throws ServiceException
	{
		return userMapper.getUserById(id);
	}
	
	@Transactional(readOnly = true)
	public User getUserByUserName(String userName) throws ServiceException
	{
		return userMapper.getUserByUserName(userName);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public User updateUser(User user) throws ServiceException
	{
		userMapper.updateUser(user);
		return user;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteUser(User user) throws ServiceException
	{
		userMapper.deleteUser(user);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteUser(long id) throws ServiceException
	{
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
	
	@Transactional(readOnly = true)
	public SearchResult<User> searchUsers(UserSearchCriteria sc) throws ServiceException
	{
		SearchResult<User> results = new SearchResult<User>();
		results.setPage(sc.getPage());
		results.setPageSize(sc.getPageSize());
		results.setSortOrder(sc.getSortOrder());
		results.setResults(userMapper.searchUsers(sc));
		results.setTotalResults(userMapper.getUserSearchCount(sc));
		return results;
	}
}
