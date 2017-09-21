package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.UserPreferenceMapper;
import com.qaprosoft.zafira.models.db.UserPreference;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserPreferenceService
{
	@Autowired
	private UserPreferenceMapper userPreferenceMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createUserPreference(UserPreference userPreference) throws ServiceException
	{
		userPreferenceMapper.createUserPreference(userPreference);
	}
	
	@Transactional(readOnly = true)
	public List<UserPreference> getAllUserPreferences(Long userId) throws ServiceException
	{
		return userPreferenceMapper.getUserPreferencesByUserId(userId);
	}
	
	@Transactional(readOnly = true)
	public UserPreference getUserPreferenceById(long id) throws ServiceException
	{
		return userPreferenceMapper.getUserPreferenceById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public UserPreference updateUserPreference(UserPreference userPreference) throws ServiceException
	{
		userPreferenceMapper.updateUserPreference(userPreference);
		return userPreference;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteUserPreferenceById(Long id) throws ServiceException
	{
		userPreferenceMapper.deleteUserPreferenceById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteUserPreferencesByUserId(Long userId) throws ServiceException
	{
		userPreferenceMapper.deleteUserPreferencesByUserId(userId);
	}

	@Transactional(readOnly = true)
	public UserPreference getUserPreferenceByNameAndUserId(String name, long userId) throws ServiceException
	{
        UserPreference userPreference = userPreferenceMapper.getUserPreferenceByNameAndUserId(name, userId);
		return userPreference;
	}

	@Transactional(rollbackFor = Exception.class)
	public UserPreference createOrUpdateUserPreference(UserPreference newUserPreference) throws ServiceException
	{
		UserPreference userPreference = getUserPreferenceByNameAndUserId(newUserPreference.getName(), newUserPreference.getUserId());
		if(userPreference == null)
		{
			createUserPreference(newUserPreference);
		}
		else if(!userPreference.equals(newUserPreference))
		{
			newUserPreference.setId(userPreference.getId());
			updateUserPreference(newUserPreference);
		}
		else
		{
			newUserPreference = userPreference;
		}
		return newUserPreference;
	}
}
