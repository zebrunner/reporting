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
import com.qaprosoft.zafira.client.impl.ZafiraClientImpl;
import com.qaprosoft.zafira.config.CIConfig;
import com.qaprosoft.zafira.config.IConfigurator;
import com.qaprosoft.zafira.listener.TestHookable;
import com.qaprosoft.zafira.listener.ExcludeTestsForRerun;
import com.qaprosoft.zafira.listener.ZafiraListener;
import com.qaprosoft.zafira.listener.adapter.MethodAdapter;
import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultStatus;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.models.dto.config.ConfigurationType;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.util.http.HttpClient;
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
import static com.qaprosoft.zafira.client.ClientDefaults.ZAFIRA_PROPERTIES_FILE;
import static com.qaprosoft.zafira.config.CIConfig.BuildCase.UPSTREAMTRIGGER;

public class ZafiraListenerImpl implements ZafiraListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZafiraListenerImpl.class);

    private final static String SKIP_CFG_EXC_MSG = "Skipping configuration method since test class doesn't contain test methods to rerun";

    private static final String ZAFIRA_PROJECT_PARAM = "zafira_project";
    private static final String ZAFIRA_RUN_ID_PARAM = "zafira_run_id";

    private boolean ZAFIRA_ENABLED = false;
    private String ZAFIRA_URL = null;
    private String ZAFIRA_ACCESS_TOKEN = null;
    private String ZAFIRA_PROJECT = null;
    private boolean ZAFIRA_RERUN_FAILURES = false;
    private String ZAFIRA_CONFIGURATOR = null;

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
    public void onSuiteStart(SuiteAdapter adapter) {
        boolean initialized = initializeZafira(adapter);
        // Exit on initialization failure
        if (!initialized)
            return;

        try {
            // TODO: investigate possibility to remove methods from suite
            // context based on need rerun flag. And delete appropriate code
            // from before method and before class

            marshaller = JAXBContext.newInstance(ConfigurationType.class).createMarshaller();

            configurator = (IConfigurator) Class.forName(ZAFIRA_CONFIGURATOR).newInstance();

            // Override project if specified in XML
            String project = adapter.getSuiteParameter(ZAFIRA_PROJECT_PARAM);
            zc.initProject(!StringUtils.isEmpty(project) ? project : ZAFIRA_PROJECT);

            // Register user who initiated test run
            this.user = zc.getUserProfile().getObject();

            // Register test suite along with suite owner
            UserType suiteOwner = zc.getUserOrAnonymousIfNotFound(configurator.getOwner(adapter));
            this.suite = zc.registerTestSuite(adapter.getSuiteName(), FilenameUtils.getName(adapter.getSuiteFileName()),
                    suiteOwner.getId());

            // Register job that triggers test run
            this.job = zc.registerJob(ci.getCiUrl(), suiteOwner.getId());

            // Register upstream job if required
            UserType anonymous;
            if (UPSTREAMTRIGGER.equals(ci.getCiBuildCause())) {
                anonymous = zc.getUserOrAnonymousIfNotFound(USER);
                parentJob = zc.registerJob(ci.getCiParentUrl(), anonymous.getId());
            }

            // Searching for existing test run with same CI run id in case of rerun
            if (!StringUtils.isEmpty(ci.getCiRunId())) {
                HttpClient.Response<TestRunType> response = zc.getTestRunByCiRunId(ci.getCiRunId());
                this.run = response.getObject();
            }

            if (this.run != null) {
                // Already discovered run with the same CI_RUN_ID, it is re-run functionality!
                // Reset build number for re-run to map to the latest rerun build
                this.run.setBuildNumber(ci.getCiBuild());
                // Reset testRun config for rerun in case of queued tests
                this.run.setConfigXML(convertToXML(configurator.getConfiguration()));
                // Reset test suite https://github.com/qaprosoft/zafira/issues/1584
                this.run.setTestSuiteId(suite.getId());
                // Re-register test run to reset status onto in progress
                HttpClient.Response<TestRunType> response = zc.startTestRun(this.run);
                this.run = response.getObject();

                List<TestType> testRunResults = Arrays.asList(zc.getTestRunResults(run.getId()).getObject());
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

                switch (ci.getCiBuildCause()) {
                    case UPSTREAMTRIGGER:
                        this.run = zc.registerTestRunUPSTREAM_JOB(suite.getId(), convertToXML(configurator.getConfiguration()), job.getId(),
                                parentJob.getId(), ci, TestRun.Initiator.UPSTREAM_JOB, JIRA_SUITE_ID);
                        break;
                    case TIMERTRIGGER:
                    case SCMTRIGGER:
                        this.run = zc.registerTestRunBySCHEDULER(suite.getId(), convertToXML(configurator.getConfiguration()), job.getId(), ci,
                                TestRun.Initiator.SCHEDULER, JIRA_SUITE_ID);
                        break;
                    case MANUALTRIGGER:
                        this.run = zc.registerTestRunByHUMAN(suite.getId(), user.getId(), convertToXML(configurator.getConfiguration()), job.getId(), ci,
                                TestRun.Initiator.HUMAN, JIRA_SUITE_ID);
                        break;
                    default:
                        throw new RuntimeException("Unable to register test run for zafira service: " + ZAFIRA_URL + " due to the misses build cause: '"
                                + ci.getCiBuildCause() + "'");
                }
            }

            if (this.run == null) {
                throw new RuntimeException("Unable to register test run for zafira service: " + ZAFIRA_URL);
            } else {
                System.setProperty(ZAFIRA_RUN_ID_PARAM, String.valueOf(this.run.getId()));
            }

            Runtime.getRuntime().addShutdownHook(new TestRunShutdownHook(this.zc, this.run));
        } catch (Throwable e) {
            ZAFIRA_ENABLED = false;
            LOGGER.error("Undefined error during test run registration!", e);
        }
    }

    @Override
    public void onSuiteFinish() {
        if (!ZAFIRA_ENABLED)
            return;

        try {
            // Reset configuration to store for example updated at run-time app_version etc
            this.run.setConfigXML(convertToXML(configurator.getConfiguration()));
            zc.registerTestRunResults(this.run);
        } catch (Throwable e) {
            LOGGER.error("Unable to finish test run correctly", e);
        }
    }

    @Override
    public void onTestStart(TestResultAdapter adapter) {
        if (!ZAFIRA_ENABLED)
            return;

        try {
            TestType startedTest = null;

            String testName = configurator.getTestName(adapter);

            // If method owner is not specified then try to use suite owner. If both are not declared then ANONYMOUS will be used.
            String po = configurator.getPrimaryOwner(adapter);
            String primaryOwnerName = !StringUtils.isEmpty(po) ? po : configurator.getOwner(adapter.getSuiteAdapter());
            UserType primaryOwner = zc.getUserOrAnonymousIfNotFound(primaryOwnerName);
            LOGGER.debug("primaryOwner: " + primaryOwnerName);

            String secondaryOwnerName = configurator.getSecondaryOwner(adapter);
            UserType secondaryOwner = null;
            if (!StringUtils.isEmpty(secondaryOwnerName)) {
                secondaryOwner = zc.getUserOrAnonymousIfNotFound(secondaryOwnerName);
                LOGGER.debug("secondaryOwner: " + secondaryOwnerName);
            }

            String testClass = adapter.getMethodAdapter().getTestClassName();
            String testMethod = configurator.getTestMethodName(adapter);

            TestCaseType testCase = zc.registerTestCase(this.suite.getId(), primaryOwner.getId(),
                    (secondaryOwner != null ? secondaryOwner.getId() : null), testClass, testMethod);

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
                startedTest = zc.registerTestRestart(startedTest);
            }

            if (startedTest == null) {
                // new test run registration
                String testArgs = adapter.getParameters().toString();

                String group = adapter.getMethodAdapter().getTestClassName();;
                group = group.substring(0, group.lastIndexOf("."));

                String[] dependsOnMethods = adapter.getMethodAdapter().getMethodDependsOnMethods();

                startedTest = zc.registerTestStart(testName, group, Status.IN_PROGRESS, testArgs, run.getId(), testCase.getId(),
                        configurator.getRunCount(adapter), convertToXML(configurator.getConfiguration()), dependsOnMethods, getThreadCiTestId(),
                        configurator.getTestTags(adapter));
            }

            zc.registerWorkItems(startedTest.getId(), configurator.getTestWorkItems(adapter));
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
        if (!ZAFIRA_ENABLED)
            return;

        try {
            HttpClient.Response<TestType> rs = zc.finishTest(populateTestResult(adapter, Status.PASSED, getFullStackTrace(adapter)));
            if (rs.getStatus() != 200 && rs.getObject() == null) {
                throw new RuntimeException("Unable to register test " + adapter.getMethodAdapter().getMethodName() + " for zafira service: " + ZAFIRA_URL);
            }
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

                // If method owner is not specified then try to use suite owner. If both are not declared then ANONYMOUS will be used.
                String po = configurator.getPrimaryOwner(adapter);
                String primaryOwnerName = !StringUtils.isEmpty(po) ? po : configurator.getOwner(adapter.getSuiteAdapter());
                UserType primaryOwner = zc.getUserOrAnonymousIfNotFound(primaryOwnerName);
                LOGGER.debug("primaryOwner: " + primaryOwnerName);

                String secondaryOwnerName = configurator.getSecondaryOwner(adapter);
                UserType secondaryOwner = null;
                if (!StringUtils.isEmpty(secondaryOwnerName)) {
                    secondaryOwner = zc.getUserOrAnonymousIfNotFound(secondaryOwnerName);
                    LOGGER.debug("secondaryOwner: " + secondaryOwnerName);
                }

                String testClass = adapter.getMethodAdapter().getTestClassName();
                String testMethod = configurator.getTestMethodName(adapter);

                // if not start new test as it is skipped dependent test method
                TestCaseType testCase = zc.registerTestCase(this.suite.getId(), primaryOwner.getId(),
                        (secondaryOwner != null ? secondaryOwner.getId() : null), testClass, testMethod);
                String testArgs = adapter.getParameters().toString();

                String group = adapter.getMethodAdapter().getTestClassName();
                group = group.substring(0, group.lastIndexOf("."));

                String[] dependsOnMethods = adapter.getMethodAdapter().getMethodDependsOnMethods();

                test = zc.registerTestStart(testName, group, Status.SKIPPED, testArgs, run.getId(), testCase.getId(),
                        configurator.getRunCount(adapter), convertToXML(configurator.getConfiguration()), dependsOnMethods, getThreadCiTestId(),
                        configurator.getTestTags(adapter));
                threadTest.set(test);
            }

            String fullStackTrace = getFullStackTrace(adapter);
            HttpClient.Response<TestType> rs = zc.finishTest(populateTestResult(adapter, Status.SKIPPED, fullStackTrace));
            if (rs.getStatus() != 200 && rs.getObject() == null) {
                throw new RuntimeException("Unable to register test " + adapter.getMethodAdapter().getMethodName() + " for zafira service: " + ZAFIRA_URL);
            }
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
        boolean success = false;
        try {
            CombinedConfiguration config = new CombinedConfiguration(new MergeCombiner());
            config.setThrowExceptionOnMissing(true);
            config.addConfiguration(new SystemConfiguration());
            config.addConfiguration(new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(new Parameters().properties().setFileName(ZAFIRA_PROPERTIES_FILE)).getConfiguration());

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

            ZAFIRA_ENABLED = (Boolean) ZafiraConfiguration.ENABLED.get(config, adapter);
            ZAFIRA_URL = (String) ZafiraConfiguration.SERVICE_URL.get(config, adapter);
            ZAFIRA_ACCESS_TOKEN = (String) ZafiraConfiguration.ACCESS_TOKEN.get(config, adapter);
            ZAFIRA_PROJECT = (String) ZafiraConfiguration.PROJECT.get(config, adapter);
            ZAFIRA_RERUN_FAILURES = (Boolean) ZafiraConfiguration.RERUN_FAILURES.get(config, adapter);
            ZAFIRA_CONFIGURATOR = (String) ZafiraConfiguration.CONFIGURATOR.get(config, adapter);

            if (ZAFIRA_ENABLED) {
                zc = new ZafiraClientImpl(ZAFIRA_URL);

                ZAFIRA_ENABLED = zc.isAvailable();

                if (ZAFIRA_ENABLED) {
                    HttpClient.Response<AuthTokenType> auth = zc.refreshToken(ZAFIRA_ACCESS_TOKEN);
                    if (auth.getStatus() == 200) {
                        zc.setAuthToken(auth.getObject().getType() + " " + auth.getObject().getAccessToken());
                    } else {
                        ZAFIRA_ENABLED = false;
                    }
                }
                LOGGER.info("Zafira is " + (ZAFIRA_ENABLED ? "available" : "unavailable"));
            }

            success = ZAFIRA_ENABLED;
        } catch (ConfigurationException e) {
            LOGGER.error("Unable to locate " + ZAFIRA_PROPERTIES_FILE + ": ", e);
        } catch (NoSuchElementException e) {
            LOGGER.error("Unable to find config property: ", e);
        }

        return success;
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
                boolean dependentMethod = false;
                String dependentMethodName = "";
                for (TestResultAdapter failedTestResultAdapter : adapter.getFailedTestResults()) {
                    for (int i = 0; i < methods.length; i++) {
                        String failedTestResultAdapterName = failedTestResultAdapter.getName();
                        if (methods[i].contains(failedTestResultAdapterName)) {
                            dependentMethodName = failedTestResultAdapterName;
                            dependentMethod = true;
                            break;
                        }
                    }
                }

                for (TestResultAdapter skippedTestResultAdapter : adapter.getSkippedTestResults()) {
                    for (int i = 0; i < methods.length; i++) {
                        String skippedTestResultAdapterName = skippedTestResultAdapter.getName();
                        if (methods[i].contains(skippedTestResultAdapterName)) {
                            dependentMethodName = skippedTestResultAdapterName;
                            dependentMethod = true;
                            break;
                        }
                    }
                }

                if (dependentMethod) {
                    sb.append("Test skipped due to the dependency from: ").append(dependentMethodName);
                } else {
                    // TODO: find a way to transfer configuration failure message in case of error in before suite/class/method
                }
            }
        } else {
            sb.append(adapter.getThrowable().getMessage()).append("\n");

            StackTraceElement[] elems = adapter.getThrowable().getStackTrace();
            for (StackTraceElement elem : elems) {
                sb.append("\n").append(elem.toString());
            }
        }

        return !StringUtils.isEmpty(sb.toString()) ? sb.toString() : null;
    }

    /**
     * TestRunShutdownHook - aborts test run when CI job is aborted.
     */
    public static class TestRunShutdownHook extends Thread {
        private ZafiraClient zc;
        private TestRunType testRun;

        public TestRunShutdownHook(ZafiraClient zc, TestRunType testRun) {
            this.zc = zc;
            this.testRun = testRun;
        }

        @Override
        public void run() {
            if (testRun != null) {
                boolean aborted = zc.abortTestRun(testRun.getId());
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
        if (!ZAFIRA_ENABLED) {
            return;
        }

        try {
            String fullStackTrace = getFullStackTrace(adapter);
            HttpClient.Response<TestType> rs = zc.finishTest(populateTestResult(adapter, Status.FAILED, fullStackTrace));
            if (rs.getStatus() != 200 && rs.getObject() == null) {
                throw new RuntimeException("Unable to register test " + adapter.getMethodAdapter().getMethodName() + " for zafira service: " + ZAFIRA_URL);
            }
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
        public Object get(Configuration config, SuiteAdapter adapter) {
            return this.canOverride && adapter.getSuiteParameter(this.configName) != null ? adapter.getSuiteParameter(this.configName)
                    : config.get(getDefaultClassValue(), this.configName, this.defaultValue);
        }

        @SuppressWarnings("rawtypes")
        private Class getDefaultClassValue() {
            Class aClass = null;
            if (this.defaultValue instanceof String) {
                aClass = String.class;
            } else if (this.defaultValue instanceof Boolean) {
                aClass = Boolean.class;
            } else if (this.defaultValue instanceof Integer) {
                aClass = Integer.class;
            }
            return aClass;
        }
    }

}
