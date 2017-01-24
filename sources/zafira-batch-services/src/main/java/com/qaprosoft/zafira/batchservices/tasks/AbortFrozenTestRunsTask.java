package com.qaprosoft.zafira.batchservices.tasks;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestRunService;

public class AbortFrozenTestRunsTask 
{	
	@Autowired
	private TestRunService testRunService;
	
	@Value("${zafira.batch.jobs.abortFrozenTestRuns.testRunExpirationHours}")
	private int testRunExpirationHours;
	
	public void runTask() throws ServiceException
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -testRunExpirationHours);
		for(TestRun testRun : testRunService.getTestRunsByStatusAndStartedBefore(Status.IN_PROGRESS, cal.getTime()))
		{
			testRunService.abortTestRun(testRun.getId());
		}
	}
}
