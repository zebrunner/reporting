package com.qaprosoft.zafira.tests.services.api.builders;

import com.qaprosoft.zafira.models.dto.JobType;

public class JobTypeBuilder extends AbstractTypeBuilder<JobType>
{

	private JobType jobType = new JobType()
	{
		private static final long serialVersionUID = -5143637654708857466L;
		{
			setName("unavailable");
			setJobURL(ZAFIRA_URL.split("/")[0] + "/job/unavailable");
			setJenkinsHost(ZAFIRA_URL.split("/")[0]);
			setUserId(userId);
		}
	};

	@Override
	public JobType getInstance()
	{
		return this.jobType;
	}

	@Override
	public JobType register()
	{
		this.jobType = zafiraClient.createJob(jobType).getObject();
		return this.jobType;
	}

	public JobType getJobType()
	{
		return jobType;
	}

	public void setJobType(JobType jobType)
	{
		this.jobType = jobType;
	}
}
