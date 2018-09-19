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
package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.UserPreferenceMapper;
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
 	public void createDefaultUserPreferences(long userId) throws ServiceException
	{
		userPreferenceMapper.createUserPreferences(userId, getDefaultUserPreferences());
	}

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
 	public List<UserPreference> getDefaultUserPreferences() throws ServiceException
	{
		return userPreferenceMapper.getDefaultUserPreferences();
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
	public List<UserPreference> updateUserPreferences(long userId, List<UserPreference> userPreferences) throws ServiceException
	{
		userPreferenceMapper.updateUserPreferences(userId, userPreferences);
		return userPreferences;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<UserPreference> resetUserPreferencesToDefault1(long userId) throws ServiceException
	{
		return updateUserPreferences(userId, getDefaultUserPreferences());
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteUserPreferenceById(Long id) throws ServiceException
	{
		userPreferenceMapper.deleteUserPreferenceById(id);
	}

    @Transactional(rollbackFor = Exception.class)
    public String getDefaultPreferenceValue(String name) throws ServiceException
    {
        return userPreferenceMapper.getDefaultPreferenceValue(name);
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
 	public List<UserPreference> resetUserPreferencesToDefault(long userId) throws ServiceException
	{
		return updateUserPreferences(userId, getDefaultUserPreferences());
	}

	@Transactional(rollbackFor = Exception.class)
	public UserPreference createOrUpdateUserPreference(UserPreference newUserPreference) throws ServiceException
	{
		UserPreference userPreference = getUserPreferenceByNameAndUserId(newUserPreference.getName(), newUserPreference.getUserId());
		if(userPreference == null)
		{
			createUserPreference(newUserPreference);
		}
		else if(! userPreference.equals(newUserPreference))
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
