package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.UserPreference;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserPreferenceMapper
{
	void createUserPreference(UserPreference userPreference);
	
	UserPreference getUserPreferenceById(long id);

	List<UserPreference> getUserPreferencesByUserId(long userId);

	UserPreference getUserPreferenceByNameAndUserId(@Param("name") String name, @Param("userId") long userId);

	void updateUserPreference(UserPreference userPreference);

	void deleteUserPreferenceById(long id);
	
	void deleteUserPreferencesByUserId(long userId);
}
