package com.qaprosoft.zafira.ws.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.models.dto.auth.CredentialsType;
import com.qaprosoft.zafira.services.services.UserService;
import com.qaprosoft.zafira.ws.security.jwt.JWTUtil;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("api/auth")
public class AuthService extends AbstractController
{
	@Value("${zafira.jwt.secret}")
	private String secret;
	
	@Value("${zafira.jwt.expiration}")
	private String expiration;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private UserService userService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AuthTokenType login(@RequestBody @Valid CredentialsType credentials) throws BadCredentialsException
	{
		AuthTokenType authToken = null;
		try
		{
			User user = userService.getUserByUserName(credentials.getUsername());
			if(user == null)
			{
				throw new UsernameNotFoundException("Invalid username: " + credentials.getUsername());
			}
			
			if(!userService.checkPassword(credentials.getPassword(), user.getPassword()))
			{
				throw new BadCredentialsException("Invalid password");
			}
			
			authToken = new AuthTokenType(jwtUtil.generateToken(user));
		}
		catch(Exception e)
		{
			throw new BadCredentialsException(e.getMessage());
		}
		return authToken;
	}
}
