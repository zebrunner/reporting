package com.qaprosoft.zafira.ws.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class UnauthorizedEntryPoint extends LoginUrlAuthenticationEntryPoint
{
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";

	public UnauthorizedEntryPoint(String loginUrl)
	{
		super(loginUrl);
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException
	{
		if (APPLICATION_JSON.equals(request.getHeader(CONTENT_TYPE_HEADER)))
		{
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} 
		else
		{
			super.commence(request, response, exception);
		}
	}
}