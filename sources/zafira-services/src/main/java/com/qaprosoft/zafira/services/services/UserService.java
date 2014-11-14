package com.qaprosoft.zafira.services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.UserMapper;
import com.qaprosoft.zafira.dbaccess.model.User;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class UserService
{
	@Autowired
	private UserMapper userMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createUser(User user) throws ServiceException
	{
		userMapper.createUser(user);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public User createOrUpdateUser(User newUser) throws ServiceException
	{
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
}
