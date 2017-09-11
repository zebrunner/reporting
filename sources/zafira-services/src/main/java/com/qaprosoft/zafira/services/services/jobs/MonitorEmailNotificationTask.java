package com.qaprosoft.zafira.services.services.jobs;

import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.EmailService;
import com.qaprosoft.zafira.services.services.emails.MonitorEmailMessageNotification;
import com.qaprosoft.zafira.services.util.HttpClientUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class MonitorEmailNotificationTask implements Job
{

	private final static Logger LOGGER = LoggerFactory.getLogger(MonitorEmailNotificationTask.class);

	private final static String EMAILS_STRING_SEPARATOR = ",";
	private final static String EMAIL_SUBJECT = "Monitor Alert!";
	private final static String EMAIL_TEXT = "";

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{

		try
		{
			// Initialize email service for quartz
			EmailService emailService = ((ApplicationContext) jobExecutionContext
					.getScheduler().getContext().get("applicationContext")).getBean(EmailService.class);

			SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
			if (schedulerContext == null)
			{
				throw new ServiceException("Scheduler context is null");
			}

			Monitor monitor = (Monitor) schedulerContext.get(jobExecutionContext.getJobDetail().getKey().getName());

			int actualResponseStatus = getResponseCode(monitor);

			if (monitor.getExpectedResponseCode() != actualResponseStatus)
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

	private Integer getResponseCode(Monitor monitor)
	{
		int responseCode = 0;
		switch (monitor.getHttpMethod())
		{
		case GET:
		{
			responseCode = HttpClientUtil.sendGetAndGetResponseStatus(monitor.getUrl());
			break;
		}
		case PUT:
		{
			responseCode = HttpClientUtil.sendPutAndGetResponseStatus(monitor.getUrl(), monitor.getRequestBody());
			break;
		}
		case POST:
		{
			responseCode = HttpClientUtil.sendPostAndGetResponseStatus(monitor.getUrl(), monitor.getRequestBody());
			break;
		}
		default:
			break;
		}
		return responseCode;
	}

	private String[] getRecipientList(String recipients)
	{
		return recipients.split(EMAILS_STRING_SEPARATOR);
	}

}
