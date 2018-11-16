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
package com.qaprosoft.zafira.batchservices.services;

import com.google.gson.Gson;
import com.qaprosoft.zafira.batchservices.tasks.MonitorEmailNotificationTask;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.models.push.events.MonitorEventMessage;
import com.qaprosoft.zafira.services.services.application.MonitorService;
import com.qaprosoft.zafira.services.services.management.MngTenancyService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import static org.quartz.TriggerKey.triggerKey;

public class MonitorJobService
{

	private final static Logger LOGGER = LoggerFactory.getLogger(MonitorJobService.class);

	private final static String JOB_GROUP_NAME = "monitorJobGroup";
	private final static String TRIGGER_GROUP_NAME = "monitorTriggerGroup";

	@Autowired
	private SchedulerFactoryBean springScheduler;

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private MngTenancyService mngTenancyService;

	@PostConstruct
	public void init()
	{
		mngTenancyService.iterateItems(tenancy -> {
			List<Monitor> monitors = monitorService.getAllMonitors();
			for (Monitor monitor : monitors)
			{
				addJob(monitor);
				if (! monitor.isMonitorEnabled())
				{
					pauseJob(monitor.getId());
				}
			}
		});
	}

	public void addJob(Monitor monitor)
	{
		JobDetail jobDetail = JobBuilder
				.newJob(MonitorEmailNotificationTask.class)
				.withIdentity(getIdentity(monitor.getId()), getJobGroupName())
				.storeDurably(true).build();

		CronTriggerImpl trigger = new CronTriggerImpl();
		trigger.setName(getIdentity(monitor.getId()));
		trigger.setGroup(getTriggerGroupName());

		try
		{
			trigger.setCronExpression(monitor.getCronExpression());
			springScheduler.getScheduler().getContext().put(jobDetail.getKey().getName(), monitor);
			springScheduler.getScheduler().scheduleJob(jobDetail, trigger);
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
			springScheduler.getScheduler().pauseJob(findJobKey(String.valueOf(id)));
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't delete job");
		}
	}

	public void resumeJob(long id)
	{
		try
		{
			springScheduler.getScheduler().resumeJob(findJobKey(String.valueOf(id)));
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't delete job");
		}
	}

	public void deleteJob(long id)
	{
		try
		{
			springScheduler.getScheduler().deleteJob(findJobKey(String.valueOf(id)));
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't delete job");
		}
	}

	public void updateExistingJob(Monitor monitor)
	{

		JobDetail job = JobBuilder
				.newJob(MonitorEmailNotificationTask.class)
				.withIdentity(getIdentity(monitor.getId()), getJobGroupName())
				.storeDurably(true).build();
		try
		{
			springScheduler.getScheduler().getContext().put(job.getKey().getName(), monitor);
			springScheduler.getScheduler().addJob(job, true);
		} catch (SchedulerException e)
		{
			LOGGER.error("Can't  schedule job!");
		}

	}

	public void updateExistingTrigger(Monitor monitor)
	{

		Trigger oldTrigger;
		try
		{
			oldTrigger = springScheduler.getScheduler()
					.getTrigger(triggerKey(getIdentity(monitor.getId()), getTriggerGroupName()));

			CronTriggerImpl newTrigger = new CronTriggerImpl();
			newTrigger.setName(getIdentity(monitor.getId()));
			newTrigger.setGroup(getTriggerGroupName());
			newTrigger.setCronExpression(monitor.getCronExpression());
			if(oldTrigger != null)
			{
				springScheduler.getScheduler().rescheduleJob(oldTrigger.getKey(), newTrigger);
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
		for (JobExecutionContext runningJob : springScheduler.getScheduler().getCurrentlyExecutingJobs())
		{
			if (Objects.equals(jobName, runningJob.getJobDetail().getKey().getName()))
			{
				return runningJob.getJobDetail().getKey();
			}
		}
		// Check all jobs if not found
		for (String groupName : springScheduler.getScheduler().getJobGroupNames())
		{
			for (JobKey jobKey : springScheduler.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(groupName)))
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

	@RabbitListener(queues = "#{monitorsQueue.name}")
	public void process(Message message) {
		MonitorEventMessage monitorMessage = new Gson().fromJson(new String(message.getBody()), MonitorEventMessage.class);
		TenancyContext.setTenantName(monitorMessage.getTenancy());
		Monitor monitor = monitorService.getMonitorById(monitorMessage.getMonitorId());
		if (monitor != null) {
			switch (monitorMessage.getAction()) {
				case CREATE:
					addJob(monitor);
					break;
				case UPDATE:
					updateMonitor(monitor);
					break;
				case SWITCH:
					switchMonitor(monitor.isMonitorEnabled(), monitor.getId());
					break;
				case DELETE:
					deleteJob(monitor.getId());
					break;
				default:
					break;
			}
		}
	}

	private String getIdentity(Long monitorId) {
		return monitorId + "_" + TenancyContext.getTenantName();
	}

	private String getJobGroupName() {
		return JOB_GROUP_NAME;
	}

	private String getTriggerGroupName() {
		return TenancyContext.getTenantName();
	}
}
