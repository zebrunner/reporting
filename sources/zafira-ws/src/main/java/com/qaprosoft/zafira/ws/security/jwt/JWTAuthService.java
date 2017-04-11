package com.qaprosoft.zafira.ws.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.ws.security.SecuredUser;

@Component
public class JWTAuthService implements UserDetailsService
{
	@Autowired
	private JWTUtil jwtUtil;
	
	@Override
	public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException
	{
		User user = null;
		try
		{
			user = jwtUtil.parseToken(token);
		} 
		catch (Exception e)
		{
			throw new UsernameNotFoundException("User not found", e);
		}
		return new SecuredUser(user.getId(), 
							   user.getUserName(), user.getPassword(), 
							   user.getEmail(), 
							   user.getFirstName(), user.getLastName(), 
							   user.getRoles());
	}
}