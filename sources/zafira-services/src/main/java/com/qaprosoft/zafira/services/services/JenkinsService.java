package com.qaprosoft.zafira.services.services;

import java.net.URI;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;
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
				server = new JenkinsServer(new URI(url), username, password);
			}
		} 
		catch (Exception e)
		{
			LOGGER.error("Unable to initialize Jenkins integration: " + e.getMessage());
		}
	}

	public boolean isRunning()
	{
		boolean running = false;
		try
		{
			running = server != null && server.isRunning();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to check if Jenkins is running:  " + e.getMessage());
		}
		return running;
	}
	
	public boolean rerunJob(Job ciJob, Integer buildNumber, boolean rerunFailures)
	{
		boolean success = false;
		try
		{
			JobWithDetails job = null;
			if(ciJob.getJobURL().matches(FOLDER_REGEX))
			{
				String folderName = ciJob.getJobURL().split("/job/")[1];
				Optional<FolderJob> folder = server.getFolderJob(server.getJob(folderName));
				job = server.getJob(folder.get(), ciJob.getName());
			}
			else
			{
				job = server.getJob(ciJob.getName());
			}
			
			Map<String, String> params = job.getBuildByNumber(buildNumber).details().getParameters();
			if(rerunFailures)
			{
				params.put("rerun_failures", "true");
			}
			QueueReference reference = job.build(params, true);
			success = reference != null && !StringUtils.isEmpty(reference.getQueueItemUrlPart());
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to rerun Jenkins job:  " + e.getMessage());
		}
		return success;
	}
}