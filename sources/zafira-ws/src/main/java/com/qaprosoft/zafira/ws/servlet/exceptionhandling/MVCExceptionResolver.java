package com.qaprosoft.zafira.ws.servlet.exceptionhandling;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class MVCExceptionResolver extends SimpleMappingExceptionResolver
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MVCExceptionResolver.class);

	public MVCExceptionResolver()
	{
		setWarnLogCategory(MVCExceptionResolver.class.getName());
	}

	@Override
	protected void logException(Exception ex, HttpServletRequest request)
	{
		LOGGER.error(buildLogMessage(ex, request), ex);
	}
}
