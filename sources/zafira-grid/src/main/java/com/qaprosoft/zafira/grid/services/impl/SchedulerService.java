package com.qaprosoft.zafira.grid.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.qaprosoft.zafira.grid.services.ISchedulerService;
import com.qaprosoft.zafira.grid.tasks.UsbDeviceHealthCheckTask;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

public class SchedulerService implements ISchedulerService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);
	
	@Autowired
	private UsbDeviceHealthCheckTask usbDeviceHealthCheck;

	@Override
	public void executeUsbDeviceHealthCheck()
	{
		try
		{
			LOGGER.info("Running usbDeviceHealthCheck");	
			usbDeviceHealthCheck.runTask();
		} catch (ServiceException e)
		{
			LOGGER.error("Error in usbDeviceHealthCheck:" + e);
		}
	}
}