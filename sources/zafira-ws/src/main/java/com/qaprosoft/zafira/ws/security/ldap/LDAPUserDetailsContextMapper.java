package com.qaprosoft.zafira.ws.security.ldap;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.JwtUserType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.UserService;

@Component
public class LDAPUserDetailsContextMapper implements UserDetailsContextMapper
{
	private Logger LOGGER = Logger.getLogger(LDAPUserDetailsContextMapper.class);
	
	private static final String ANONYMOUS = "ananymous";
	
	@Autowired
	private UserService userService;

	@Override
	public UserDetails mapUserFromContext(DirContextOperations operations, String username, Collection<? extends GrantedAuthority> authorities)
	{
		User user = null;
		try
		{
			user = userService.getUserByUsername(username);
			if(user == null)
			{
				user = userService.getUserByUsername(ANONYMOUS);
			}
		} catch (ServiceException e)
		{
			LOGGER.error(e.getMessage());
		}
		return new JwtUserType(user.getId(), username, user.getPassword(), user.getGroups());
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter adapter)
	{
		// Do nothing
	}
}
