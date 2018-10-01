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
package com.qaprosoft.zafira.listener;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
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
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.xml.XmlClass;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.client.ZafiraClient.Response;
import com.qaprosoft.zafira.config.CIConfig;
import com.qaprosoft.zafira.config.CIConfig.BuildCasue;
import com.qaprosoft.zafira.config.IConfigurator;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun.DriverMode;
import com.qaprosoft.zafira.models.db.TestRun.Initiator;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.models.dto.config.ConfigurationType;
import com.qaprosoft.zafira.models.dto.user.UserType;

/**
 * TestNG listener that provides integration with Zafira reporting web-service.
 * Accumulates test results and handles rerun failures logic.
 * 
 * @author akhursevich
 */
public class ZafiraListener implements ISuiteListener, ITestListener, IHookable, IInvokedMethodListener
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ZafiraListener.class);
	
	private static final String ZAFIRA_PROPERTIES = "zafira.properties";
	
	private static final String ZAFIRA_PROJECT_PARAM = "zafira_project";
	private static final String ZAFIRA_RUN_ID_PARAM = "zafira_run_id";
	
	private boolean ZAFIRA_ENABLED = false;
	private String 	ZAFIRA_URL = null;
	private String 	ZAFIRA_ACCESS_TOKEN = null;
	private String 	ZAFIRA_PROJECT = null;
	private boolean ZAFIRA_RERUN_FAILURES = false;
	private String 	ZAFIRA_CONFIGURATOR = null;
	
	private String JIRA_SUITE_ID = null;
	
	private IConfigurator configurator;
	private CIConfig ci;
	private ZafiraClient zc;
	
	private UserType user = null;
	private JobType parentJob = null;
	private JobType job = null;
	private TestSuiteType suite = null;
	private TestRunType run = null;
	private Map<String, TestType> registeredTests = new HashMap<>();
	private Set<String> classesToRerun = new HashSet<>();
	
	private static ThreadLocal<String> threadCiTestId = new ThreadLocal<>();
	private static ThreadLocal<TestType> threadTest = new ThreadLocal<>();
	
	private Marshaller marshaller;
	
	@Override
	public void onStart(ISuite suiteContext)
	{
		boolean initialized = initializeZafira(suiteContext);
		// Exit on initialization failure
		if(!initialized) return;
		
		try
		{
			// TODO: investigate possibility to remove methods from suite
			// context based on need rerun flag. And delete appropriate code
			// from before method and before class
			
			marshaller = JAXBContext.newInstance(ConfigurationType.class).createMarshaller();
			
			configurator = (IConfigurator) Class.forName(ZAFIRA_CONFIGURATOR).newInstance();
			
			// Override project if specified in XML
			String project = suiteContext.getXmlSuite().getParameter(ZAFIRA_PROJECT_PARAM);
			zc.initProject(!StringUtils.isEmpty(project) ? project : ZAFIRA_PROJECT);
			
			// Register user who initiated test run
			this.user = zc.getUserProfile().getObject();
					
			// Register test suite along with suite owner
			UserType suiteOwner =  zc.getUserOrAnonymousIfNotFound(configurator.getOwner(suiteContext));
			this.suite = zc.registerTestSuite(suiteContext.getName(), FilenameUtils.getName(suiteContext.getXmlSuite().getFileName()), suiteOwner.getId());
			
			// Register job that triggers test run
			this.job = zc.registerJob(ci.getCiUrl(), suiteOwner.getId());
			
			// Register upstream job if required
			UserType anonymous = null;
			if (BuildCasue.UPSTREAMTRIGGER.equals(ci.getCiBuildCause())) 
			{
				anonymous = zc.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER);
				parentJob = zc.registerJob(ci.getCiParentUrl(), anonymous.getId());
			}
			
			// Searching for existing test run with same CI run id in case of rerun
			if(!StringUtils.isEmpty(ci.getCiRunId())) 
			{
				Response<TestRunType> response = zc.getTestRunByCiRunId(ci.getCiRunId());
				this.run = response.getObject();
			}
			
			if (this.run != null) 
			{
				// Already discovered run with the same CI_RUN_ID, it is re-run functionality!
				// Reset build number for re-run to map to the latest rerun build
				this.run.setBuildNumber(ci.getCiBuild());
				// Reset testRun config for rerun in case of queued tests
				this.run.setConfigXML(convertToXML(configurator.getConfiguration()));
				// Re-register test run to reset status onto in progress
				Response<TestRunType> response = zc.startTestRun(this.run);
				this.run = response.getObject();
				
				List<TestType> testRunResults = Arrays.asList(zc.getTestRunResults(run.getId()).getObject());
				for (TestType test : testRunResults)
				{
					registeredTests.put(test.getName(), test);
					if (test.isNeedRerun())
					{
						classesToRerun.add(test.getTestClass());
					}
				}

				if (ZAFIRA_RERUN_FAILURES)
				{
					ExcludeTestsForRerun.excludeTestsForRerun(suiteContext, testRunResults, configurator);
				}
			} 
			else 
			{
				if(ZAFIRA_RERUN_FAILURES) 
				{
					LOGGER.error("Unable to find data in Zafira Reporting Service with CI_RUN_ID: '" + ci.getCiRunId() + "'.\n" + "Rerun failures featrure will be disabled!");
					ZAFIRA_RERUN_FAILURES = false;
				}
				// Register new test run
				DriverMode driverMode = configurator.getDriverMode();
				
				switch (ci.getCiBuildCause())
				{
					case UPSTREAMTRIGGER:
						this.run = zc.registerTestRunUPSTREAM_JOB(suite.getId(), convertToXML(configurator.getConfiguration()), job.getId(), parentJob.getId(), ci, Initiator.UPSTREAM_JOB, JIRA_SUITE_ID, driverMode);
						break;
					case TIMERTRIGGER:
					case SCMTRIGGER:
						this.run = zc.registerTestRunBySCHEDULER(suite.getId(), convertToXML(configurator.getConfiguration()), job.getId(), ci, Initiator.SCHEDULER, JIRA_SUITE_ID, driverMode);
						break;
					case MANUALTRIGGER:
						this.run = zc.registerTestRunByHUMAN(suite.getId(), user.getId(), convertToXML(configurator.getConfiguration()), job.getId(), ci, Initiator.HUMAN, JIRA_SUITE_ID, driverMode);
						break;
					default:
						throw new RuntimeException("Unable to register test run for zafira service: " + ZAFIRA_URL + " due to the misses build cause: '" + ci.getCiBuildCause() + "'");
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
			
			Runtime.getRuntime().addShutdownHook(new TestRunShutdownHook(this.zc, this.run));
		}
		catch (Throwable e) 
		{
			ZAFIRA_ENABLED = false;
			LOGGER.error("Undefined error during test run registration!", e);
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
			String primaryOwnerName = !StringUtils.isEmpty(configurator.getPrimaryOwner(result)) ? configurator.getPrimaryOwner(result) : configurator.getOwner(result.getTestContext().getSuite());
			UserType primaryOwner = zc.getUserOrAnonymousIfNotFound(primaryOwnerName);
			LOGGER.debug("primaryOwner: " + primaryOwnerName);
			
			String secondaryOwnerName = configurator.getSecondaryOwner(result);
			UserType secondaryOwner = null;
			if(!StringUtils.isEmpty(secondaryOwnerName))
			{
				secondaryOwner = zc.getUserOrAnonymousIfNotFound(secondaryOwnerName);
				LOGGER.debug("secondaryOwner: " + secondaryOwnerName);
			}
			
			String testClass = result.getMethod().getTestClass().getName();
			String testMethod = configurator.getTestMethodName(result);

			TestCaseType testCase = zc.registerTestCase(this.suite.getId(), primaryOwner.getId(), (secondaryOwner != null ? secondaryOwner.getId() : null), testClass, testMethod);

			// Search already registered test!
			if(registeredTests.containsKey(testName))
			{
				startedTest = registeredTests.get(testName);

				// Skip already passed tests if rerun failures enabled
				if (ZAFIRA_RERUN_FAILURES && !startedTest.isNeedRerun())
				{
					throw new SkipException("ALREADY_PASSED: " + testName);
				}

				startedTest.setFinishTime(null);
				startedTest.setStartTime(new Date().getTime());
				startedTest.setCiTestId(getThreadCiTestId());
				startedTest = zc.registerTestRestart(startedTest);
			}
			
			if (startedTest == null) 
			{
				//new test run registration
				String testArgs = result.getParameters().toString();
				
				String group = result.getMethod().getTestClass().getName();
				group = group.substring(0, group.lastIndexOf("."));
				
				String [] dependsOnMethods = result.getMethod().getMethodsDependedUpon();

				startedTest = zc.registerTestStart(testName, group, Status.IN_PROGRESS, testArgs, run.getId(), testCase.getId(), configurator.getRunCount(result), convertToXML(configurator.getConfiguration()), dependsOnMethods, getThreadCiTestId());
			}
			
			zc.registerWorkItems(startedTest.getId(), configurator.getTestWorkItems(result));
			// TODO: investigate why we need it
			threadTest.set(startedTest);
			registeredTests.put(testName, startedTest);
			
			// Add Zafira test id for internal usage
			result.setAttribute("ztid", startedTest.getId());
		}
		catch (SkipException e)
		{
			throw e;
		}
		catch (Throwable e) 
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
			Response<TestType> rs = zc.finishTest(populateTestResult(result, Status.PASSED, getFullStackTrace(result)));
			if(rs.getStatus() != 200 && rs.getObject() == null)
			{
				throw new RuntimeException("Unable to register test " + rs.getObject().getName() + " for zafira service: " + ZAFIRA_URL);
			}
		} 
		catch (Throwable e) {
			LOGGER.error("Undefined error during test case/method finish!", e);
		}
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result)
	{
		if(!ZAFIRA_ENABLED) return;
		
		try 
		{
			Response<TestType> rs = zc.finishTest(populateTestResult(result, Status.FAILED, getFullStackTrace(result)));
			if(rs.getStatus() != 200 && rs.getObject() == null)
			{
				throw new RuntimeException("Unable to register test " + rs.getObject().getName() + " for zafira service: " + ZAFIRA_URL);
			}
		} 
		catch (Throwable e) {
			LOGGER.error("Undefined error during test case/method finish!", e);
		}
	}

	@Override
	public void onTestFailure(ITestResult result)
	{
		if(!ZAFIRA_ENABLED) return;
		
		try 
		{
			Response<TestType> rs = zc.finishTest(populateTestResult(result, Status.FAILED, getFullStackTrace(result)));
			if(rs.getStatus() != 200 && rs.getObject() == null)
			{
				throw new RuntimeException("Unable to register test " + rs.getObject().getName() + " for zafira service: " + ZAFIRA_URL);
			}
		} 
		catch (Throwable e) {
			LOGGER.error("Undefined error during test case/method finish!", e);
		}
	}

	@Override
	public void onTestSkipped(ITestResult result)
	{
		if(!ZAFIRA_ENABLED) return;
		// Test is skipped as ALREADY_PASSED
		if (result.getThrowable() != null && result.getThrowable().getMessage() != null && result.getThrowable().getMessage().startsWith("ALREADY_PASSED")) 
		{
			return;
		}
		
		try 
		{
			// Test skipped manually from test body
			TestType test = threadTest.get();//testByThread.get(Thread.currentThread().getId());
			// Test skipped when upstream failed
			if(test == null)
			{
				// Try to identify test was already registered then do not report it twice as skipped
				test = registeredTests.get(configurator.getTestName(result));
			}
			
			// When test is skipped as dependent, reinit test from scratch.
			if (test == null) 
			{
				// That's definitely the case with skipped dependent method
				String testName = configurator.getTestName(result);
				
				// If method owner is not specified then try to use suite owner. If both are not declared then ANONYMOUS will be used.
				String primaryOwnerName = !StringUtils.isEmpty(configurator.getPrimaryOwner(result)) ? configurator.getPrimaryOwner(result) : configurator.getOwner(result.getTestContext().getSuite());
				UserType primaryOwner = zc.getUserOrAnonymousIfNotFound(primaryOwnerName);
				LOGGER.debug("primaryOwner: " + primaryOwnerName);
				
				String secondaryOwnerName = configurator.getSecondaryOwner(result);
				UserType secondaryOwner = null;
				if(!StringUtils.isEmpty(secondaryOwnerName))
				{
					secondaryOwner = zc.getUserOrAnonymousIfNotFound(secondaryOwnerName);
					LOGGER.debug("secondaryOwner: " + secondaryOwnerName);
				}
				
				String testClass = result.getMethod().getTestClass().getName();
				String testMethod = configurator.getTestMethodName(result);
				
				//if not start new test as it is skipped dependent test method
				TestCaseType testCase = zc.registerTestCase(this.suite.getId(), primaryOwner.getId(),  (secondaryOwner != null ? secondaryOwner.getId() : null), testClass, testMethod);
				String testArgs = result.getParameters().toString();
				
				String group = result.getMethod().getTestClass().getName();
				group = group.substring(0, group.lastIndexOf("."));
				
				String [] dependsOnMethods = result.getMethod().getMethodsDependedUpon();
				
				test = zc.registerTestStart(testName, group, Status.SKIPPED, testArgs, run.getId(), testCase.getId(), configurator.getRunCount(result), convertToXML(configurator.getConfiguration()), dependsOnMethods, getThreadCiTestId());
				threadTest.set(test);
			}
			
			Response<TestType> rs = zc.finishTest(populateTestResult(result, Status.SKIPPED, getFullStackTrace(result)));
			if(rs.getStatus() != 200 && rs.getObject() == null)
			{
				throw new RuntimeException("Unable to register test " + rs.getObject().getName() + " for zafira service: " + ZAFIRA_URL);
			}
		} 
		catch (Throwable e) {
			LOGGER.error("Undefined error during test case/method finish!", e);
		}
	}
	
	private TestType populateTestResult(ITestResult result, Status status, String message) throws JAXBException
	{
		long threadId = Thread.currentThread().getId();
		TestType test = threadTest.get();//testByThread.get(threadId);
		final Long finishTime = new Date().getTime();
		
		String testName = configurator.getTestName(result);
		LOGGER.debug("testName registered with current thread is: " + testName);
		
		if (test == null) 
		{
			throw new RuntimeException("Unable to find TestType result to mark test as finished! name: '" + testName + "'; threadId: " + threadId);
		}
		
		test.setTestMetrics(configurator.getTestMetrics(result));
		test.setConfigXML(convertToXML(configurator.getConfiguration()));
		test.setArtifacts(configurator.getArtifacts(result));
		
		String testDetails = "testId: %d; testCaseId: %d; testRunId: %d; name: %s; thread: %s; status: %s, finishTime: %s \n message: %s";
		String logMessage = String.format(testDetails, test.getId(), test.getTestCaseId(), test.getTestRunId(), test.getName(), threadId, status, finishTime, message);
		
		LOGGER.debug("Test details to finish registration:" + logMessage);

		test.setStatus(status);
		test.setMessage(message);
		test.setFinishTime(finishTime);
		
		threadTest.remove();
		threadCiTestId.remove();
		
		return test;
	}
	
	@Override
	public void onFinish(ISuite suiteContext)
	{
		if(!ZAFIRA_ENABLED) return;
		
		try 
		{	
			// Reset configuration to store for example updated at run-time app_version etc
			this.run.setConfigXML(convertToXML(configurator.getConfiguration()));
			zc.registerTestRunResults(this.run);
		}
		catch (Throwable e) 
		{
			LOGGER.error("Unable to finish test run correctly", e);
		}
	}
	
	@Override
	public void onStart(ITestContext context) {
	}
	
	@Override
	public void onFinish(ITestContext context)
	{
		// Do nothing
	}
	
	@Override
	public void run(IHookCallBack hookCallBack, ITestResult testResult)
	{
		if (!ZAFIRA_ENABLED)
		{
			hookCallBack.runTestMethod(testResult);
		} else
		{
			String testName = configurator.getTestName(testResult);
			TestType startedTest = registeredTests.get(testName);

			if (ZAFIRA_RERUN_FAILURES && startedTest != null && !startedTest.isNeedRerun())
			{
				// do nothing
			} else
			{
				hookCallBack.runTestMethod(testResult);
			}
		}
	}

	private final static String SKIP_CFG_EXC_MSG = "Skipping configuration method since test class doesn't contain test methods to rerun";

	@Override
	public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult)
	{
		if (ZAFIRA_RERUN_FAILURES)
		{
			ITestNGMethod m = invokedMethod.getTestMethod();
			String declaringClassName = m.getConstructorOrMethod().getMethod().getDeclaringClass().getName();
			String testClassName = m.getTestClass().getName();
			if (!classesToRerun.contains(testClassName) && declaringClassName.equals(testClassName))
			{
				if (m.isBeforeClassConfiguration() || m.isAfterClassConfiguration())
				{
					LOGGER.info("SKIPPING CONFIGURATION METHOD: " + declaringClassName + " : " + m.getMethodName()
							+ " for class " + testClassName);
					throw new SkipException(SKIP_CFG_EXC_MSG);
				} else if (m.isBeforeTestConfiguration() || m.isAfterTestConfiguration())
				{
					boolean shouldSkip = true;
					for (XmlClass cl : testResult.getTestContext().getCurrentXmlTest().getClasses())
					{
						if (classesToRerun.contains(cl.getName()))
						{
							shouldSkip = false;
							break;
						}
					}
					if (shouldSkip)
					{
						LOGGER.info("SKIPPING CONFIGURATION METHOD: " + declaringClassName + " : " + m.getMethodName()
								+ " for class " + testClassName);
						throw new SkipException(SKIP_CFG_EXC_MSG);
					}
				}
			}
		}
	}

	@Override
	public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult)
	{
		// do nothing
	}

	//==========================
	
	/**
	 * Reads zafira.properties and creates zafira client.
	 * 
	 * @return if initialization success
	 */
	private boolean initializeZafira(ISuite suiteContext)
	{
		boolean success = false;
		try
		{
			CombinedConfiguration config = new CombinedConfiguration(new MergeCombiner());
			config.setThrowExceptionOnMissing(true);
			config.addConfiguration(new SystemConfiguration());
			config.addConfiguration(new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				    					  .configure(new Parameters().properties().setFileName(ZAFIRA_PROPERTIES)).getConfiguration());

			ci = new CIConfig();
			ci.setCiRunId(config.getString("ci_run_id", UUID.randomUUID().toString()));
			ci.setCiUrl(config.getString("ci_url", "http://localhost:8080/job/unavailable"));
			ci.setCiBuild(config.getString("ci_build", null));
			ci.setCiBuildCause(config.getString("ci_build_cause", "MANUALTRIGGER"));
			ci.setCiParentUrl(config.getString("ci_parent_url", null));
			ci.setCiParentBuild(config.getString("ci_parent_build", null));
			
			ci.setGitBranch(config.getString("git_branch", null));
			ci.setGitCommit(config.getString("git_commit", null));
			ci.setGitUrl(config.getString("git_url", null));
			
			JIRA_SUITE_ID = config.getString("jira_suite_id", null);
			
			ZAFIRA_ENABLED = (Boolean) ZafiraConfiguration.ENABLED.get(config, suiteContext);
			ZAFIRA_URL = (String) ZafiraConfiguration.SERVICE_URL.get(config, suiteContext);
			ZAFIRA_ACCESS_TOKEN = (String) ZafiraConfiguration.ACCESS_TOKEN.get(config, suiteContext);
			ZAFIRA_PROJECT = (String) ZafiraConfiguration.PROJECT.get(config, suiteContext);
			ZAFIRA_RERUN_FAILURES = (Boolean) ZafiraConfiguration.RERUN_FAILURES.get(config, suiteContext);
			ZAFIRA_CONFIGURATOR = (String) ZafiraConfiguration.CONFIGURATOR.get(config, suiteContext);
			
			if(ZAFIRA_ENABLED)
			{
				zc = new ZafiraClient(ZAFIRA_URL);
				
				ZAFIRA_ENABLED =  zc.isAvailable();
				
				if(ZAFIRA_ENABLED)
				{
					Response<AuthTokenType> auth = zc.refreshToken(ZAFIRA_ACCESS_TOKEN);
					if(auth.getStatus() == 200)
					{
						zc.setAuthToken(auth.getObject().getType() + " " + auth.getObject().getAccessToken());
					}
					else
					{
						ZAFIRA_ENABLED = false;
					}
				}
				LOGGER.info("Zafira is " + (ZAFIRA_ENABLED ? "available" : "unavailable"));
				System.out.println("Zafira is " + (ZAFIRA_ENABLED ? "available" : "unavailable"));
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
	 * Marshals configuration bean to XML.
	 * 
	 * @param config bean
	 * @return XML representation of configuration bean
	 * @throws JAXBException
	 */
	private String convertToXML(ConfigurationType config)
	{
		final StringWriter w = new StringWriter();
		try
		{
			marshaller.marshal(config != null ? config : new ConfigurationType(), w);
		}
		catch(Throwable thr)
		{
			LOGGER.error("Unable to convert config to XML!", thr);
		}
		return w.toString();
	}
	
	/**
	 * Generated full test failures stack trace taking into account test skip reasons.
	 * 
	 * @param result result
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

	/**
	 * TestRunShutdownHook - aborts test run when CI job is aborted.
	 */
	public static class TestRunShutdownHook extends Thread 
	{
		private ZafiraClient zc;
		private TestRunType testRun;

		public TestRunShutdownHook (ZafiraClient zc, TestRunType testRun)
		{
			this.zc = zc;
			this.testRun = testRun;
		}

		@Override
		public void run() 
		{
			if(testRun != null)
			{
				boolean aborted = zc.abortTestRun(testRun.getId());
				LOGGER.info("TestRunShutdownHook was executed with result: " + aborted);
			}
		}
	}
	
	public static String getThreadCiTestId() 
	{
		if(StringUtils.isEmpty(threadCiTestId.get())) {
			LOGGER.info("Generating new ci_test_id...");
			System.out.println("Generating new ci_test_id...");
			threadCiTestId.set(UUID.randomUUID().toString());
			LOGGER.info("Generated ci_test_id: " + threadCiTestId.get());
			System.out.println("Generated ci_test_id: " + threadCiTestId.get());
		}
		System.out.println("return existing ci_test_id: " + threadCiTestId.get());
		LOGGER.info("return existing ci_test_id: " + threadCiTestId.get());
		return threadCiTestId.get();
	}

	public enum ZafiraConfiguration {

		ENABLED("zafira_enabled", false),
		SERVICE_URL("zafira_service_url", StringUtils.EMPTY),
		ACCESS_TOKEN("zafira_access_token", StringUtils.EMPTY),
		PROJECT("zafira_project", StringUtils.EMPTY, true),
		RERUN_FAILURES("zafira_rerun_failures", false),
		CONFIGURATOR("zafira_configurator", "com.qaprosoft.zafira.listener.DefaultConfigurator", true);

		private String configName;
		private Object defaultValue;
		private boolean canOverride;

		ZafiraConfiguration(String configName, Object defaultValue) {
			this.configName = configName;
			this.defaultValue = defaultValue;
		}

		ZafiraConfiguration(String configName, Object defaultValue, boolean canOverride) {
			this.configName = configName;
			this.defaultValue = defaultValue;
			this.canOverride = canOverride;
		}

		public String getConfigName() {
			return configName;
		}

		public Object getDefaultValue() {
			return defaultValue;
		}

		public boolean isCanOverride() {
			return canOverride;
		}

		@SuppressWarnings("unchecked")
		public Object get(Configuration config, ISuite suiteConfig) {
			return this.canOverride &&  suiteConfig.getParameter(this.configName) != null ? suiteConfig.getParameter(this.configName) :
					config.get(getDefaultClassValue(), this.configName, this.defaultValue);
		}

		@SuppressWarnings("rawtypes")
		private Class getDefaultClassValue() {
			Class aClass = null;
			if(this.defaultValue instanceof String) {
				aClass = String.class;
			} else if(this.defaultValue instanceof Boolean) {
				aClass = Boolean.class;
			} else if(this.defaultValue instanceof Integer) {
				aClass = Integer.class;
			}
			return aClass;
		}
	}
}