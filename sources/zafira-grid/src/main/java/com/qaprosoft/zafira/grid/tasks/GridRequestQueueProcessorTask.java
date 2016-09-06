package com.qaprosoft.zafira.grid.tasks;

import org.springframework.beans.factory.annotation.Autowired;

import com.qaprosoft.zafira.grid.queue.GridRequestQueueService;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

public class GridRequestQueueProcessorTask 
{	
	@Autowired
	private GridRequestQueueService gridRequestQueueService;
	
	public void runTask() throws ServiceException
	{
		gridRequestQueueService.processPendingConnections();
	}
}
