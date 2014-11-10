package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.dbaccess.model.User;

public interface UserMapper
{
	void createUser(User user);

	User getUserById(long id);

	User getUserByUserName(String username);

	void updateUser(User user);

	void deleteUserById(long id);

	void deleteUser(User user);
}
