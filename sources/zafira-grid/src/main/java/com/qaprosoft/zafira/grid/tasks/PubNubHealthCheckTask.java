package com.qaprosoft.zafira.grid.tasks;

import org.springframework.beans.factory.annotation.Autowired;

import com.qaprosoft.zafira.grid.services.impl.PubNubService;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

public class PubNubHealthCheckTask 
{	
	@Autowired
	private PubNubService pubNubService;
	
	public void runTask() throws ServiceException
	{
		pubNubService.pubNubHealthCheck();
	}
}
