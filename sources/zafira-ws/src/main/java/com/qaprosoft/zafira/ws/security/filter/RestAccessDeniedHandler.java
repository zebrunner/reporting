package com.qaprosoft.zafira.ws.security.filter;

import static org.apache.commons.httpclient.HttpStatus.SC_FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.models.dto.errors.Error;
import com.qaprosoft.zafira.models.dto.errors.ErrorCode;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;

/**
 * The RestAccessDeniedHandler is called by the ExceptionTranslationFilter to handle all AccessDeniedExceptions. These
 * exceptions are thrown when the authentication is valid but access is not authorized.
 */
public class RestAccessDeniedHandler implements AccessDeniedHandler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException
	{
		ErrorResponse result = new ErrorResponse();
		result.setError(new Error(ErrorCode.FORBIDDENT));
		ObjectMapper objMapper = new ObjectMapper();

		final HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
		wrapper.setStatus(SC_FORBIDDEN);
		wrapper.setContentType(APPLICATION_JSON_VALUE);
		wrapper.getWriter().println(objMapper.writeValueAsString(result));
		wrapper.getWriter().flush();
	}
}
