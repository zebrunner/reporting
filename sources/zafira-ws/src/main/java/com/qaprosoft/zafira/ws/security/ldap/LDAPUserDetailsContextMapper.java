/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.ws.security.ldap;

import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.JwtUserType;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

public class LDAPUserDetailsContextMapper implements UserDetailsContextMapper
{

	private static final Logger LOGGER = Logger.getLogger(LDAPUserDetailsContextMapper.class);
	
	@Autowired
	private UserService userService;

	@Override
	public UserDetails mapUserFromContext(DirContextOperations operations, String username, Collection<? extends GrantedAuthority> authorities)
	{
		User user = null;
		try
		{
			user = userService.getUserByUsername(username);
			if(user == null) {
				throw new ForbiddenOperationException();
			}
		} catch (ServiceException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		return new JwtUserType(user.getId(), username, user.getPassword(), user.getGroups());
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter adapter)
	{
		// Do nothing
	}
}
