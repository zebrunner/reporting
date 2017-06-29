package com.qaprosoft.zafira.ws.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.models.dto.errors.Error;
import com.qaprosoft.zafira.models.dto.errors.ErrorCode;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;

public class UnauthorizedEntryPoint extends LoginUrlAuthenticationEntryPoint
{
	private String error;
	
	public UnauthorizedEntryPoint(String loginUrl) throws JsonProcessingException
	{
		super(loginUrl);
		this.error = new ObjectMapper().writeValueAsString(new ErrorResponse().setError(new Error(ErrorCode.UNAUTHORIZED)));
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException
	{
		response.setContentType("application/json");
	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    response.getOutputStream().println(error);
	}
}