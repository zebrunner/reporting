package com.zafira.ws.controller;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;

public abstract class AbstractController
{
	@Resource(name = "messageSource")
	protected MessageSource messageSource;
}
