package com.qaprosoft.zafira.services.services.auth;

import java.util.*;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Group.Role;
import com.qaprosoft.zafira.models.db.Permission;
import com.qaprosoft.zafira.models.db.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.codehaus.jackson.map.ObjectMapper;

public class JWTService
{
	private String secret;
	private Integer authTokenExp;
	private Integer refreshTokenExp;

	private ObjectMapper objectMapper;

	public JWTService(String secret, Integer authTokenExp, Integer refreshTokenExp)
	{
		this.secret = secret;
		this.authTokenExp = authTokenExp;
		this.refreshTokenExp = refreshTokenExp;
		this.objectMapper = new ObjectMapper();
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
		claims.put("groups", user.getGrantedGroups());
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
		((List<HashMap>)body.get("groups")).forEach(groupMap ->
				user.getGroups().add(objectMapper.convertValue(groupMap, Group.class)));
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