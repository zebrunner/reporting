package com.qaprosoft.zafira.ws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.JwtUserType;
import com.qaprosoft.zafira.services.services.UserService;

@Component
public class UserPassAuthService implements UserDetailsService
{
	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User user = null;
		try
		{
			user = userService.getUserByUsername(username);
			if (user == null)
			{
				throw new Exception("Invalid user name " + username);
			}
		} 
		catch (Exception e)
		{
			throw new UsernameNotFoundException("User not found", e);
		}
		return new JwtUserType(user.getId(), username, user.getPassword(), user.getGroups());
	}
}
