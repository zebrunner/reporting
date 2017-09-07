package com.qaprosoft.zafira.services.services.jobs;

import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.EmailService;
import com.qaprosoft.zafira.services.services.emails.MonitorEmailMessageNotification;
import com.qaprosoft.zafira.services.util.HttpClientUtil;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author Kirill Bugrim
 *
 *  @version 1.0
 */

@Service
public class MonitorEmailNotificationTask implements Job {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MonitorEmailNotificationTask.class);

    public MonitorEmailNotificationTask() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ApplicationContext applicationContext = null;
        try {
            applicationContext = (ApplicationContext) jobExecutionContext
                    .getScheduler().getContext().get("applicationContext");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        EmailService emailService = applicationContext.getBean(EmailService.class);
        SchedulerContext schedulerContext = null;
        try {
            schedulerContext = jobExecutionContext.getScheduler().getContext();
        } catch (SchedulerException e1) {
            e1.printStackTrace();
        }

        Monitor monitor =
                (Monitor) schedulerContext.get(jobExecutionContext.getJobDetail().getKey().getName());

        String url = monitor.getUrl();
        String body = monitor.getRequestBody();
        Monitor.Type type = monitor.getType();

        int expectedResponseStatus = monitor.getExpectedResponseCode();
        int actualResponseStatus = 0;

        switch (monitor.getHttpMethod()) {
            case GET: {
                actualResponseStatus = HttpClientUtil.sendGetAndGetResponseStatus(url);
                break;
            }
            case PUT:{
                actualResponseStatus = HttpClientUtil.sendPutAndGetResponseStatus(url,body);
                break;
            }
            case POST:{
                actualResponseStatus = HttpClientUtil.sendPostAndGetResponseStatus(url,body);
            }
        }

        if (expectedResponseStatus != actualResponseStatus) {
            String emails = monitor.getEmails();
            String[] emailList = getEmailList(emails);
            MonitorEmailMessageNotification monitorEmailMessageNotification = new MonitorEmailMessageNotification(monitor);

            try {

                emailService.sendEmail(monitorEmailMessageNotification, emailList);
            } catch (ServiceException e) {
                LOGGER.info("Unable to send email!");
            }
        }

    }


    private String[] getEmailList(String emails) {
        return emails.split(";");
    }

}
