package com.qaprosoft.zafira.services.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class VersionService
{
	@Value("${zafira.service.version}")
	private String serviceVersion;
	
	@Value("${zafira.client.version}")
	private String clientVersion;
	
	public String getServiceVersion() throws ServiceException
	{
		return serviceVersion;
	}
	
	public String getClientVersion() throws ServiceException
	{
		return clientVersion;
	}
}
