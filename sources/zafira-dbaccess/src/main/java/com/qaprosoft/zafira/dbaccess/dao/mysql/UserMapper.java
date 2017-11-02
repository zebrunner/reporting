package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper
{
	void createUser(User user);

	User getUserById(long id);

	User getUserByUserName(String username);

	void updateUser(User user);
	
	void updateLastLoginDate(long userId);

	void deleteUserById(long id);

	void deleteUser(User user);

	void addUserToGroup(@Param("userId") Long userId, @Param("groupId")Long groupId);

	void deleteUserFromGroup(@Param("userId") Long userId, @Param("groupId")Long groupId);

	List<User> searchUsers(UserSearchCriteria sc);

	Integer getUserSearchCount(UserSearchCriteria sc);
}
