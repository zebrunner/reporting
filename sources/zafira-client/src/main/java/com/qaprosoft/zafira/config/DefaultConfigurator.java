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
package com.qaprosoft.zafira.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.qaprosoft.zafira.models.dto.TagType;
import org.testng.ISuite;
import org.testng.ITestResult;

import com.qaprosoft.zafira.models.db.TestRun.DriverMode;
import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.config.ConfigurationType;

/**
 * Default implementation of Zafira {@link IConfigurator} used for more deep integration with test frameworks.
 * It should be enough to use default configurator to get base reporting functionality.
 * 
 * @author akhursevich
 */
public class DefaultConfigurator implements IConfigurator
{
	private static final String ANONYMOUS = "anonymous";
	
	@Override
	public ConfigurationType getConfiguration()
	{
		return new ConfigurationType();
	}

	@Override
	public String getOwner(ISuite suite)
	{
		return ANONYMOUS;
	}

	@Override
	public String getPrimaryOwner(ITestResult test)
	{
		return ANONYMOUS;
	}
	
	@Override
	public String getSecondaryOwner(ITestResult test)
	{
		return null;
	}

	@Override
	public String getTestName(ITestResult test)
	{
		return test.getName();
	}

	@Override
	public String getTestMethodName(ITestResult test)
	{
		return test.getMethod().getMethodName();
	}

	@Override
	public int getRunCount(ITestResult test)
	{
		return 0;
	}

	@Override
	public DriverMode getDriverMode()
	{
		return DriverMode.METHOD_MODE;
	}

	@Override
	public List<String> getTestWorkItems(ITestResult test)
	{
		return new ArrayList<>();
	}

	@Override
	public Map<String, Long> getTestMetrics(ITestResult test)
	{
		return null;
	}

	@Override
	public Set<TestArtifactType> getArtifacts(ITestResult test) 
	{
		return new HashSet<>();
	}

	@Override
	public Set<TagType> getTestTags(ITestResult test) {
		return new HashSet<>();
	}
}