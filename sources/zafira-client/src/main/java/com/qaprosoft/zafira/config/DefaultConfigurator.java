package com.qaprosoft.zafira.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ITestResult;

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
	public String getOwner(ITestResult test)
	{
		return ANONYMOUS;
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
	public String getLogURL(ITestResult test)
	{
		return "";
	}

	@Override
	public String getDemoURL(ITestResult test)
	{
		return "";
	}

	@Override
	public int getRunCount(ITestResult test)
	{
		return 0;
	}

	@Override
	public boolean isClassMode()
	{
		return false;
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
}