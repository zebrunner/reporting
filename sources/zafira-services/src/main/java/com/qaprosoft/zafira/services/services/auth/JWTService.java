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
package com.qaprosoft.zafira.services.services.auth;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.qaprosoft.zafira.models.db.application.Group;
import com.qaprosoft.zafira.models.db.application.User;
import com.qaprosoft.zafira.services.services.application.GroupService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTService
{
	private String secret;
	
	private Integer authTokenExp;
	
	private Integer refreshTokenExp;

	@Autowired
	private GroupService groupService;

	public JWTService(String secret, Integer authTokenExp, Integer refreshTokenExp)
	{
		this.secret = secret;
		this.authTokenExp = authTokenExp;
		this.refreshTokenExp = refreshTokenExp;
	}

	/**
	 * Generates JWT auth token storing id, username, email, roles of the user and specifies expiration date.
	 * 
	 * @param user
	 *            - for token generation
	 * @return generated JWT token
	 */
	public String generateAuthToken(final User user, final String tenant)
	{
		Claims claims = Jwts.claims().setSubject(user.getId().toString());
		claims.put("username", user.getUsername());
		claims.put("groupIds", user.getGroups().stream().map(Group::getId).collect(Collectors.toList()));
		claims.put("tenant", tenant);
		return buildToken(claims, authTokenExp);
	}

	/**
	 * Parses user details from JWT token.
	 * 
	 * @param token
	 *            - to parse
	 * @return retrieved user details
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public User parseAuthToken(String token)
	{
		final Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		User user = new User();
		user.setId(Long.valueOf(body.getSubject()));
		user.setUsername((String) body.get("username"));
		((List) body.get("groupIds"))
				.forEach(groupId -> user.getGroups().add(groupService.getGroupById(((Number) groupId).longValue())));
		user.setTenant((String) body.get("tenant"));
		return user;
	}

	/**
	 * Verifies JWT refresh token.
	 * 
	 * @param token
	 *            - tp refresh
	 * @return parsed user
	 */
	public User parseRefreshToken(final String token)
	{
		final Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		User user = new User();
		user.setId(Long.valueOf(body.getSubject()));
		user.setPassword((String) body.get("password"));
		user.setTenant((String) body.get("tenant"));
		return user;
	}

	/**
	 * Generates JWT refresh token storing id, username, password of the user and specifies expiration date.
	 * 
	 * @param user
	 *            - for token refresh
	 * @return generated JWT token
	 */
	public String generateRefreshToken(final User user, final String tenant)
	{
		Claims claims = Jwts.claims().setSubject(user.getId().toString());
		claims.put("password", user.getPassword());
		claims.put("tenant", tenant);
		return buildToken(claims, refreshTokenExp);
	}

	/**
	 * Generates JWT access token storing id, password of the user and specifies expiration (that never expires).
	 * 
	 * @param user
	 *            - for token generation
	 * @return generated JWT token
	 */
	public String generateAccessToken(User user, String tenant)
	{
		Claims claims = Jwts.claims().setSubject(user.getId().toString());
		claims.put("password", user.getPassword());
		claims.put("tenant", tenant);
		return buildToken(claims, Integer.MAX_VALUE);
	}

	private String buildToken(Claims claims, Integer exp)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, exp);
		return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).setExpiration(c.getTime())
				.compact();
	}

	public Integer getExpiration()
	{
		return authTokenExp;
	}
}