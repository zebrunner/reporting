package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import com.qaprosoft.zafira.models.db.Job;

public interface JobMapper
{
	void createJob(Job job);
	
	List<Job> getAllJobs();

	Job getJobById(long id);

	Job getJobByName(String name);
	
	Job getJobByJobURL(String jobURL);

	void updateJob(Job job);

	void deleteJobById(long id);

	void deleteJob(Job job);
}
