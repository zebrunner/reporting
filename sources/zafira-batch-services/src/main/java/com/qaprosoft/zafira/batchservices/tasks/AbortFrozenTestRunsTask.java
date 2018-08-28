/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.batchservices.tasks;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.TestRunService;

//TODO: remove AbortFrozenTestRunsTask class
public class AbortFrozenTestRunsTask 
{	
	@Autowired
	private TestRunService testRunService;
	
	@Value("${zafira.batch.jobs.abortFrozenTestRuns.testRunExpirationHours}")
	private int testRunExpirationHours;
	
	public void runTask() throws ServiceException, InterruptedException
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -testRunExpirationHours);
		for(TestRun testRun : testRunService.getTestRunsByStatusAndStartedBefore(Status.IN_PROGRESS, cal.getTime()))
		{
			testRunService.abortTestRun(testRun, "");
		}
	}
}
