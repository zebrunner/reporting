/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.listener.impl;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.client.ZafiraSingleton;
import com.qaprosoft.zafira.config.CiConfig;
import com.qaprosoft.zafira.config.IConfigurator;
import com.qaprosoft.zafira.listener.TestHookable;
import com.qaprosoft.zafira.listener.ExcludeTestsForRerun;
import com.qaprosoft.zafira.listener.ZafiraListener;
import com.qaprosoft.zafira.listener.adapter.MethodAdapter;
import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultStatus;
import com.qaprosoft.zafira.listener.domain.CiConfiguration;
import com.qaprosoft.zafira.listener.domain.ZafiraConfiguration;
import com.qaprosoft.zafira.listener.service.JobTypeService;
import com.qaprosoft.zafira.listener.service.ProjectTypeService;
import com.qaprosoft.zafira.listener.service.TestCaseTypeService;
import com.qaprosoft.zafira.listener.service.TestTypeService;
import com.qaprosoft.zafira.listener.service.TestRunTypeService;
import com.qaprosoft.zafira.listener.service.TestSuiteTypeService;
import com.qaprosoft.zafira.listener.service.UserTypeService;
import com.qaprosoft.zafira.listener.service.impl.JobTypeServiceImpl;
import com.qaprosoft.zafira.listener.service.impl.ProjectTypeServiceImpl;
import com.qaprosoft.zafira.listener.service.impl.TestCaseTypeServiceImpl;
import com.qaprosoft.zafira.listener.service.impl.TestTypeServiceImpl;
import com.qaprosoft.zafira.listener.service.impl.TestRunTypeServiceImpl;
import com.qaprosoft.zafira.listener.service.impl.TestSuiteTypeServiceImpl;
import com.qaprosoft.zafira.listener.service.impl.UserTypeServiceImpl;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.config.ConfigurationType;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.util.ConfigurationUtil;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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

import static com.qaprosoft.zafira.client.ClientDefaults.USER;
import static com.qaprosoft.zafira.config.CiConfig.BuildCase.UPSTREAMTRIGGER;

