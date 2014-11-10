package com.zafira.services.services;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.zafira.dbaccess.dao.mysql.JobMapper;
import com.zafira.dbaccess.model.Job;
import com.zafira.services.exceptions.ServiceException;
import com.zafira.services.util.URLFormatUtil;

@Service
public class JobsService
{
	private static String VIEW_PATTER = "/view/";
	private static String JOB_PATTER = "/job/";
	
	@Autowired
	private JobMapper jobMapper;
	
	@Value("${zafira.jenkins.username}")
	private String jenkinsUsername;
	
	@Value("${zafira.jenkins.password}")
	private String jenkinsPassword;
	
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
	public Job getJobByJobName(String name) throws ServiceException
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
	public Job initiateJob(Job j) throws ServiceException
	{
		try
		{
			if(StringUtils.isEmpty(j.getJobURL())) throw new Exception("Invalid job URL!");
			
			Job job = getJobByJobName(getJobName(j.getJobURL()));
			if(job == null)
			{
				JenkinsServer jenkins = new JenkinsServer(new URI(getJenkinsHost(j.getJobURL())), jenkinsUsername, jenkinsPassword);
				JobWithDetails jobDetails = jenkins.getJob(getJobName(j.getJobURL()));
				
				job = new Job();
				job.setJenkinsHost(URLFormatUtil.normalize(getJenkinsHost(j.getJobURL())));
				job.setJobURL(URLFormatUtil.normalize(jobDetails.getUrl()));
				job.setName(jobDetails.getName());
				
				createJob(job);
			}
			
			return job;
		}
		catch(Exception e)
		{
			throw new ServiceException(e.getMessage());
		}
	}
	
	private String getJenkinsHost(String jobURL)
	{
		return jobURL.contains(VIEW_PATTER) ? jobURL.split(VIEW_PATTER)[0] : jobURL.split(JOB_PATTER)[0];
	}
	
	private String getJobName(String jobURL)
	{
		return jobURL.split(JOB_PATTER)[1].replaceAll("/", "");
	}
}
