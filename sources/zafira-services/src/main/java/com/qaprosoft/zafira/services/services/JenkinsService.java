package com.qaprosoft.zafira.services.services;

import java.net.URI;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.QueueReference;

@Service
public class JenkinsService
{
	private static Logger LOGGER = LoggerFactory.getLogger(JenkinsService.class);

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
	
	public boolean rerunJob(String jobName, Integer buildNumber, boolean rerunFailures)
	{
		boolean success = false;
		try
		{
			Map<String, String> params = server.getJob(jobName).getBuildByNumber(buildNumber).details().getParameters();
			if(rerunFailures)
			{
				params.put("rerun_failures", "true");
			}
			QueueReference reference = server.getJob(jobName).build(params, true);
			success = reference != null && !StringUtils.isEmpty(reference.getQueueItemUrlPart());
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to rerun Jenkins job:  " + e.getMessage());
		}
		return success;
	}
}