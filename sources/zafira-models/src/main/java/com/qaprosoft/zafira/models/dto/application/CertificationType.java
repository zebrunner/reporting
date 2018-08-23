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
package com.qaprosoft.zafira.models.dto.application;

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
