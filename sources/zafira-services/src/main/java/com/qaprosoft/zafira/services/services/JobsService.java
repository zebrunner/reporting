package com.qaprosoft.zafira.services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.JobMapper;
import com.qaprosoft.zafira.dbaccess.model.Job;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class JobsService
{
	@Autowired
	private JobMapper jobMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createJob(Job job) throws ServiceException
	{
		jobMapper.createJob(job);
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
}
