package com.qaprosoft.zafira.grid.tasks;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.qaprosoft.zafira.grid.models.GridRequest;
import com.qaprosoft.zafira.grid.services.impl.GridRequestQueueService;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

public class GridRequestQueueProcessorTask 
{	
	private Logger LOGGER = Logger.getLogger(GridRequestQueueProcessorTask.class);
	
	@Autowired
	private GridRequestQueueService gridRequestQueueService;
	
	public void runTask() throws ServiceException
	{
		Collection<GridRequest> requests = gridRequestQueueService.getPendingConnectionRequests();
		LOGGER.info(String.format("Starting to process %d device requests...", requests.size()));
		
		for(GridRequest request : requests)
		{
			gridRequestQueueService.connectDevice(request);
		}
	}
}
