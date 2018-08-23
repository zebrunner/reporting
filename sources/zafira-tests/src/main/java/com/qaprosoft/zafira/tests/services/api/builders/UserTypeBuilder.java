package com.qaprosoft.zafira.tests.services.api.builders;

import com.qaprosoft.zafira.models.dto.application.user.UserType;

public class UserTypeBuilder extends AbstractTypeBuilder<UserType>
{

	private UserType userType = new UserType()
	{
		private static final long serialVersionUID = -779719747964127512L;
		{
			setFirstName("f" + getNextRandomInt());
			setLastName("l" + getNextRandomInt());
			setUsername("u" + getNextRandomInt());
			setEmail("e" + getNextRandomInt() + "@test.com");
		}
	};

	@Override
	public UserType getInstance()
	{
		return this.userType;
	}

	@Override
	public UserType register()
	{
		return zafiraClient.registerUser(userType.getUsername(), userType.getEmail(), userType.getFirstName(), userType.getLastName());
	}

	public UserType getUserType()
	{
		return userType;
	}
}
