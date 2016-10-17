package com.qaprosoft.zafira.grid.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.qaprosoft.zafira.grid.service.ISchedulerService;
import com.qaprosoft.zafira.grid.tasks.GridHealthCheckTask;
import com.qaprosoft.zafira.grid.tasks.GridRequestQueueProcessorTask;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

public class SchedulerService implements ISchedulerService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);
	
	@Autowired
	private GridRequestQueueProcessorTask gridRequestQueueTask;
	
	@Autowired
	private GridHealthCheckTask gridHealthCheckTask;
	
	@Override
	public void executeGridRequestQueueTask() 
	{
		try
		{
			LOGGER.info("Running executeGridRequestQueueTask");	
			gridRequestQueueTask.runTask();
		} catch (ServiceException e)
		{
			LOGGER.error("Error in executeGridRequestQueueTask:" + e);
		}
	}
	
	@Override
	public void executeGridHealthCheckTask() 
	{
		try
		{
			LOGGER.info("Running gridHealthCheckTask");	
			gridHealthCheckTask.runTask();
		} catch (ServiceException e)
		{
			LOGGER.error("Error in gridHealthCheckTask:" + e);
		}
	}
}