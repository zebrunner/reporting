package com.qaprosoft.zafira.grid.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.qaprosoft.zafira.grid.service.ISchedulerService;
import com.qaprosoft.zafira.grid.tasks.GridRequestQueueProcessorTask;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

public class SchedulerService implements ISchedulerService
{
	private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);
	
	@Autowired
	private GridRequestQueueProcessorTask gridRequestQueueTask;
	
	@Override
	public void executeGridRequestQueueTask() 
	{
		try
		{
			gridRequestQueueTask.runTask();
			logger.info("");		
		} catch (ServiceException e)
		{
			logger.error("" + e);
		}
	}
}