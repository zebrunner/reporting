package com.qaprosoft.zafira.ws.security.jwt;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil
{
	@Value("${zafira.jwt.secret}")
	private String secret;
	
	@Value("${zafira.jwt.expiration}")
	private String expiration;

	/**
	 * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role
	 * prefilled (extracted from token). If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
	 * 
	 * @param token
	 *          the JWT token to parse
	 * @return 
	 *			the User object extracted from specified token or null if a token is invalid.
	 */
	@SuppressWarnings("unchecked")
	public User parseToken(String token)
	{
		Claims body = Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();
		
		User user = new User();
		user.setId(Long.parseLong((String) body.get("id")));
		user.setUserName(body.getSubject());
		user.setPassword((String)body.get("password"));
		user.setFirstName((String)body.get("firstName"));
		user.setLastName((String)body.get("lastName"));
		user.setEmail((String)body.get("email"));
		user.setGroups((List<Group>)body.get("groups"));
		return user;
	}

	/**
	 * Generates a JWT token containing username as subject, and userId and role as additional claims. These properties
	 * are taken from the specified User object. Tokens validity is infinite.
	 * 
	 * @param user
	 *            the user for which the token will be generated
	 * @return the JWT token
	 */
	public String generateToken(User user)
	{
		Claims claims = Jwts.claims().setSubject(user.getUserName());
		claims.put("id", user.getId());
		claims.put("email", user.getEmail());
		claims.put("password", StringUtils.EMPTY);
		claims.put("firstName", user.getFirstName());
		claims.put("lastName", user.getLastName());
		claims.put("groups", user.getGroups());
		
		Calendar exp = Calendar.getInstance();
		exp.add(Calendar.MINUTE, Integer.valueOf(expiration));

		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, secret)
				.setExpiration(exp.getTime())
				.compact();
	}
}