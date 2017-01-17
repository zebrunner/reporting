package com.qaprosoft.zafira.listener;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.client.ZafiraClient.Response;
import com.qaprosoft.zafira.client.model.JobType;
import com.qaprosoft.zafira.client.model.TestCaseType;
import com.qaprosoft.zafira.client.model.TestRunType;
import com.qaprosoft.zafira.client.model.TestRunType.Initiator;
import com.qaprosoft.zafira.client.model.TestSuiteType;
import com.qaprosoft.zafira.client.model.TestType;
import com.qaprosoft.zafira.client.model.TestType.Status;
import com.qaprosoft.zafira.client.model.UserType;
import com.qaprosoft.zafira.client.model.config.Configuration;

public class ZafiraListener implements ISuiteListener, ITestListener
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ZafiraListener.class);
	
	private static final String ZAFIRA_PROPERTIES = "zafira.properties";
	
	private static final String ANONYMOUS = "anonymous";
	
	private static final String ZAFIRA_PROJECT_PARAM = "zafira_project";
	private static final String ZAFIRA_RUN_ID_PARAM = "zafira_run_id";
	
	private boolean ZAFIRA_ENABLED = false;
	private String 	ZAFIRA_URL = null;
	private String 	ZAFIRA_USERNAME = null;
	private String 	ZAFIRA_PASSWORD = null;
	private String 	ZAFIRA_PROJECT = null;
	private String 	ZAFIRA_REPORT_EMAILS = null;
	private boolean ZAFIRA_RERUN_FAILURES = false;
	private String 	ZAFIRA_CONFIGURATOR = null;
	
	private String CI_RUN_ID = null;
	private String CI_URL = null;
	private Integer CI_BUILD = null;
	private String CI_BUILD_CAUSE = null;
	private String CI_PARENT_URL = null;
	private Integer CI_PARENT_BUILD = null;
	private String CI_USER_ID = null;
	private String CI_USER_FIRST_NAME = null;
	private String CI_USER_LAST_NAME = null;
	private String CI_USER_EMAIL = null;

	private String GIT_BRANCH = null;
	private String GIT_COMMIT = null;
	private String GIT_URL = null;
	
	private String JIRA_SUITE_ID = null;
	
	private ZafiraClient zc;
	
	private IConfigurator configurator;
	
	private final ConcurrentHashMap<Long, TestType> testByThread = new ConcurrentHashMap<Long, TestType>();
	
	private UserType user = null;
	private JobType parentJob = null;
	private JobType job = null;
	private TestSuiteType suite = null;
	private TestRunType run = null;
	private Map<String, TestType> registeredTests = new HashMap<>();
	
	private Marshaller marshaller;
	
	@Override
	public void onStart(ISuite suiteСontext)
	{
		if(initializeZafira())
		{
			try
			{
				marshaller = JAXBContext.newInstance(Configuration.class).createMarshaller();
				
				configurator = (IConfigurator) Class.forName(ZAFIRA_CONFIGURATOR).newInstance();
				
				// Override project if specified in XML
				String project = suiteСontext.getXmlSuite().getParameter(ZAFIRA_PROJECT_PARAM);
				zc.setProject(!StringUtils.isEmpty(project) ? project : ZAFIRA_PROJECT);
				
				// Register user who initiated test run
				this.user = registerUser(CI_USER_ID, CI_USER_EMAIL, CI_USER_FIRST_NAME, CI_USER_LAST_NAME);
		
				// Register test suite along with suite owner
				UserType suiteOwner = registerUser(configurator.getOwner(suiteСontext), null, null, null);
				this.suite = registerTestSuite(suiteСontext.getName(), FilenameUtils.getName(suiteСontext.getXmlSuite().getFileName()), suiteOwner.getId());
				
				// Register job that triggers test run
				this.job = registerJob(CI_URL, suiteOwner.getId());
				
				// Register upstream job if required
				UserType anonymous = null;
				if (CI_BUILD_CAUSE.toUpperCase().contains("UPSTREAMTRIGGER")) 
				{
					anonymous = registerUser(ANONYMOUS, null, null, null);
					parentJob = registerJob(CI_PARENT_URL, anonymous.getId());
				}
				
				// Searching for existing test run with same CI run id in case of rerun
				if(!StringUtils.isEmpty(CI_RUN_ID)) 
				{
					Response<TestRunType> response = zc.getTestRunByCiRunId(CI_RUN_ID);
					this.run = response.getObject();
				}
				
				if (this.run != null) 
				{
					// Already discovered run with the same CI_RUN_ID, it is re-run functionality!
					// Reset build number for re-run to map to the latest rerun build 
					this.run.setBuildNumber(CI_BUILD);
					// Re-register test run to reset status onto in progress
					Response<TestRunType> response = zc.startTestRun(this.run);
					this.run = response.getObject();
					
					for(TestType test : Arrays.asList(zc.getTestRunResults(run.getId()).getObject()))
					{
						registeredTests.put(test.getName(), test);
					}
				} 
				else 
				{
					if(ZAFIRA_RERUN_FAILURES) 
					{
						LOGGER.error("Unable to find data in Zafira Reporting Service with CI_RUN_ID: '" + CI_RUN_ID + "'.\n" + "Rerun failures featrure will be disabled!");
						ZAFIRA_RERUN_FAILURES = false;
					}
					// Register new test run
					if (CI_BUILD_CAUSE.toUpperCase().contains("UPSTREAMTRIGGER")) 
					{
						this.run = registerTestRunUPSTREAM_JOB(suite.getId(), convertToXML(configurator.getConfiguration()), job.getId(), parentJob.getId(), CI_PARENT_BUILD, CI_BUILD, Initiator.UPSTREAM_JOB, JIRA_SUITE_ID);
					} 
					else if(CI_BUILD_CAUSE.toUpperCase().contains("TIMERTRIGGER")) 
					{
						this.run = registerTestRunBySCHEDULER(suite.getId(), convertToXML(configurator.getConfiguration()), job.getId(), CI_BUILD, Initiator.SCHEDULER, JIRA_SUITE_ID);
					} 
					else if (CI_BUILD_CAUSE.toUpperCase().contains("MANUALTRIGGER")) 
					{
						this.run = registerTestRunByHUMAN(suite.getId(), user.getId(), convertToXML(configurator.getConfiguration()), job.getId(), CI_BUILD, Initiator.HUMAN, JIRA_SUITE_ID);
					}
					else 
					{
						throw new RuntimeException("Unable to register test run for zafira service: " + ZAFIRA_URL + " due to the misses build cause: '" + CI_BUILD_CAUSE + "'");
					}
				}
				
				if (this.run == null) 
				{
					throw new RuntimeException("Unable to register test run for zafira service: " + ZAFIRA_URL);
				}
				else
				{
					System.setProperty(ZAFIRA_RUN_ID_PARAM, String.valueOf(this.run.getId()));
				}
			}
			catch (Exception e) 
			{
				ZAFIRA_ENABLED = false;
				LOGGER.error("Undefined error during test run registration!", e);
			}
		}
	}
	
	@Override
	public void onTestStart(ITestResult result)
	{
		if(!ZAFIRA_ENABLED) return;
		
		try 
		{
			TestType startedTest = null;
			
			String testName = configurator.getTestName(result);

			// If method owner is not specified then try to use suite owner. If both are not declared then ANONYMOUS will be used.
			String owner = !StringUtils.isEmpty(configurator.getOwner(result)) ? configurator.getOwner(result) : configurator.getOwner(result.getTestContext().getSuite());
			UserType methodOwner = registerUser(owner, null, null, null);
			LOGGER.debug("methodOwner: " + methodOwner);

			TestCaseType testCase = registerTestCase(result);

			// Search already registered test!
			if(registeredTests.containsKey(testName))
			{
				startedTest = registeredTests.get(testName);
				
				// Skip already passed tests if rerun failures enabled
				if(ZAFIRA_RERUN_FAILURES && !startedTest.isNeedRerun())
				{
					throw new SkipException("ALREADY_PASSED: " + testName);
				}
				
				startedTest.setFinishTime(null);
				startedTest.setStartTime(new Date().getTime());
				startedTest = registerTestRestart(startedTest);
			}
			
			if (startedTest == null) 
			{
				//new test run registration
				String testArgs = result.getParameters().toString();
				
				String group = result.getMethod().getTestClass().getName();
				group = group.substring(0, group.lastIndexOf("."));

				startedTest = registerTestStart(testName, group, Status.IN_PROGRESS, testArgs, run.getId(), testCase.getId(), configurator.getDemoURL(result), configurator.getLogURL(result), configurator.getRunCount(result));
			}
			
			registerWorkItems(startedTest.getId(), configurator.getTestWorkItems(result));
			// TODO: investigate why we need it
			testByThread.put(Thread.currentThread().getId(), startedTest);
			
			registeredTests.put(testName, startedTest);
		} 
		catch(SkipException e)
		{
			throw e;
		}
		catch (Exception e) 
		{
			LOGGER.error("Undefined error during test case/method start!", e);
		}
	}
	
	@Override
	public void onTestSuccess(ITestResult result)
	{
		if(!ZAFIRA_ENABLED) return;

		try 
		{
			registerTestResults(result, Status.PASSED, null, new Date().getTime());
		} 
		catch (Exception e) {
			LOGGER.error("Undefined error during test case/method finish!", e);
		}
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result)
	{
		if(!ZAFIRA_ENABLED) return;
		
		try 
		{
			registerTestResults(result, Status.FAILED, getFullStackTrace(result), new Date().getTime());
		} 
		catch (Exception e) {
			LOGGER.error("Undefined error during test case/method finish!", e);
		}
	}

	@Override
	public void onTestFailure(ITestResult result)
	{
		if(!ZAFIRA_ENABLED) return;
		
		try 
		{
			registerTestResults(result, Status.FAILED, getFullStackTrace(result), new Date().getTime());
		} 
		catch (Exception e) {
			LOGGER.error("Undefined error during test case/method finish!", e);
		}
	}

	@Override
	public void onTestSkipped(ITestResult result)
	{
		if(!ZAFIRA_ENABLED) return;
		
		if (result.getThrowable() != null && result.getThrowable().getMessage() != null && result.getThrowable().getMessage().startsWith("ALREADY_PASSED")) 
		{
			return;
		}
		
		try 
		{
			registerTestResults(result, Status.SKIPPED, getFullStackTrace(result), new Date().getTime());
		} 
		catch (Exception e) {
			LOGGER.error("Undefined error during test case/method finish!", e);
		}
	}
	
	@Override
	public void onFinish(ISuite suiteContext)
	{
		if(!ZAFIRA_ENABLED) return;
		
		try 
		{
			registerTestRunResults();
			zc.sendTestRunReport(this.run.getId(), ZAFIRA_REPORT_EMAILS, false);
		} 
		catch (Exception e) 
		{
			LOGGER.error("Undefined error during test run finish!", e);
		}
	}
	
	
	@Override
	public void onStart(ITestContext context)
	{
		// Do nothing
	}
	
	@Override
	public void onFinish(ITestContext context)
	{
		// Do nothing
	}
	
	//==========================
	
	/**
	 * Reads zafira.properties and creates zafira client.
	 * 
	 * @return if initialization success
	 */
	private boolean initializeZafira()
	{
		boolean success = false;
		try
		{
			CombinedConfiguration config = new CombinedConfiguration(new MergeCombiner());
			config.setThrowExceptionOnMissing(true);
			config.addConfiguration(new SystemConfiguration());
			config.addConfiguration(new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				    					  .configure(new Parameters().properties().setFileName(ZAFIRA_PROPERTIES)).getConfiguration());

			CI_RUN_ID = config.getString("ci_run_id", UUID.randomUUID().toString());
			CI_URL = StringUtils.removeEnd(config.getString("ci_url", "http://localhost:8080/job/unavailable"), "/");
			CI_BUILD = config.getInteger("ci_build", 0);
			CI_BUILD_CAUSE = config.getString("ci_build_cause", "MANUALTRIGGER");
			CI_PARENT_URL = config.getString("ci_parent_url", null);
			CI_PARENT_BUILD = config.getInteger("ci_parent_build", 0);
			CI_USER_ID = config.getString("ci_user_id", ANONYMOUS);;
			CI_USER_FIRST_NAME = config.getString("ci_user_first_name", null);
			CI_USER_LAST_NAME = config.getString("ci_user_last_name", null);
			CI_USER_EMAIL = config.getString("ci_user_email", null);

			GIT_BRANCH = config.getString("git_branch", null);
			GIT_COMMIT = config.getString("git_commit", null);
			GIT_URL = config.getString("git_url", null);
			
			JIRA_SUITE_ID = config.getString("jira_suite_id", null);
			
			ZAFIRA_ENABLED = config.getBoolean("zafira_enabled", false);
			ZAFIRA_URL = config.getString("zafira_service_url");
			ZAFIRA_USERNAME = config.getString("zafira_username");
			ZAFIRA_PASSWORD = config.getString("zafira_password");
			ZAFIRA_PROJECT = config.getString("zafira_project");
			ZAFIRA_REPORT_EMAILS = config.getString("zafira_report_emails", "").trim().replaceAll(" ", ",").replaceAll(";", ",");
			ZAFIRA_RERUN_FAILURES = config.getBoolean("zafira_rerun_failures", false);
			ZAFIRA_CONFIGURATOR = config.getString("zafira_configurator", "com.qaprosoft.zafira.listener.DefaultConfigurator");
			
			if(ZAFIRA_ENABLED)
			{
				zc = new ZafiraClient(ZAFIRA_URL, ZAFIRA_USERNAME, ZAFIRA_PASSWORD);
				if(!zc.isAvailable())
				{
					ZAFIRA_ENABLED = false;
					LOGGER.error("Zafira server is unavailable!");
				}
			}
			
			success = ZAFIRA_ENABLED;
		}
		catch(ConfigurationException e)
		{
			LOGGER.error("Unable to locate "+ ZAFIRA_PROPERTIES +": ", e);
		}
		catch(NoSuchElementException e)
		{
			LOGGER.error("Unable to find config property: ", e);
		}
		
		return success;
	}
	
	/**
	 * Registers user in Zafira, it may be a new one or existing returned by service.
	 * 
	 * @param userName - in general LDAP user name
	 * @param email - corporate email
	 * @param firstName - human-readable first name
	 * @param lastName - human-readable last name
	 * @return created user
	 */
	private UserType registerUser(String userName, String email, String firstName, String lastName) 
	{
		if (StringUtils.isEmpty(userName) || userName.equals("$BUILD_USER_ID"))
		{
			userName = ANONYMOUS;
		}
		userName = userName.toLowerCase();
		
		String userDetails = "userName: %s, email: %s, firstName: %s, lastName: %s";
		LOGGER.debug("User details for registration:" + String.format(userDetails, userName, email, firstName, lastName));
		
		UserType user = new UserType(userName, email, firstName, lastName);
		Response<UserType> response = zc.createUser(user);
		user = response.getObject();

		if (user == null) 
		{
			throw new RuntimeException("Unable to register user '" + userName + "' for zafira service: " + ZAFIRA_URL);
		} 
		else 
		{
			LOGGER.debug("Registered user details:" + String.format(userDetails, user.getUserName(), user.getEmail(), user.getFirstName(), user.getLastName()));
		}
		return user;
	}
	
	/**
	 * Registers test suite in Zafira, it may be a new one or existing returned by service.
	 * 
	 * @param suiteName - test suite name
	 * @param fileName - TestNG xml file name
	 * @param userId - suite owner user id
	 * @return created test suite
	 */
	private TestSuiteType registerTestSuite(String suiteName, String fileName, Long userId) 
	{
		TestSuiteType testSuite = new TestSuiteType(suiteName, fileName, userId);
		String testSuiteDetails = "suiteName: %s, fileName: %s, userId: %s";
		LOGGER.debug("Test Suite details for registration:" + String.format(testSuiteDetails, suiteName, fileName, userId));
		
		Response<TestSuiteType> response = zc.createTestSuite(testSuite);
		testSuite = response.getObject();

		if (testSuite == null) 
		{
			throw new RuntimeException("Unable to register test suite '" + suiteName + "' for zafira service: " + ZAFIRA_URL);
		} 
		else 
		{
			LOGGER.debug("Registered test suite details:" + String.format(testSuiteDetails, testSuite.getName(), testSuite.getFileName(), testSuite.getUserId()));
		}
		return testSuite;
	}
	
	/**
	 * Registers job in Zafira, it may be a new one or existing returned by service.
	 * 
	 * @param jobUrl - CI job URL
	 * @param userId - job owner user id 
	 * @return created job
	 */
	private JobType registerJob(String jobUrl, Long userId) 
	{
		String jobName = StringUtils.substringAfterLast(jobUrl, "/");
		String jenkinsHost = StringUtils.EMPTY;
		if(jobUrl.contains("/view/"))
		{
			jenkinsHost = jobUrl.split("/view/")[0];
		}
		else if(jobUrl.contains("/job/"))
		{
			jenkinsHost = jobUrl.split("/job/")[0];
		}
		
		String jobDetails = "jobName: %s, jenkinsHost: %s, userId: %s";
		LOGGER.debug("Job details for registration:" + String.format(jobDetails, jobName, jenkinsHost, userId));
		
		JobType job = new JobType(jobName, jobUrl, jenkinsHost, userId);
		Response<JobType> response = zc.createJob(job);
		job = response.getObject();

		if (job == null) 
		{
			throw new RuntimeException("Unable to register job '" + CI_URL + "' for zafira service: " + ZAFIRA_URL);
		} else 
		{
			LOGGER.debug("Registered job details:" + String.format(jobDetails, job.getName(), job.getJenkinsHost(), job.getUserId()));
		}

		return job;
	}
	
	/**
	 * Registers new test run triggered by human.
	 * 
	 * @param testSuiteId
	 * @param userId
	 * @param configXML
	 * @param jobId
	 * @param buildNumber
	 * @param startedBy
	 * @param workItem
	 * @return created test run
	 */
	private TestRunType registerTestRunByHUMAN(Long testSuiteId, Long userId, String configXML, Long jobId, Integer buildNumber, Initiator startedBy, String workItem) 
	{
		
		TestRunType testRun = new TestRunType(CI_RUN_ID, testSuiteId, userId, GIT_URL, GIT_BRANCH, GIT_COMMIT, configXML, jobId, buildNumber, startedBy, workItem);
		testRun.setClassMode(configurator.isClassMode());
		String testRunDetails = "testSuiteId: %s, userId: %s, scmURL: %s, scmBranch: %s, scmCommit: %s, jobId: %s, buildNumber: %s, startedBy: %s, workItem";
		LOGGER.debug("Test Run details for registration:" + String.format(testRunDetails, testSuiteId, userId, GIT_URL, GIT_BRANCH, GIT_COMMIT, jobId, buildNumber, startedBy, workItem));
		
		Response<TestRunType> response = zc.startTestRun(testRun);
		testRun = response.getObject();
		if (testRun == null) 
		{
			throw new RuntimeException("Unable to register test run '" + String.format(testRunDetails, testSuiteId, userId,
					GIT_URL, GIT_BRANCH, GIT_COMMIT, jobId, buildNumber, startedBy, workItem) + "' for zafira service: " + ZAFIRA_URL);
		} else {
			LOGGER.debug("Registered test run details:" 
							+ String.format(testRunDetails, testSuiteId, userId, GIT_URL, GIT_BRANCH, GIT_COMMIT, jobId, buildNumber, startedBy, workItem));
		}
		return testRun;
	}

	/**
	 * Registers new test run triggered by scheduler.
	 * 
	 * @param testSuiteId
	 * @param configXML
	 * @param jobId
	 * @param buildNumber
	 * @param startedBy
	 * @param workItem
	 * @return created test run
	 */
	private TestRunType registerTestRunBySCHEDULER(Long testSuiteId, String configXML, Long jobId, Integer buildNumber, Initiator startedBy, String workItem) 
	{
		TestRunType testRun = new TestRunType(CI_RUN_ID, testSuiteId, GIT_URL, GIT_BRANCH, GIT_COMMIT, configXML, jobId, buildNumber, startedBy, workItem);
		testRun.setClassMode(configurator.isClassMode());
		String testRunDetails = "testSuiteId: %s, scmURL: %s, scmBranch: %s, scmCommit: %s, jobId: %s, buildNumber: %s, startedBy: %s, workItem";
		LOGGER.debug("Test Run details for registration:" + String.format(testRunDetails, testSuiteId, GIT_URL, GIT_BRANCH, GIT_COMMIT, jobId, buildNumber, startedBy, workItem));

		Response<TestRunType> response = zc.startTestRun(testRun);
		testRun = response.getObject();
		if (testRun == null) 
		{
			throw new RuntimeException("Unable to register test run '"
							+ String.format(testRunDetails, testSuiteId, GIT_URL, GIT_BRANCH, GIT_COMMIT, jobId, buildNumber, startedBy, workItem)
							+ "' for zafira service: " + ZAFIRA_URL);
		} 
		else 
		{
			LOGGER.debug("Registered test run details:" + String.format(testRunDetails, testSuiteId, GIT_URL, GIT_BRANCH, GIT_COMMIT, jobId, buildNumber, startedBy, workItem));
		}
		return testRun;
	}

	/**
	 * Registers new test run triggered by upstream job.
	 * 
	 * @param testSuiteId
	 * @param configXML
	 * @param jobId
	 * @param parentJobId
	 * @param parentBuildNumber
	 * @param buildNumber
	 * @param startedBy
	 * @param workItem
	 * @return created test run
	 */
	private TestRunType registerTestRunUPSTREAM_JOB(Long testSuiteId, String configXML, Long jobId, Long parentJobId, Integer parentBuildNumber, Integer buildNumber, Initiator startedBy, String workItem) 
	{
		TestRunType testRun = new TestRunType(CI_RUN_ID, testSuiteId, GIT_URL, GIT_BRANCH, GIT_COMMIT, configXML, jobId, parentJobId, parentBuildNumber,
				buildNumber, startedBy, workItem);
		testRun.setClassMode(configurator.isClassMode());
		String testRunDetails = "testSuiteId: %s, scmURL: %s, scmBranch: %s, scmCommit: %s, jobId: %s, parentJobId: %s, parentBuildNumber: %s, buildNumber: %s, startedBy: %s, workItem";
		LOGGER.debug("Test Run details for registration:"
				+ String.format(testRunDetails, testSuiteId, GIT_URL, GIT_BRANCH, GIT_COMMIT, jobId, parentJobId, parentBuildNumber, buildNumber, startedBy, workItem));

		Response<TestRunType> response = zc.startTestRun(testRun);
		testRun = response.getObject();
		if (testRun == null) 
		{
			throw new RuntimeException("Unable to register test run '"
					+ String.format(testRunDetails, testSuiteId, GIT_URL, GIT_BRANCH, GIT_COMMIT, jobId, parentJobId, parentBuildNumber, buildNumber, startedBy, workItem) 
							+ "' for zafira service: " + ZAFIRA_URL);
		} 
		else 
		{
			LOGGER.debug("Registered test run details:" + String.format(testRunDetails, testSuiteId, GIT_URL, GIT_BRANCH, GIT_COMMIT, jobId,parentJobId, parentBuildNumber, buildNumber, startedBy, workItem));
		}
		return testRun;
	}
	
	/**
	 * Registers test case in Zafira, it may be a new one or existing returned by service. 
	 * 
	 * @param  result
	 * @return created test case
	 */
	private TestCaseType registerTestCase(ITestResult result) 
	{
		String testClass = result.getMethod().getTestClass().getName();
		String testMethod = configurator.getTestMethodName(result);

		// If method owner is not specified then try to use suite owner. If both are not declared then ANONYMOUS will be used.
		String owner = !StringUtils.isEmpty(configurator.getOwner(result)) ? configurator.getOwner(result) : configurator.getOwner(result.getTestContext().getSuite());
		UserType methodOwner = registerUser(owner, null, null, null);
		
		TestCaseType testCase = new TestCaseType(testClass, testMethod, "", suite.getId(), methodOwner.getId());
		String testCaseDetails = "testClass: %s, testMethod: %s, info: %s, testSuiteId: %s, userId: %s";
		LOGGER.debug("Test Case details for registration:" + String.format(testCaseDetails, testClass, testMethod, "", suite.getId(), methodOwner.getId()));
		Response<TestCaseType> response = zc.createTestCase(testCase);
		testCase = response.getObject();
		if (testCase == null) 
		{
			throw new RuntimeException("Unable to register test case '"
					+ String.format(testCaseDetails, testClass, testMethod, "", suite.getId(), methodOwner.getId()) + "' for zafira service: " + ZAFIRA_URL);
		} 
		else 
		{
			LOGGER.debug("Registered test case details:" + String.format(testCaseDetails, testClass, testMethod, "", suite.getId(), methodOwner.getId()));
		}
		return testCase;
	}
	
	/**
	 * Updates test run statistics.
	 * 
	 * @return updated test run
	 * @throws JAXBException 
	 */
	private TestRunType registerTestRunResults() throws JAXBException 
	{
		// Reset configuration to store for example updated at run-time app_version etc
		this.run.setConfigXML(convertToXML(configurator.getConfiguration()));
		zc.updateTestRun(this.run); 
		
		Response<TestRunType> response = zc.finishTestRun(this.run.getId());
		return response.getObject();
	}
	
	/**
	 * Registers test run in Zafira.
	 * 
	 * @param name
	 * @param group
	 * @param status
	 * @param testArgs
	 * @param testRunId
	 * @param testCaseId
	 * @param demoURL
	 * @param logURL
	 * @param retry
	 * @return registered test
	 * @throws JAXBException
	 */
	private TestType registerTestStart(String name, String group, Status status, String testArgs, Long testRunId, Long testCaseId, String demoURL, String logURL, int retry) throws JAXBException 
	{
		Long startTime = new Date().getTime();

		String testDetails = "name: %s, status: %s, testArgs: %s, testRunId: %s, testCaseId: %s, startTime: %s, demoURL: %s, logURL: %s, retry: %d";

		TestType test = new TestType(name, status, testArgs, testRunId, testCaseId, startTime, demoURL, logURL, null, retry, convertToXML(configurator.getConfiguration()));
		LOGGER.debug("Test details for startup registration:" + String.format(testDetails, name, status, testArgs, testRunId, testCaseId, startTime, demoURL, logURL, retry));

		test.setTestGroup(group);
		Response<TestType> response = zc.startTest(test);
		test = response.getObject();
		if (test == null) 
		{
			throw new RuntimeException("Unable to register test '" + String.format(testDetails, name, status, testArgs,
					testRunId, testCaseId, startTime, demoURL, logURL, retry) + "' startup for zafira service: " + ZAFIRA_URL);
		} 
		else 
		{
			LOGGER.debug("Registered test startup details:" + String.format(testDetails, name, status, testArgs, testRunId, testCaseId, startTime, demoURL, logURL, retry));
		}
		return test;
	}

	/**
	 * Registers test re-run in Zafira.
	 * 
	 * @param test
	 * @return registered test
	 */
	private TestType registerTestRestart(TestType test) 
	{
		String testName = test.getName();
		Response<TestType> response = zc.startTest(test);
		test = response.getObject();
		if (test == null) 
		{
			throw new RuntimeException("Unable to register test '" + testName + "' restart for zafira service: " + ZAFIRA_URL);
		} 
		else 
		{
			LOGGER.debug("Registered test restart details:'" + testName + "'; startTime: " + test.getStartTime());
		}
		return test;
	}
	
	/**
	 * Registers test execution results.
	 * 
	 * @param result
	 * @param status
	 * @param message
	 * @param finishTime
	 * @return updated test
	 * @throws JAXBException
	 */
	private TestType registerTestResults(ITestResult result, Status status, String message, Long finishTime) throws JAXBException 
	{
		long threadId = Thread.currentThread().getId();
		TestType test = testByThread.get(threadId);
		
		String testName = configurator.getTestName(result);
		LOGGER.debug("testName registered with current thread is: " + testName);
		
		if (test == null && !Status.SKIPPED.equals(status)) 
		{
			throw new RuntimeException("Unable to find TestType result to mark test as finished! name: '" + testName + "'; threadId: " + threadId);
		}

		// When test is skipped as dependent, reinit test from scratch.
		if (Status.SKIPPED.equals(status) && test == null) 
		{
			//that's dfinitely the case with skipped dependent method
			testName = configurator.getTestName(result);
			
			//if not start new test as it is skipped dependent test method
			TestCaseType testCase = registerTestCase(result);
			String testArgs = result.getParameters().toString();
			
			String group = result.getMethod().getTestClass().getName();
			group = group.substring(0, group.lastIndexOf("."));
			
			test = registerTestStart(testName, group, status, testArgs, run.getId(), testCase.getId(), null, null, configurator.getRunCount(result));
		}
		
		test.setDemoURL(configurator.getDemoURL(result));
		test.setLogURL(configurator.getLogURL(result));
		test.setTestMetrics(configurator.getTestMetrics(result));
		
		String testDetails = "testId: %d; testCaseId: %d; testRunId: %d; name: %s; thread: %s; status: %s, finishTime: %s \n message: %s";
		String logMessage = String.format(testDetails, test.getId(), test.getTestCaseId(), test.getTestRunId(), test.getName(), threadId, status, finishTime, message);
		
		LOGGER.debug("Test details to finish registration:" + logMessage);

		test.setStatus(status);
		test.setMessage(message);
		test.setFinishTime(finishTime);

		Response<TestType> response = zc.finishTest(test);
		
		test = response.getObject();
		if (test == null) 
		{
			throw new RuntimeException("Unable to register test '" + logMessage + "' for zafira service: " + ZAFIRA_URL);
		}
		else 
		{
			LOGGER.debug("Registered test details:" + logMessage);
		}
		
		testByThread.remove(threadId);
		
		return test;
	}
	
	
	/**
	 * Registers test work items.
	 * 
	 * @param testId
	 * @param workItems
	 * @return test for which we registers work items.
	 */
	private TestType registerWorkItems(Long testId, List<String> workItems) 
	{
		TestType test = null;
		if(workItems != null && workItems.size() > 0)
		{
			Response<TestType> response = zc.createTestWorkItems(testId, workItems);
			test = response.getObject();
		}
		return test;
	}

	/**
	 * Marshals configuration bean to XML.
	 * 
	 * @param config bean
	 * @return XML representation of configuration bean
	 * @throws JAXBException
	 */
	private String convertToXML(Configuration config) throws JAXBException
	{
		final StringWriter w = new StringWriter();
		marshaller.marshal(config, w);
		return w.toString();
	}
	
	/**
	 * Generated full test failures stack trace taking into account test skip reasons.
	 * 
	 * @param ITestResult result
	 * @return full error stack trace
	 */
	private String getFullStackTrace(ITestResult result) 
	{
		StringBuilder sb = new StringBuilder();
		
		if(result.getThrowable() == null)
		{
			if(result.getStatus() == ITestResult.SKIP)
			{
				// Identify is it due to the dependent failure or exception in before suite/class/method
				String[] methods = result.getMethod().getMethodsDependedUpon();

				// Find if any parent method failed/skipped
				boolean dependentMethod = false;
				String dependentMethodName = "";
				for (ITestResult failedTest : result.getTestContext().getFailedTests().getAllResults())
				{
					for (int i = 0; i < methods.length; i++)
					{
						if (methods[i].contains(failedTest.getName()))
						{
							dependentMethodName = failedTest.getName();
							dependentMethod = true;
							break;
						}
					}
				}

				for (ITestResult skippedTest : result.getTestContext().getSkippedTests().getAllResults())
				{
					for (int i = 0; i < methods.length; i++)
					{
						if (methods[i].contains(skippedTest.getName()))
						{
							dependentMethodName = skippedTest.getName();
							dependentMethod = true;
							break;
						}
					}
				}

				if (dependentMethod)
				{
					sb.append("Test skipped due to the dependency from: " + dependentMethodName);
				} 
				else 
				{
					//TODO: find a way to transfer configuration failure message in case of error in before suite/class/method
				}
			}
		}
		else
		{
			sb.append(result.getThrowable().getMessage() + "\n");
	    	
            StackTraceElement[] elems = result.getThrowable().getStackTrace();
	        for (StackTraceElement elem : elems) 
	        {
	        	sb.append("\n" + elem.toString());
            }
		}
		
	    return !StringUtils.isEmpty(sb.toString()) ? sb.toString() : null;
	}
}