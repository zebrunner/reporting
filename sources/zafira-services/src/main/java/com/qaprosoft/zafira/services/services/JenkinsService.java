package com.qaprosoft.zafira.services.services;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import com.offbytwo.jenkins.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.offbytwo.jenkins.JenkinsServer;
import com.qaprosoft.zafira.models.db.Job;

@Service
public class JenkinsService
{
	private static Logger LOGGER = LoggerFactory.getLogger(JenkinsService.class);

	private final String FOLDER_REGEX = ".+job\\/.+\\/job.+";

	private JenkinsServer server;

	public JenkinsService(String url, String username, String password)
	{
		try
		{
			if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(username) && !StringUtils.isEmpty(password))
			{
				this.server = new JenkinsServer(new URI(url), username, password);
			}
		} catch (Exception e)
		{
			LOGGER.error("Unable to initialize Jenkins integration: " + e.getMessage());
		}
	}

	public boolean rerunJob(Job ciJob, Integer buildNumber, boolean rerunFailures)
	{
		boolean success = false;
		try
		{
			JobWithDetails job = getJobWithDetails(ciJob);

			Map<String, String> params = job.getBuildByNumber(buildNumber).details().getParameters();
			if (rerunFailures)
			{
				params.put("rerun_failures", "true");
			}
			QueueReference reference = job.build(params, true);
			success = reference != null && !StringUtils.isEmpty(reference.getQueueItemUrlPart());
		} catch (Exception e)
		{
			LOGGER.error("Unable to rerun Jenkins job:  " + e.getMessage());
		}
		return success;
	}

	public boolean buildJob(Job ciJob, Integer buildNumber, Map<String, String> jobParameters,
			boolean buildWithParameters)
	{
		boolean success = false;
		if (buildWithParameters)
		{
			try
			{
				JobWithDetails job = getJobWithDetails(ciJob);
				QueueReference reference = job.build(jobParameters, true);
				success = reference != null && !StringUtils.isEmpty(reference.getQueueItemUrlPart());
			} catch (Exception e)
			{
				LOGGER.error("Unable to run Jenkins job:  " + e.getMessage());
			}
		} else
		{
			success = rerunJob(ciJob, buildNumber, false);
		}
		return success;
	}

	public Map<String, String> getBuildParameters(Job ciJob, Integer buildNumber)
	{
		Map<String, String> jobParameters = null;
		try
		{
			JobWithDetails job = getJobWithDetails(ciJob);
			jobParameters = job.getBuildByNumber(buildNumber).details().getParameters();
			jobParameters.put("ci_run_id", UUID.randomUUID().toString());
		} catch (Exception e)
		{
			LOGGER.error("Unable to get job:  " + e.getMessage());
		}
		return jobParameters;
	}

	private JobWithDetails getJobWithDetails(Job ciJob) throws IOException
	{
		JobWithDetails job = null;
		if (ciJob.getJobURL().matches(FOLDER_REGEX))
		{
			String folderName = ciJob.getJobURL().split("/job/")[1];
			Optional<FolderJob> folder = server.getFolderJob(server.getJob(folderName));
			job = server.getJob(folder.get(), ciJob.getName());
		} else
		{
			job = server.getJob(ciJob.getName());
		}
		return job;
	}

	public boolean isConnected()
	{
		return this.server != null && this.server.isRunning();
	}
}