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
package com.qaprosoft.zafira.services.services.jobs;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.MonitorMapper;
import com.qaprosoft.zafira.models.db.Monitor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import static org.quartz.TriggerKey.triggerKey;

@Service
public class MonitorJobService
{

	private final static Logger LOGGER = LoggerFactory.getLogger(MonitorJobService.class);

	private final static String JOB_GROUP_NAME = "monitorJobGroup";
	private final static String TRIGGER_GROUP_NAME = "monitorTriggerGroup";

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private MonitorMapper monitorMapper;

	@PostConstruct
	public void init()
	{
		List<Monitor> monitors = monitorMapper.getAllMonitors();
		for (Monitor monitor : monitors)
		{
			addJob(monitor);
			if (! monitor.isMonitorEnabled())
			{
				pauseJob(monitor.getId());
			}
		}
	}

	public void addJob(Monitor monitor)
	{
		JobDetail jobDetail = JobBuilder
				.newJob(MonitorEmailNotificationTask.class)
				.withIdentity(monitor.getId().toString(), JOB_GROUP_NAME)
				.storeDurably(true).build();

		CronTriggerImpl trigger = new CronTriggerImpl();
		trigger.setName(monitor.getId().toString());
		trigger.setGroup(TRIGGER_GROUP_NAME);

		try
		{
			trigger.setCronExpression(monitor.getCronExpression());
			schedulerFactoryBean.getScheduler().getContext().put(jobDetail.getKey().getName(), monitor);
			schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
		} catch (ParseException e)
		{
			LOGGER.error("Can't  set cron expression!");
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't  schedule job!");
		}
	}

	public void pauseJob(long id)
	{
		try
		{
			schedulerFactoryBean.getScheduler().pauseJob(findJobKey(String.valueOf(id)));
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't delete job");
		}
	}

	public void resumeJob(long id)
	{
		try
		{
			schedulerFactoryBean.getScheduler().resumeJob(findJobKey(String.valueOf(id)));
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't delete job");
		}
	}

	public void deleteJob(long id)
	{
		try
		{
			schedulerFactoryBean.getScheduler().deleteJob(findJobKey(String.valueOf(id)));
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't delete job");
		}
	}

	public void updateExistingJob(Monitor monitor)
	{

		JobDetail job = JobBuilder
				.newJob(MonitorEmailNotificationTask.class)
				.withIdentity(monitor.getId().toString(), JOB_GROUP_NAME)
				.storeDurably(true).build();
		try
		{
			schedulerFactoryBean.getScheduler().getContext().put(job.getKey().getName(), monitor);
			schedulerFactoryBean.getScheduler().addJob(job, true);
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't  schedule job!");
		}

	}

	public void updateExistingTrigger(Monitor monitor)
	{

		Trigger oldTrigger = null;
		try
		{
			oldTrigger = schedulerFactoryBean.getScheduler()
					.getTrigger(triggerKey(monitor.getId().toString(), TRIGGER_GROUP_NAME));

			CronTriggerImpl newTrigger = new CronTriggerImpl();
			newTrigger.setName(monitor.getId().toString());
			newTrigger.setGroup(TRIGGER_GROUP_NAME);
			newTrigger.setCronExpression(monitor.getCronExpression());
			if(oldTrigger != null)
			{
				schedulerFactoryBean.getScheduler().rescheduleJob(oldTrigger.getKey(), newTrigger);
			}
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't get old trigger!");
		} catch (ParseException e)
		{
			LOGGER.error("Can't set cron expression!");
		}
	}

	public void updateMonitor(Monitor monitor)
	{
		updateExistingJob(monitor);
		updateExistingTrigger(monitor);
	}

	public JobKey findJobKey(String jobName) throws SchedulerException
	{
		// Check running jobs first
		for (JobExecutionContext runningJob : schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs())
		{
			if (Objects.equals(jobName, runningJob.getJobDetail().getKey().getName()))
			{
				return runningJob.getJobDetail().getKey();
			}
		}
		// Check all jobs if not found
		for (String groupName : schedulerFactoryBean.getScheduler().getJobGroupNames())
		{
			for (JobKey jobKey : schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(groupName)))
			{
				if (Objects.equals(jobName, jobKey.getName()))
				{
					return jobKey;
				}
			}
		}
		return null;
	}

	public void switchMonitor(boolean isRunning, long monitorId)
	{
		if (isRunning)
		{
			resumeJob(monitorId);
		} else
		{
			pauseJob(monitorId);
		}
	}

}
