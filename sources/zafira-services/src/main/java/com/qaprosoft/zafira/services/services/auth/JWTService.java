package com.qaprosoft.zafira.services.services.auth;

import java.util.*;
import java.util.stream.Collectors;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.User;

import com.qaprosoft.zafira.services.services.GroupService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;

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
	 * @param user - for token generation
	 * @return generated JWT token
	 */
	public String generateAuthToken(final User user)
	{
		Claims claims = Jwts.claims().setSubject(user.getId().toString());
		claims.put("username", user.getUsername());
		claims.put("groupIds", user.getGroups().stream().map(Group::getId).collect(Collectors.toList()));
		return buildToken(claims, authTokenExp);
	}

	/**
	 * Parses user details from JWT token.
	 * @param token - to parse
	 * @return retrieved user details
	 */
	@SuppressWarnings("unchecked")
	public User parseAuthToken(String token)
	{
		final Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		User user = new User();
		user.setId(Long.valueOf(body.getSubject()));
		user.setUsername((String)body.get("username"));
		((List) body.get("groupIds")).forEach(groupId ->
				user.getGroups().add(groupService.getGroupById(((Number) groupId).longValue())));
		return user;
	}

	/**
	 * Verifies JWT refresh token.
	 * @param token - tp refresh
	 * @return parsed user
	 */
	public User parseRefreshToken(String token)
	{
		final Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		User user = new User();
		user.setId(Long.valueOf(body.getSubject()));
		user.setPassword((String)body.get("password"));
		return user;
	}

	/**
	 * Generates JWT refresh token storing id, username, password of the user and specifies expiration date.
	 * @param user - for token refresh
	 * @return generated JWT token
	 */
	public String generateRefreshToken(User user)
	{
		Claims claims = Jwts.claims().setSubject(user.getId().toString());
		claims.put("password", user.getPassword());
		return buildToken(claims, refreshTokenExp);
	}

	/**
	 * Generates JWT access token storing id, password of the user and specifies expiration (that never expires).
	 * @param user - for token generation
	 * @return generated JWT token
	 */
	public String generateAccessToken(User user)
	{
		Claims claims = Jwts.claims().setSubject(user.getId().toString());
		claims.put("password", user.getPassword());
		return buildToken(claims, Integer.MAX_VALUE);
	}

	private String buildToken(Claims claims, Integer exp)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, exp);
		return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).setExpiration(c.getTime()).compact();
	}

	public Integer getExpiration()
	{
		return authTokenExp;
	}
}