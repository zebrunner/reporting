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
package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.UserPreference;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserPreferenceMapper
{
	void createUserPreference(UserPreference userPreference);

	String getDefaultPreferenceValue (String name);
	
	UserPreference getUserPreferenceById(long id);

	List<UserPreference> getUserPreferencesByUserId(long userId);

	UserPreference getUserPreferenceByNameAndUserId(@Param("name") String name, @Param("userId") long userId);

	void updateUserPreference(UserPreference userPreference);

	void deleteUserPreferenceById(long id);
	
	void deleteUserPreferencesByUserId(long userId);
}
