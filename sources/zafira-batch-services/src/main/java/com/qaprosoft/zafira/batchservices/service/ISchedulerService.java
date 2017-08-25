package com.qaprosoft.zafira.batchservices.service;

public interface ISchedulerService
{
	void executeAbortFrozenTestRunsTask();

	void executeSendMessageToUserTask();
}
