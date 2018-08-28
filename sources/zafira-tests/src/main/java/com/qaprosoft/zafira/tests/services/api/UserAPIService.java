package com.qaprosoft.zafira.tests.services.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.tests.services.api.builders.UserTypeBuilder;

public class UserAPIService extends AbstractAPIService
{

	public UserType createUser(UserTypeBuilder userTypeBuilder)
	{
		return userTypeBuilder.register();
	}

	public List<UserType> createUsers(int count)
	{
		List<UserType> userTypes = new ArrayList<>();
		IntStream.iterate(0, i -> i++).limit(count).forEach(index -> {
			userTypes.add(createUser(new UserTypeBuilder()));
		});
		return userTypes;
	}
}
