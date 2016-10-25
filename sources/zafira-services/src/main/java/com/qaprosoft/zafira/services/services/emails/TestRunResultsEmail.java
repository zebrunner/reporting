package com.qaprosoft.zafira.services.services.emails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qaprosoft.zafira.dbaccess.model.Setting;
import com.qaprosoft.zafira.dbaccess.model.Status;
import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.TestRun;
import com.qaprosoft.zafira.dbaccess.model.config.Argument;
import com.qaprosoft.zafira.dbaccess.model.config.Configuration;


public class TestRunResultsEmail implements IEmailMessage
{
	private static final String SUBJECT = "%s: %s (%s) on %s";
	private static final String TEMPLATE = "test_run_results.ftl";

	private Map<String, String> configuration = new HashMap<String, String>();
	private TestRun testRun;
	private List<Test> tests;
	private String jiraURL;
	
	public TestRunResultsEmail(Configuration config, TestRun testRun, List<Test> tests, Setting jiraURLSetting)
	{
		for(Argument arg : config.getArg())
		{
			configuration.put(arg.getKey(), arg.getValue());
		}
		this.testRun = testRun;
		this.tests = tests;
		this.jiraURL = jiraURLSetting != null ? jiraURLSetting.getValue() : "";
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
	
	public String getJiraURL() {
		return jiraURL;
	}

	public void setJiraURL(String jiraURL) {
		this.jiraURL = jiraURL;
	}

	@Override
	public String getSubject()
	{
		String status = Status.PASSED.equals(testRun.getStatus()) && testRun.isKnownIssue() ? "PASSED (known issues)" : testRun.getStatus().name();
		return String.format(SUBJECT, status, testRun.getTestSuite().getName(), testRun.getTestSuite().getFileName(), configuration.get("env"));
	}

	@Override
	public String getTemplate()
	{
		return TEMPLATE;
	}
}
