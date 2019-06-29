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
package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.UserPreferenceMapper;
import com.qaprosoft.zafira.models.db.UserPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.qaprosoft.zafira.models.db.UserPreference.Name.DEFAULT_DASHBOARD;

@Service
public class UserPreferenceService {

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

    @Transactional(rollbackFor = Exception.class)
    public void createDefaultUserPreferences(long userId) {
        userPreferenceMapper.createUserPreferences(userId, getDefaultUserPreferences());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createUserPreference(UserPreference userPreference) {
        userPreferenceMapper.createUserPreference(userPreference);
    }

    @Transactional(readOnly = true)
    public List<UserPreference> getAllUserPreferences(Long userId) {
        return userPreferenceMapper.getUserPreferencesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<UserPreference> getDefaultUserPreferences() {
        return userPreferenceMapper.getDefaultUserPreferences();
    }

    @Transactional(readOnly = true)
    public List<UserPreference> getUserPreferencesByNameAndDashboardTitle(UserPreference.Name name, String title) {
        return userPreferenceMapper.getUserPreferencesByNameAndDashboardTitle(name, title);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserPreference updateUserPreference(UserPreference userPreference) {
        userPreferenceMapper.updateUserPreference(userPreference);
        return userPreference;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UserPreference> updateUserPreferences(long userId, List<UserPreference> userPreferences) {
        userPreferenceMapper.updateUserPreferences(userId, userPreferences);
        return userPreferences;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUserPreferencesByUserId(Long userId) {
        userPreferenceMapper.deleteUserPreferencesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public UserPreference getUserPreferenceByNameAndUserId(String name, long userId) {
        return userPreferenceMapper.getUserPreferenceByNameAndUserId(name, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UserPreference> resetUserPreferencesToDefault(long userId) {
        return updateUserPreferences(userId, getDefaultUserPreferences());
    }

    @Transactional(rollbackFor = Exception.class)
    public UserPreference createOrUpdateUserPreference(UserPreference newUserPreference) {
        UserPreference userPreference = getUserPreferenceByNameAndUserId(newUserPreference.getName().name(), newUserPreference.getUserId());
        if (userPreference == null) {
            createUserPreference(newUserPreference);
        } else if (!userPreference.equals(newUserPreference)) {
            newUserPreference.setId(userPreference.getId());
            updateUserPreference(newUserPreference);
        } else {
            newUserPreference = userPreference;
        }
        return newUserPreference;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDefaultDashboardPreference(String fromTitle, String toTitle) {
        List<UserPreference> userPreferences = getUserPreferencesByNameAndDashboardTitle(DEFAULT_DASHBOARD, fromTitle);
        for (UserPreference userPreference : userPreferences) {
            userPreference.setValue(toTitle);
            updateUserPreference(userPreference);
        }
    }
}
