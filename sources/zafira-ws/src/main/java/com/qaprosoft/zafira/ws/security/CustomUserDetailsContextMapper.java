package com.qaprosoft.zafira.ws.security;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.dbaccess.model.User;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.UserService;

@Component
public class CustomUserDetailsContextMapper implements UserDetailsContextMapper
{
	private Logger LOGGER = Logger.getLogger(CustomUserDetailsContextMapper.class);
	
	private static final String ANONYMOUS = "ananymous";
	private static final String ROLE = "ROLE_USER";
	
	@Autowired
	private UserService userService;

	@Override
	public UserDetails mapUserFromContext(DirContextOperations operations, String userName,
			Collection<? extends GrantedAuthority> authorities)
	{
		User user = null;
		try
		{
			user = userService.getUserByUserName(userName);
			if(user == null)
			{
				user = userService.getUserByUserName(ANONYMOUS);
			}
		} catch (ServiceException e)
		{
			LOGGER.error(e.getMessage());
		}
		return user != null ? new SecuredUser(user.getId(), userName, user.getPassword(), user.getEmail(), user.getFirstName(), user.getLastName(), ROLE) : new SecuredUser(userName, ROLE);
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter adapter)
	{
		// Do nothing
	}
}
