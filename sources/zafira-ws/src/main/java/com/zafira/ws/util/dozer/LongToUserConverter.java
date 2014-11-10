package com.zafira.ws.util.dozer;

import org.dozer.DozerConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zafira.dbaccess.model.User;
import com.zafira.services.exceptions.ServiceException;
import com.zafira.services.services.UserService;

public class LongToUserConverter extends DozerConverter<Long, User>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LongToUserConverter.class);
	@Autowired
	private UserService userService;

	public LongToUserConverter()
	{
		super(Long.class, User.class);
	}

	@Override
	public User convertTo(Long source, User destination)
	{
		try
		{
			return (source == null) ? null : userService.getUserById(source);
		} catch (ServiceException e)
		{
			LOGGER.error("Couldn't get user by id", e);
			return null;
		}
	}

	@Override
	public Long convertFrom(User source, Long destination)
	{
		return (source == null) ? null : source.getId();
	}
}
