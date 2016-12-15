package com.qaprosoft.zafira.grid.services;

public interface ISchedulerService
{
	 public void executeGridRequestQueueTask();
	 
	 public void executeGridHealthCheckTask();
	 
	 public void executePubNubHealthCheckTask();
	 
	 public void executeUsbDeviceHealthCheck();
}
