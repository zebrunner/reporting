package com.qaprosoft.zafira.ws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.dbaccess.model.User;
import com.qaprosoft.zafira.services.services.UserService;

@Component
public class UserAuthService implements UserDetailsService
{
	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException
	{
		User user = null;
		try
		{
			user = userService.getUserByUserName(userName);
			if (user == null)
			{
				throw new Exception("Invalid user name " + userName);
			}
		} catch (Exception e)
		{
			throw new UsernameNotFoundException("User not found", e);
		}
		return new SecuredUser(user.getId(), userName, user.getPassword(), user.getEmail(), user.getFirstName(), user.getLastName(), "USER");
	}
}