public class ZafiraListenerImpl implements ZafiraListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZafiraListenerImpl.class);

    private final static String SKIP_CFG_EXC_MSG = "Skipping configuration method since test class doesn't contain test methods to rerun";

    private static final String ZAFIRA_RUN_ID_PARAM = "zafira_run_id";

    private boolean ZAFIRA_ENABLED;
    private String ZAFIRA_URL;
    private String ZAFIRA_PROJECT;
    private boolean ZAFIRA_RERUN_FAILURES;
    private String ZAFIRA_CONFIGURATOR;

    private String JIRA_SUITE_ID;

    private IConfigurator configurator;
    private CiConfig ci;
    private ZafiraClient zc;

    private UserType user;
    private JobType parentJob;
    private JobType job;
    private TestSuiteType suite;
    private TestRunType run;
    private Map<String, TestType> registeredTests = new HashMap<>();
    private Set<String> classesToRerun = new HashSet<>();

    private static ThreadLocal<String> threadCiTestId = new ThreadLocal<>();
    private static ThreadLocal<TestType> threadTest = new ThreadLocal<>();

    private TestRunTypeService testRunTypeService;
    private TestSuiteTypeService testSuiteTypeService;
    private ProjectTypeService projectTypeService;
    private UserTypeService userTypeService;
    private JobTypeService jobTypeService;
    private TestTypeService testTypeService;
    private TestCaseTypeService testCaseTypeService;

    public ZafiraListenerImpl() {
    }

    @Override
    public void onSuiteStart(SuiteAdapter adapter) {
        boolean initialized = initializeZafira(adapter);
        // Exit on initialization failure
        if (!initialized)
            return;

        try {
            // TODO: investigate possibility to remove methods from suite
            // context based on need rerun flag. And delete appropriate code
            // from before method and before class

            configurator = (IConfigurator) Class.forName(ZAFIRA_CONFIGURATOR).newInstance();

            // Override project if specified in XML
            String project = (String) ZafiraConfiguration.PROJECT.get(adapter);
            project = !StringUtils.isEmpty(project) ? project : ZAFIRA_PROJECT;
            projectTypeService.initProject(project);

            // Register user who initiated test run
            this.user = userTypeService.getUserProfile();

            // Register test suite along with suite owner
            String owner = configurator.getOwner(adapter);
            UserType suiteOwner = userTypeService.getUserOrAnonymousIfNotFound(owner);
            this.suite = testSuiteTypeService.register(adapter.getSuiteName(), adapter.getSuiteFileName(), suiteOwner.getId());

            // Register job that triggers test run
            this.job = jobTypeService.register(ci.getCiUrl(), suiteOwner.getId());

            // Register upstream job if required
            if (UPSTREAMTRIGGER.equals(ci.getCiBuildCause())) {
                UserType anonymous = userTypeService.getUserOrAnonymousIfNotFound(USER);
                parentJob = jobTypeService.register(ci.getCiParentUrl(), anonymous.getId());
            }

            // Searching for existing test run with same CI run id in case of rerun
            if (!StringUtils.isEmpty(ci.getCiRunId())) {
                this.run = testRunTypeService.findTestRunByCiRunId(ci.getCiRunId());
            }

            if (this.run != null) {
                // Already discovered run with the same CI_RUN_ID, it is re-run functionality!
                run = testRunTypeService.rerun(run, ci.getCiBuild(), suite.getId(), configurator.getConfiguration());

                List<TestType> testRunResults = testRunTypeService.findTestRunResults(run.getId());
                for (TestType test : testRunResults) {
                    registeredTests.put(test.getName(), test);
                    if (test.isNeedRerun()) {
                        classesToRerun.add(test.getTestClass());
                    }
                }

                if (ZAFIRA_RERUN_FAILURES) {
                    ExcludeTestsForRerun.excludeTestsForRerun(adapter, testRunResults, configurator);
                }
            } else {
                if (ZAFIRA_RERUN_FAILURES) {
                    LOGGER.error("Unable to find data in Zafira Reporting Service with CI_RUN_ID: '" + ci.getCiRunId() + "'.\n"
                            + "Rerun failures featrure will be disabled!");
                    ZAFIRA_RERUN_FAILURES = false;
                }
                // Register new test run

                this.run = testRunTypeService.register(run, ci.getCiBuildCause(), suite.getId(), job.getId(), user.getId(), parentJob,
                        ci, JIRA_SUITE_ID, configurator.getConfiguration());
            }

            if (this.run == null) {
                throw new RuntimeException("Unable to register test run for zafira service: " + ZAFIRA_URL);
            } else {
                ConfigurationUtil.addSystemConfiguration(ZAFIRA_RUN_ID_PARAM, String.valueOf(this.run.getId()));
            }

            Runtime.getRuntime().addShutdownHook(new TestRunShutdownHook(testRunTypeService, this.run));
        } catch (Throwable e) {
            ZAFIRA_ENABLED = false;
            LOGGER.error("Undefined error during test run registration!", e);
        }
    }

    @Override
    public void onSuiteFinish() {
        if (!ZAFIRA_ENABLED) {
            return;
        }

        try {
            testRunTypeService.registerTestRunResults(run, configurator.getConfiguration());
        } catch (Throwable e) {
            LOGGER.error("Unable to finish test run correctly", e);
        }
    }

    @Override
    public void onTestStart(TestResultAdapter adapter) {
        if (!ZAFIRA_ENABLED) {
            return;
        }

        try {
            TestType startedTest = null;

            String testName = configurator.getTestName(adapter);

            TestCaseType testCase = registerTestCase(adapter);

            // Search already registered test!
            if (registeredTests.containsKey(testName)) {
                startedTest = registeredTests.get(testName);

                // Skip already passed tests if rerun failures enabled
                if (ZAFIRA_RERUN_FAILURES && !startedTest.isNeedRerun()) {
                    throw adapter.getSkipExceptionInstance("ALREADY_PASSED: " + testName);
                }

                startedTest.setFinishTime(null);
                startedTest.setStartTime(new Date().getTime());
                startedTest.setCiTestId(getThreadCiTestId());
                /*
                 * keep static tags registration onTestStart but add dynamic tags onTestFInish obligatory:
                 * https://github.com/qaprosoft/carina/issues/701
                 * https://github.com/qaprosoft/carina/issues/707
                 */
                startedTest.setTags(configurator.getTestTags(adapter));
                startedTest = testTypeService.registerTestRestart(startedTest);
            }

            if (startedTest == null) {
                // new test run registration
                String testArgs = adapter.getParameters().toString();

                String group = adapter.getMethodAdapter().getTestClassName();;
                group = group.substring(0, group.lastIndexOf("."));

                String[] dependsOnMethods = adapter.getMethodAdapter().getMethodDependsOnMethods();

                startedTest = testTypeService.registerTestStart(testName, group, Status.IN_PROGRESS, testArgs, run.getId(), testCase.getId(),
                        configurator.getRunCount(adapter), convertToXML(configurator.getConfiguration()), dependsOnMethods, getThreadCiTestId(), configurator.getTestTags(adapter));
            }

            testTypeService.registerWorkItems(startedTest.getId(), configurator.getTestWorkItems(adapter));
            // TODO: investigate why we need it
            threadTest.set(startedTest);
            registeredTests.put(testName, startedTest);

            // Add Zafira test id for internal usage
            adapter.setAttribute("ztid", startedTest.getId());
        } catch (Throwable e) {
            if (adapter.getSkipExceptionInstance(null).getClass().isAssignableFrom(e.getClass())) {
                throw e;
            }
            LOGGER.error("Undefined error during test case/method start!", e);
        }
    }

    @Override
    public void onTestSuccess(TestResultAdapter adapter) {
        if (!ZAFIRA_ENABLED) {
            return;
        }

        try {
            finishTest(adapter, Status.PASSED);
        } catch (Throwable e) {
            LOGGER.error("Undefined error during test case/method finish!", e);
        }
    }

    @Override
    public void onTestFailure(TestResultAdapter adapter) {
        processResultOnTestFailure(adapter);
    }

    @Override
    public void onTestSkipped(TestResultAdapter adapter) {
        if (!ZAFIRA_ENABLED)
            return;
        // Test is skipped as ALREADY_PASSED
        if (adapter.getThrowable() != null && adapter.getThrowable().getMessage() != null
                && adapter.getThrowable().getMessage().startsWith("ALREADY_PASSED")) {
            return;
        }

        try {
            // Test skipped manually from test body
            TestType test = threadTest.get();// testByThread.get(Thread.currentThread().getId());
            String testName = configurator.getTestName(adapter);
            // Test skipped when upstream failed
            if (test == null) {
                // Try to identify test was already registered then do not report it twice as skipped
                test = registeredTests.get(testName);
            }

            // When test is skipped as dependent, reinit test from scratch.
            if (test == null) {

                // if not start new test as it is skipped dependent test method
                TestCaseType testCase = registerTestCase(adapter);
                String testArgs = adapter.getParameters().toString();

                String group = adapter.getMethodAdapter().getTestClassName();
                group = group.substring(0, group.lastIndexOf("."));

                String[] dependsOnMethods = adapter.getMethodAdapter().getMethodDependsOnMethods();

                test = testTypeService.registerTestStart(testName, group, Status.SKIPPED, testArgs, run.getId(), testCase.getId(),
                        configurator.getRunCount(adapter), convertToXML(configurator.getConfiguration()), dependsOnMethods, getThreadCiTestId(), configurator.getTestTags(adapter));
                threadTest.set(test);
            }

            finishTest(adapter, Status.SKIPPED);
        } catch (Throwable e) {
            LOGGER.error("Undefined error during test case/method finish!", e);
        }
    }

    @Override
    public void onTestHook(TestHookable hookCallBack, TestResultAdapter adapter) {
        if (!ZAFIRA_ENABLED) {
            LOGGER.info("IHookCallBack: zafira not connected so running the test body");
            hookCallBack.runTestMethod(adapter);
        } else {
            String testName = configurator.getTestName(adapter);
            TestType startedTest = registeredTests.get(testName);

            if (ZAFIRA_RERUN_FAILURES && startedTest != null && !startedTest.isNeedRerun()) {
                LOGGER.info("IHookCallBack: test will not be executed since it already passed in previous run");
                // do nothing
            } else {
                LOGGER.debug("IHookCallBack: default execution of test body");
                hookCallBack.runTestMethod(adapter);
            }
        }
    }

    @Override
    public void beforeMethodInvocation(MethodAdapter invokedMethodAdapter, TestResultAdapter adapter) {
        if (ZAFIRA_RERUN_FAILURES) {
            String declaringClassName = invokedMethodAdapter.getDeclaredClassName();
            String testClassName = invokedMethodAdapter.getTestClassName();
            if (!classesToRerun.contains(testClassName) && declaringClassName.equals(testClassName)) {
                if (invokedMethodAdapter.isBeforeClassConfiguration() || invokedMethodAdapter.isAfterClassConfiguration()) {
                    LOGGER.info("SKIPPING CONFIGURATION METHOD: " + declaringClassName + " : " + invokedMethodAdapter.getMethodName()
                            + " for class " + testClassName);
                    throw adapter.getSkipExceptionInstance(SKIP_CFG_EXC_MSG);
                } else if (invokedMethodAdapter.isBeforeTestConfiguration() || invokedMethodAdapter.isAfterTestConfiguration()) {
                    boolean shouldSkip = true;
                    for (String className : adapter.getKnownClassNames()) {
                        if (classesToRerun.contains(className)) {
                            shouldSkip = false;
                            break;
                        }
                    }
                    if (shouldSkip) {
                        LOGGER.info("SKIPPING CONFIGURATION METHOD: " + declaringClassName + " : " + invokedMethodAdapter.getMethodName()
                                + " for class " + testClassName);
                        throw adapter.getSkipExceptionInstance(SKIP_CFG_EXC_MSG);
                    }
                }
            }
        }
    }

    /**
     * Reads zafira.properties and creates zafira client.
     *
     * @return if initialization success
     */
    private boolean initializeZafira(SuiteAdapter adapter) {
        try {
            CombinedConfiguration config = ConfigurationUtil.getConfiguration();
            ci = ConfigurationUtil.retrieveCiConfig(config);

            JIRA_SUITE_ID = (String) CiConfiguration.JIRA_SUITE_ID.get(config, adapter);
            ZAFIRA_ENABLED = (Boolean) ZafiraConfiguration.ENABLED.get(config, adapter);
            ZAFIRA_URL = (String) ZafiraConfiguration.SERVICE_URL.get(config, adapter);
            ZAFIRA_PROJECT = (String) ZafiraConfiguration.PROJECT.get(config, adapter);
            ZAFIRA_RERUN_FAILURES = (Boolean) ZafiraConfiguration.RERUN_FAILURES.get(config, adapter);
            ZAFIRA_CONFIGURATOR = (String) ZafiraConfiguration.CONFIGURATOR.get(config, adapter);

            if (ZAFIRA_ENABLED) {
                zc = ZafiraSingleton.INSTANCE.getClient();
                if(zc != null) {
                    ZAFIRA_ENABLED = zc.isAvailable();

                    this.testRunTypeService = new TestRunTypeServiceImpl(zc);
                    this.testSuiteTypeService = new TestSuiteTypeServiceImpl(zc);
                    this.projectTypeService = new ProjectTypeServiceImpl(zc);
                    this.userTypeService = new UserTypeServiceImpl(zc);
                    this.jobTypeService = new JobTypeServiceImpl(zc);
                    this.testTypeService = new TestTypeServiceImpl(zc);
                    this.testCaseTypeService = new TestCaseTypeServiceImpl(zc);
                }
                LOGGER.info("Zafira is " + (ZAFIRA_ENABLED ? "available" : "unavailable"));
            }

        } catch (NoSuchElementException e) {
            LOGGER.error("Unable to find config property: ", e);
        }

        return ZAFIRA_ENABLED;
    }

    /**
     * Marshals configuration bean to XML.
     *
     * @param config bean
     * @return XML representation of configuration bean
     */
    private String convertToXML(ConfigurationType config) {
        final StringWriter w = new StringWriter();
        try {
            Marshaller marshaller = JAXBContext.newInstance(ConfigurationType.class).createMarshaller();
            marshaller.marshal(config != null ? config : new ConfigurationType(), w);
        } catch (Throwable thr) {
            LOGGER.error("Unable to convert config to XML!", thr);
        }
        return w.toString();
    }

    /**
     * Generated full test failures stack trace taking into account test skip reasons.
     *
     * @param adapter result
     * @return full error stack trace
     */
    private String getFullStackTrace(TestResultAdapter adapter) {
        StringBuilder sb = new StringBuilder();
        if (adapter.getThrowable() == null) {
            if (adapter.getStatus().getCode() == TestResultStatus.SKIP.getCode()) {
                // Identify is it due to the dependent failure or exception in before suite/class/method
                String[] methods = adapter.getMethodAdapter().getMethodDependsOnMethods();
                // Find if any parent method failed/skipped
                String dependentMethodName = null;
                for (TestResultAdapter failedTestResultAdapter : adapter.getFailedTestResults()) {
                    String failedTestResultAdapterName = failedTestResultAdapter.getName();
                    dependentMethodName = getDependentMethodName(methods, failedTestResultAdapterName);
                }

                for (TestResultAdapter skippedTestResultAdapter : adapter.getSkippedTestResults()) {
                    String skippedTestResultAdapterName = skippedTestResultAdapter.getName();
                    String skippedDependentMethodName = getDependentMethodName(methods, skippedTestResultAdapterName);
                    dependentMethodName = skippedDependentMethodName != null ? skippedDependentMethodName : dependentMethodName;
                }

                if (dependentMethodName != null) {
                    sb.append("Test skipped due to the dependency from: ").append(dependentMethodName);
                } else {
                    // TODO: find a way to transfer configuration failure message in case of error in before suite/class/method
                }
            }
        } else {
            sb.append(adapter.getThrowable().getMessage()).append("\n");
            for (StackTraceElement elem : adapter.getThrowable().getStackTrace()) {
                sb.append("\n").append(elem.toString());
            }
        }
        return !StringUtils.isEmpty(sb.toString()) ? sb.toString() : null;
    }

    private String getDependentMethodName(String[] methods, String testName) {
        String result = null;
        boolean contains = Arrays.stream(methods).anyMatch(method -> method.contains(testName));
        if(contains) {
            result = testName;
        }
        return result;
    }

    /**
     * TestRunShutdownHook - aborts test run when CI job is aborted.
     */
    public static class TestRunShutdownHook extends Thread {

        private final TestRunTypeService testRunTypeService;
        private final TestRunType testRun;

        TestRunShutdownHook(TestRunTypeService testRunTypeService, TestRunType testRun) {
            this.testRunTypeService = testRunTypeService;
            this.testRun = testRun;
        }

        @Override
        public void run() {
            if (testRun != null) {
                boolean aborted = testRunTypeService.abort(testRun.getId());
                LOGGER.info("TestRunShutdownHook was executed with result: " + aborted);
            }
        }
    }

    public static String getThreadCiTestId() {
        if (StringUtils.isEmpty(threadCiTestId.get())) {
            threadCiTestId.set(UUID.randomUUID().toString());
        }
        return threadCiTestId.get();
    }

    private void processResultOnTestFailure(TestResultAdapter adapter) {
        if (!ZAFIRA_ENABLED)
            return;

        try {
            finishTest(adapter, Status.FAILED);
        } catch (Throwable e) {
            LOGGER.error("Undefined error during test case/method finish!", e);
        }
    }

    private TestType populateTestResult(TestResultAdapter adapter, Status status, String message) throws JAXBException {
        long threadId = Thread.currentThread().getId();
        TestType test = threadTest.get();// testByThread.get(threadId);
        final Long finishTime = new Date().getTime();

        String testName = configurator.getTestName(adapter);
        LOGGER.debug("testName registered with current thread is: " + testName);

        if (test == null) {
            throw new RuntimeException("Unable to find TestType result to mark test as finished! name: '" + testName + "'; threadId: " + threadId);
        }

        test.setTestMetrics(configurator.getTestMetrics(adapter));
        test.setConfigXML(convertToXML(configurator.getConfiguration()));
        test.setArtifacts(configurator.getArtifacts(adapter));
        configurator.clearArtifacts();

        test.setTags(configurator.getTestTags(adapter));

        String testDetails = "testId: %d; testCaseId: %d; testRunId: %d; name: %s; thread: %s; status: %s, finishTime: %s \n message: %s";
        String logMessage = String.format(testDetails, test.getId(), test.getTestCaseId(), test.getTestRunId(), test.getName(), threadId, status,
                finishTime, message);

        LOGGER.debug("Test details to finish registration:" + logMessage);

        test.setStatus(status);
        test.setMessage(message);
        test.setFinishTime(finishTime);

        threadTest.remove();
        threadCiTestId.remove();

        return test;
    }

    private void finishTest(TestResultAdapter adapter, Status status) throws JAXBException {
        String fullStackTrace = getFullStackTrace(adapter);
        TestType finishedTest = populateTestResult(adapter, status, fullStackTrace);
        testTypeService.finishTest(finishedTest);
    }

    private TestCaseType registerTestCase(TestResultAdapter adapter) {
        // If method owner is not specified then try to use suite owner. If both are not declared then ANONYMOUS will be used.
        String po = configurator.getPrimaryOwner(adapter);
        String primaryOwnerName = !StringUtils.isEmpty(po) ? po : configurator.getOwner(adapter.getSuiteAdapter());
        UserType primaryOwner = userTypeService.getUserOrAnonymousIfNotFound(primaryOwnerName);
        LOGGER.debug("primaryOwner: " + primaryOwnerName);

        String secondaryOwnerName = configurator.getSecondaryOwner(adapter);
        UserType secondaryOwner = null;
        if (!StringUtils.isEmpty(secondaryOwnerName)) {
            secondaryOwner = userTypeService.getUserOrAnonymousIfNotFound(secondaryOwnerName);
            LOGGER.debug("secondaryOwner: " + secondaryOwnerName);
        }

        String testClass = adapter.getMethodAdapter().getTestClassName();
        String testMethod = configurator.getTestMethodName(adapter);
        Long testCaseSecondaryOwner = secondaryOwner != null ? secondaryOwner.getId() : null;
        return testCaseTypeService.registerTestCase(suite.getId(), primaryOwner.getId(), testCaseSecondaryOwner, testClass, testMethod);
    }

}
