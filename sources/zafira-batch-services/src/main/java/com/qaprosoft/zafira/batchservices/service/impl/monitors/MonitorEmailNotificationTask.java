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
package com.qaprosoft.zafira.batchservices.service.impl.monitors;

import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.models.db.MonitorStatus;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.EmailService;
import com.qaprosoft.zafira.services.services.application.MonitorService;
import com.qaprosoft.zafira.services.services.application.emails.MonitorEmailMessageNotification;
import com.qaprosoft.zafira.services.services.application.jobs.MonitorHttpService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class MonitorEmailNotificationTask implements Job
{
	private final static Logger LOGGER = LoggerFactory.getLogger(MonitorEmailNotificationTask.class);

	private final static String EMAIL_SUBJECT = "Monitor Alert";
	private final static String EMAIL_TEXT = "";

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		try
		{
			SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();

			if (schedulerContext == null)
			{
				throw new ServiceException("Scheduler context is null");
			}

			ApplicationContext applicationContext = ((ApplicationContext) schedulerContext.get("applicationContext"));

			// Initialize email service for quartz
			EmailService emailService = applicationContext.getBean(EmailService.class);
			MonitorService monitorService = applicationContext.getBean(MonitorService.class);
			MonitorHttpService monitorHttpService = applicationContext.getBean(MonitorHttpService.class);

			Monitor monitor = (Monitor) schedulerContext.get(jobExecutionContext.getJobDetail().getKey().getName());

			int actualResponseStatus = monitorHttpService.getResponseCode(monitor);
			boolean codeMatch = monitor.getExpectedCode() == actualResponseStatus;

			monitor.setSuccess(codeMatch);
			monitorService.updateMonitor(monitor, false, false);

			Calendar dateOfPermission = Calendar.getInstance();
			Calendar lastMonitorStatusDate = Calendar.getInstance();
			MonitorStatus lastMonitorStatus = monitorService.getLastMonitorStatus(monitor.getId());
			if(lastMonitorStatus != null)
			{
				lastMonitorStatusDate.setTime(lastMonitorStatus.getCreatedAt());
				dateOfPermission.add(Calendar.HOUR_OF_DAY, -1);
			} else
			{
				dateOfPermission.add(Calendar.MILLISECOND, 1);
			}
			if(dateOfPermission.after(lastMonitorStatusDate))
			{
				monitorService.createMonitorStatus(new MonitorStatus(codeMatch), monitor.getId());
			}

			if (! codeMatch && monitor.isNotificationsEnabled())
			{
				MonitorEmailMessageNotification monitorEmailMessageNotification
						= new MonitorEmailMessageNotification(EMAIL_SUBJECT, EMAIL_TEXT, monitor, actualResponseStatus);
				try
				{
					emailService.sendEmail(monitorEmailMessageNotification, getRecipientList(monitor.getRecipients()));
				} catch (ServiceException e)
				{
					LOGGER.error("Unable to send email!");
				}
			}
		} catch (SchedulerException e1)
		{
			LOGGER.error("Can't get job context!");
		} catch (ServiceException e)
		{
			LOGGER.error("Scheduler context is null");
		}
	}

	private String[] getRecipientList(String recipients)
	{
		return recipients.replaceAll(" ", ",").replaceAll(";", ",").split(",");
	}

}
