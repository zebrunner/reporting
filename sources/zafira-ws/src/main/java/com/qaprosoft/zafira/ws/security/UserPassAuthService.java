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
package com.qaprosoft.zafira.ws.security;

import com.qaprosoft.zafira.services.services.auth.VarUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.models.db.application.User;
import com.qaprosoft.zafira.models.dto.auth.JwtUserType;

@Component
public class UserPassAuthService implements UserDetailsService
{
	@Autowired
	private VarUserService varUserService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User user = null;
		try
		{
			user = varUserService.getUserByUsername(username);
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
