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
package com.qaprosoft.zafira.services.services;

import java.util.List;

import com.qaprosoft.zafira.models.db.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.JobMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.JobViewMapper;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.db.JobView;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class JobsService
{
	@Autowired
	private JobMapper jobMapper;
	
	@Autowired
	private JobViewMapper jobViewMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createJob(Job job) throws ServiceException
	{
		jobMapper.createJob(job);
	}

	//Check the same logics in ZafiraClient method registerJob
	@Transactional(rollbackFor = Exception.class)
	public Job createOrUpdateJobByURL(String jobUrl, User user) throws ServiceException
	{
		if (jobUrl.charAt(jobUrl.length() - 1) == '/')
		{
			jobUrl = StringUtils.chop(jobUrl);
		}
		String jobName = StringUtils.substringAfterLast(jobUrl, "/");
		String jenkinsHost = StringUtils.EMPTY;
		if(jobUrl.contains("/view/"))
		{
			jenkinsHost = jobUrl.split("/view/")[0];
		}
		else if(jobUrl.contains("/job/"))
		{
			jenkinsHost = jobUrl.split("/job/")[0];
		}
		Job job = new Job(jobName, jobUrl, jenkinsHost, user);
		createOrUpdateJob(job);
		return job;
	}

	@Transactional(readOnly = true)
	public List<Job> getAllJobs() throws ServiceException
	{
		return jobMapper.getAllJobs();
	}
	
	@Transactional(readOnly = true)
	public Job getJobById(long id) throws ServiceException
	{
		return jobMapper.getJobById(id);
	}
	
	@Transactional(readOnly = true)
	public Job getJobByName(String name) throws ServiceException
	{
		return jobMapper.getJobByName(name);
	}

	@Transactional(readOnly = true)
	public Job getJobByJobURL(String url) throws ServiceException
	{
		return jobMapper.getJobByJobURL(url);
	}

	@Transactional(rollbackFor = Exception.class)
	public Job updateJob(Job job) throws ServiceException
	{
		jobMapper.updateJob(job);
		return job;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteJob(Job job) throws ServiceException
	{
		jobMapper.deleteJob(job);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Job createOrUpdateJob(Job newJob) throws ServiceException
	{
		Job job = getJobByName(newJob.getName());
		if(job == null )
		{
			createJob(newJob);
		}
		else if(!job.equals(newJob))
		{
			newJob.setId(job.getId());
			updateJob(newJob);
		}
		else
		{
			newJob = job;
		}
		return newJob;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public JobView createJobView(JobView jobView) throws ServiceException
	{
		jobViewMapper.createJobView(jobView);
		return jobView;
	}
	
	@Transactional(readOnly = true)
	public List<JobView> getJobViewsByViewId(long viewId) throws ServiceException
	{
		return jobViewMapper.getJobViewsByViewId(viewId);
	}
	
	@Transactional(readOnly = true)
	public List<JobView> getJobViewsByViewIdAndEnv(long viewId, String env) throws ServiceException
	{
		return jobViewMapper.getJobViewsByViewIdAndEnv(viewId, env);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteJobViews(long viewId, String env) throws ServiceException
	{
		jobViewMapper.deleteJobViewsByViewIdAndEnv(viewId, env);
	}
}
