/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.JobMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.JobViewMapper;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.db.JobView;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.service.integration.IntegrationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobsService {

    private static final String  INTEGRATION_TYPE_NAME = "JENKINS";

    private final JobMapper jobMapper;
    private final JobViewMapper jobViewMapper;
    private final IntegrationService integrationService;
    private final UserService userService;

    public JobsService(JobMapper jobMapper, JobViewMapper jobViewMapper, IntegrationService integrationService, UserService userService) {
        this.jobMapper = jobMapper;
        this.jobViewMapper = jobViewMapper;
        this.integrationService = integrationService;
        this.userService = userService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createJob(Job job) {
        jobMapper.createJob(job);
    }

    // Check the same logic in ZafiraClient method registerJob
    @Transactional(rollbackFor = Exception.class)
    public Job createOrUpdateJobByURL(String jobUrl, long userId) {
        Job job = createJobFromURL(jobUrl, userId);
        return createOrUpdateJob(job);
    }

    private Job createJobFromURL(String jobUrl, long userId) {
        User user = userService.getUserById(userId);
        jobUrl = jobUrl.replaceAll("/$", "");
        String jobName = StringUtils.substringAfterLast(jobUrl, "/");
        String jenkinsHost = parseJenkinsHost(jobUrl);
        return new Job(jobName, jobUrl, jenkinsHost, user);
    }

    private String parseJenkinsHost(String jobUrl) {
        String jenkinsHost = StringUtils.EMPTY;
        if (jobUrl.contains("/view/")) {
            jenkinsHost = jobUrl.split("/view/")[0];
        } else if (jobUrl.contains("/job/")) {
            jenkinsHost = jobUrl.split("/job/")[0];
        }
        return jenkinsHost;
    }

    @Transactional(readOnly = true)
    public List<Job> getAllJobs() {
        return jobMapper.getAllJobs();
    }

    @Transactional(readOnly = true)
    public Job getJobByJobURL(String url) {
        return jobMapper.getJobByJobURL(url);
    }

    @Transactional(rollbackFor = Exception.class)
    public Job updateJob(Job job) {
        jobMapper.updateJob(job);
        return job;
    }

    @Transactional(rollbackFor = Exception.class)
    public Job createOrUpdateJob(Job newJob) {
        Integration integration = integrationService.retrieveByJobAndIntegrationTypeName(newJob, INTEGRATION_TYPE_NAME);
        newJob.setAutomationServerId(integration.getId());
        Job job = getJobByJobURL(newJob.getJobURL());
        if (job == null) {
            createJob(newJob);
        } else if (!job.equals(newJob)) {
            newJob.setId(job.getId());
            updateJob(newJob);
        } else {
            newJob = job;
        }
        return newJob;
    }

    @Transactional(rollbackFor = Exception.class)
    public JobView createJobView(JobView jobView) {
        jobViewMapper.createJobView(jobView);
        return jobView;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createJobViews(List<JobView> jobViews) {
        jobViews.forEach(jobViewMapper::createJobView);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateJobViews(List<JobView> jobViews, long viewId, String env) {
        deleteJobView(viewId, env);
        createJobViews(jobViews);
    }

    @Transactional(readOnly = true)
    public List<JobView> getJobViewsByViewId(long viewId) {
        return jobViewMapper.getJobViewsByViewId(viewId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteJobView(long viewId, String env) {
        jobViewMapper.deleteJobViewsByViewIdAndEnv(viewId, env);
    }
}
