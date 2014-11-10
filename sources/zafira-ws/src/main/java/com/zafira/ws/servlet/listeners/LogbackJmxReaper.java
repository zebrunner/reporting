package com.zafira.ws.servlet.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

@WebListener
public class LogbackJmxReaper implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		lc.stop();
	}
}
