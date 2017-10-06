package com.qaprosoft.zafira.services.services.jobs;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.EmailService;
import com.qaprosoft.zafira.services.services.emails.MonitorEmailMessageNotification;

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
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		switch (monitor.getHttpMethod())
		{
		case GET:
		{
			try
			{
				HttpGet request = new HttpGet(monitor.getUrl());
				request.addHeader("Accept", "*/*");
				responseCode = httpClient.execute(request).getStatusLine().getStatusCode();
			}
			catch (Exception e) 
			{
				LOGGER.error(e.getMessage());
			}
			break;
		}
		case PUT:
		{
			try
			{
				HttpPut request = new HttpPut(monitor.getUrl());
				request.addHeader("Content-Type", "application/json");
				request.addHeader("Accept", "*/*");
				request.setEntity(new StringEntity(monitor.getRequestBody(), "UTF-8"));
				responseCode = httpClient.execute(request).getStatusLine().getStatusCode();
			}
			catch (Exception e) 
			{
				LOGGER.error(e.getMessage());
			}
			break;
		}
		case POST:
		{
			try
			{
				HttpPost request = new HttpPost(monitor.getUrl());
				request.addHeader("Content-Type", "application/json");
				request.addHeader("Accept", "*/*");
				request.setEntity(new StringEntity(monitor.getRequestBody(), "UTF-8"));
				responseCode = httpClient.execute(request).getStatusLine().getStatusCode();
			}
			catch (Exception e) 
			{
				LOGGER.error(e.getMessage());
			}
			break;
		}
		default:
			break;
		}
		return responseCode;
	}

	private String[] getRecipientList(String recipients)
	{
		return recipients.replaceAll(" ", ",").replaceAll(";", ",").split(",");
	}

}
