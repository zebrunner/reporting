package com.qaprosoft.zafira.models.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CertificationType
{
	private Set<String> platforms = new TreeSet<>();
	
	private Set<String> steps = new TreeSet<>();
	
	private Map<String, Map<String, String>> screenshots = new HashMap<>();
	
	public void addScreenshot(String step, String platform, String url)
	{
		if(step == null || step.isEmpty())
		{
			return;
		}
		
		platforms.add(platform);
		steps.add(step);
		if(!screenshots.containsKey(platform))
		{
			screenshots.put(platform, new HashMap<String, String>());
		}
		screenshots.get(platform).put(step, url);
	}

	public Set<String> getPlatforms()
	{
		return platforms;
	}

	public Set<String> getSteps()
	{
		return steps;
	}

	public Map<String, Map<String, String>> getScreenshots()
	{
		return screenshots;
	}
	
	public void sort()
	{
		
	}
}
