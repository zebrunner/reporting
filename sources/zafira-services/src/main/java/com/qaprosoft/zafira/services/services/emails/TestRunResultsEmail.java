package com.qaprosoft.zafira.services.services.emails;

import com.qaprosoft.zafira.dbaccess.model.*;
import com.qaprosoft.zafira.dbaccess.model.config.Argument;
import com.qaprosoft.zafira.dbaccess.model.config.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRunResultsEmail implements IEmailMessage
{
	private static final String SUBJECT = "%s: %s %s (%s) on %s %s";
	private static final String TEMPLATE = "test_run_results.ftl";

	private Map<String, String> configuration = new HashMap<String, String>();
	private TestRun testRun;
	private List<Test> tests;
	private String jiraURL;
	private boolean showOnlyFailures = false;
	private int successRate;
	private String elapsed;

	public TestRunResultsEmail(Configuration config, TestRun testRun, List<Test> tests)
	{
		for (Argument arg : config.getArg())
		{
			configuration.put(arg.getKey(), arg.getValue());
		}
		this.testRun = testRun;
		this.tests = tests;
		if(testRun.getElapsed() != null)
		{
			int s = testRun.getElapsed() % 60;
			int m = (testRun.getElapsed() / 60) % 60;
			int h = (testRun.getElapsed() / (60 * 60)) % 24;
			this.elapsed = String.format("%02d:%02d:%02d", h,m,s);
		}
	}

	public Map<String, String> getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(Map<String, String> configuration)
	{
		this.configuration = configuration;
	}

	public TestRun getTestRun()
	{
		return testRun;
	}

	public void setTestRun(TestRun testRun)
	{
		this.testRun = testRun;
	}

	public List<Test> getTests()
	{
		return tests;
	}

	public void setTests(List<Test> tests)
	{
		this.tests = tests;
	}

	public String getJiraURL()
	{
		return jiraURL;
	}

	public void setJiraURL(String jiraURL)
	{
		this.jiraURL = jiraURL;
	}

	public void setJiraURL(Setting setting)
	{
		this.jiraURL = setting != null ? setting.getValue() : "";
	}

	public boolean isShowOnlyFailures()
	{
		return showOnlyFailures;
	}

	public void setShowOnlyFailures(boolean showOnlyFailures)
	{
		this.showOnlyFailures = showOnlyFailures;
	}

	public int getSuccessRate()
	{
		return successRate;
	}

	public void setSuccessRate(int successRate)
	{
		this.successRate = successRate;
	}
	
	public String getElapsed()
	{
		return elapsed;
	}

	@Override
	public String getSubject()
	{
		String status = Status.PASSED.equals(testRun.getStatus()) && testRun.isKnownIssue() ? "PASSED (known issues)"
				: testRun.getStatus().name();
		String appVersion = argumentIsPresent("app_version")? configuration.get("app_version") + " - ": "";
		return String.format(SUBJECT, status, appVersion, testRun.getTestSuite().getName(), testRun.getTestSuite().getFileName(),
				configuration.get("env"), buildPlatformInfo());
	}

	private boolean argumentIsPresent(String arg, String... ignoreValues) {
		if("".equals(configuration.get(arg)) || configuration.get(arg).equalsIgnoreCase("null")) {
			return false;
		}
		for(String ignoreValue: ignoreValues) {
			if(configuration.get(arg).equals(ignoreValue)) {
				return false;
			}
		}
		return true;
	}

	private String buildPlatformInfo() {
		String platformInfo = "%s %s %s";
		String mobilePlatformVersion = argumentIsPresent("mobile_platform_name")? configuration.get("mobile_platform_name"): "";
		String browser = argumentIsPresent("browser")? configuration.get("browser"): "";
		String locale = argumentIsPresent("locale", "en_US")? configuration.get("locale"): "";
		platformInfo = String.format(platformInfo, mobilePlatformVersion, browser, locale);
		if(!platformInfo.replaceAll(" ", "").equals("()")) {
			platformInfo = platformInfo.trim();
			if(platformInfo.lastIndexOf("  ") != -1) {
				platformInfo = platformInfo.substring(0, platformInfo.lastIndexOf(" ")) + platformInfo.substring(platformInfo.lastIndexOf(" ") + 1);
			}
			platformInfo = "(" + platformInfo + ")";
			return platformInfo;
		}
		return "";
	}

	@Override
	public String getTemplate()
	{
		return TEMPLATE;
	}

	@Override
	public List<Attachment> getAttachments()
	{
		return null;
	}

	@Override
	public String getText()
	{
		return null;
	}
}
