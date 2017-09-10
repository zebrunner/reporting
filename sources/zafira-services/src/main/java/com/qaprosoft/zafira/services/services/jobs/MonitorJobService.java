package com.qaprosoft.zafira.services.services.jobs;

import com.qaprosoft.zafira.dbaccess.dao.mysql.MonitorMapper;
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

/**
 * @author Kirill Bugrim
 * @version 1.0
 */

@Service
public class MonitorJobService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MonitorJobService.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    private MonitorMapper monitorMapper;


    @PostConstruct
    public void init() {
        List<Monitor> monitors = monitorMapper.getAllMonitors();
        for (Monitor monitor : monitors) {
            addJob(monitor);
        }
    }

    public void addJob(Monitor monitor) {
        JobDetail jobDetail = JobBuilder.newJob(MonitorEmailNotificationTask.class)
                .withIdentity(monitor.getId().toString(), "monitorJobGroup")
                .storeDurably(true).build();

        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setName(monitor.getId().toString());
        trigger.setGroup("monitorTriggerGroup");

        try {
            trigger.setCronExpression(monitor.getCronExpression());
        } catch (ParseException e) {
            LOGGER.info("Can't  set cron expression!");
        }

        try {
            schedulerFactoryBean.getScheduler().getContext().put(jobDetail.getKey().getName(), monitor);
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            LOGGER.info("Can't  schedule job!");
        }
    }


    public void deleteJob(long id) {
//        List<Monitor> monitors = monitorMapper.getAllMonitors();
//        Monitor monitor = null;
//        for (Monitor monitor1 : monitors) {
//            if (monitor1.getId() == id) {
//                monitor = monitor1;
//                break;
//            }
//        }

        try {
            schedulerFactoryBean.getScheduler().deleteJob(findJobKey(String.valueOf(id)));
        } catch (SchedulerException e) {
           LOGGER.info("Can't delete job");
        }
    }


    public void updateExistingJob(Monitor monitor) {

        JobDetail job = JobBuilder.newJob(MonitorEmailNotificationTask.class)
                .withIdentity(monitor.getId().toString(), "monitorJobGroup")
                .storeDurably(true).build();
        try {
            schedulerFactoryBean.getScheduler().getContext().put(job.getKey().getName(), monitor);
            schedulerFactoryBean.getScheduler().addJob(job, true);
        } catch (SchedulerException e) {
            LOGGER.info("Can't  schedule job!");
        }

    }


    public void updateExistingTrigger(Monitor monitor) {

        Trigger oldTrigger = null;
        try {
            oldTrigger = schedulerFactoryBean.getScheduler().getTrigger(triggerKey(monitor.getId().toString(), "monitorTriggerGroup"));
        } catch (SchedulerException e) {
            LOGGER.info("Can't get old trigger!");
        }

        CronTriggerImpl newTrigger = new CronTriggerImpl();
        newTrigger.setName(monitor.getId().toString());
        newTrigger.setGroup("monitorTriggerGroup");
        try {
            newTrigger.setCronExpression(monitor.getCronExpression());
        } catch (ParseException e) {
            LOGGER.info("Can't set cron expression!");
        }

        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(oldTrigger.getKey(), newTrigger);
        } catch (SchedulerException e) {
            LOGGER.info("Can't reschedule trigger!");
        }
    }


    public void updateMonitor(Monitor monitor) {
        updateExistingJob(monitor);
        updateExistingTrigger(monitor);
    }


    public JobKey findJobKey(String jobName) throws SchedulerException {
        // Check running jobs first
        for (JobExecutionContext runningJob : schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs()) {
            if (Objects.equals(jobName, runningJob.getJobDetail().getKey().getName())) {
                return runningJob.getJobDetail().getKey();
            }
        }
        // Check all jobs if not found
        for (String groupName : schedulerFactoryBean.getScheduler().getJobGroupNames()) {
            for (JobKey jobKey : schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                if (Objects.equals(jobName, jobKey.getName())) {
                    return jobKey;
                }
            }
        }
        return null;
    }

}
