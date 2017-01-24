package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.Job;

public interface JobMapper
{
	void createJob(Job job);

	Job getJobById(long id);

	Job getJobByName(String name);
	
	Job getJobByJobURL(String jobURL);

	void updateJob(Job job);

	void deleteJobById(long id);

	void deleteJob(Job job);
}
