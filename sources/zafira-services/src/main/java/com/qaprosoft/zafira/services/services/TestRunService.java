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
package com.qaprosoft.zafira.services.services;

import static com.qaprosoft.zafira.models.db.Setting.SettingType.JIRA_URL;
import static com.qaprosoft.zafira.models.db.Status.*;
import static com.qaprosoft.zafira.services.util.DateFormatter.actualizeSearchCriteriaDate;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.JobSearchCriteria;
import com.qaprosoft.zafira.services.services.cache.StatisticsService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.models.dto.QueueTestRunParamsType;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.services.exceptions.InvalidTestRunException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;
import com.qaprosoft.zafira.services.services.emails.TestRunResultsEmail;
import com.qaprosoft.zafira.services.util.FreemarkerUtil;

@Service
public class TestRunService
{
	private static Logger LOGGER = LoggerFactory.getLogger(TestRunService.class);
	
	@Value("${zafira.webservice.url}")
	private String wsURL;
	
	@Autowired
	private TestRunMapper testRunMapper;
	
	@Autowired
	private TestService testService;

	@Autowired
	private FreemarkerUtil freemarkerUtil;
	
	@Autowired
	private TestConfigService testConfigService;

	@Autowired
	private WorkItemService workItemService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private SettingsService settingsService;

	@Autowired
	private JobsService jobsService;

	@Autowired
	private StatisticsService statisticsService;

	private static LoadingCache<Long, Lock> updateLocks = CacheBuilder.newBuilder()
			.maximumSize(100000)
			.expireAfterWrite(150, TimeUnit.MILLISECONDS)
			.build(
					new CacheLoader<Long, Lock>()
					{
						public Lock load(Long key)
						{
							return new ReentrantLock();
						}
					});

	@Transactional(rollbackFor = Exception.class)
	public void createTestRun(TestRun testRun) throws ServiceException
	{
		testRunMapper.createTestRun(testRun);
	}
	
	@Transactional(readOnly = true)
	public TestRun getTestRunById(long id) throws ServiceException
	{
		return testRunMapper.getTestRunById(id);
	}
	
	@Transactional(readOnly = true)
	public TestRun getNotNullTestRunById(long id) throws ServiceException
	{
		TestRun testRun = getTestRunById(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		return testRun;
	}
	
	@Transactional(readOnly = true)
	public SearchResult<TestRun> searchTestRuns(TestRunSearchCriteria sc) throws ServiceException
	{
		actualizeSearchCriteriaDate(sc);
		SearchResult<TestRun> results = new SearchResult<TestRun>();
		results.setPage(sc.getPage());
		results.setPageSize(sc.getPageSize());
		results.setSortOrder(sc.getSortOrder());
		List<TestRun> testRuns = testRunMapper.searchTestRuns(sc);
		for(TestRun testRun: testRuns) {
			if (!StringUtils.isEmpty(testRun.getConfigXML())) {
				for (Argument arg : testConfigService.readConfigArgs(testRun.getConfigXML())) {
					if (!StringUtils.isEmpty(arg.getValue())) {
						if ("browser_version".equals(arg.getKey())&&!arg.getValue().equals("*")&&!arg.getValue().equals("")&&arg.getValue()!=null) {
							testRun.setPlatform(testRun.getPlatform() + " " + arg.getValue());
						}
					}
				}
			}
		}
		results.setResults(testRuns);
		results.setTotalResults(testRunMapper.getTestRunsSearchCount(sc));
		return results;
	}
	
	@Transactional(readOnly = true)
	public TestRun getTestRunByCiRunId(String ciRunId) throws ServiceException
	{
		return !StringUtils.isEmpty(ciRunId) ? testRunMapper.getTestRunByCiRunId(ciRunId) : null;
	}
	
	@Transactional(readOnly = true)
	public TestRun getTestRunByIdFull(long id) throws ServiceException
	{
		return testRunMapper.getTestRunByIdFull(id);
	}

	@Transactional(readOnly = true)
	public TestRun getTestRunByIdFull(String id) throws ServiceException
	{
		return id.matches("\\d+") ? testRunMapper.getTestRunByIdFull(Long.valueOf(id)) : getTestRunByCiRunIdFull(id);
	}

	@Transactional(readOnly = true)
	public TestRun getTestRunByCiRunIdFull(String ciRunId) throws ServiceException
	{
		return testRunMapper.getTestRunByCiRunIdFull(ciRunId);
	}
	
	@Transactional(readOnly = true)
	public List<TestRun> getTestRunsByStatusAndStartedBefore(Status status, Date startedBefore) throws ServiceException
	{
		return testRunMapper.getTestRunsByStatusAndStartedBefore(status, startedBefore);
	}
	
	@Transactional(readOnly = true)
	public List<TestRun> getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(Long jobId, Integer buildNumber) throws ServiceException
	{
		return testRunMapper.getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(jobId, buildNumber);
	}
	
	@Transactional(readOnly = true)
	public Map<Long, TestRun> getLatestJobTestRuns(String env, List<Long> jobIds) throws ServiceException
	{
		Map<Long, TestRun> jobTestRuns = new HashMap<>();
		for(TestRun tr : testRunMapper.getLatestJobTestRuns(env, jobIds))
		{
			jobTestRuns.put(tr.getJob().getId(), tr);
		}
		return jobTestRuns;
	}

	@Transactional(readOnly = true)
	public TestRun getLatestJobTestRunByBranchAndJobName(String branch, String jobName) throws ServiceException
	{
		return testRunMapper.getLatestJobTestRunByBranch(branch, jobsService.getJobByName(jobName).getId());
	}

	@Transactional(rollbackFor = Exception.class)
	public TestRun queueTestRun(QueueTestRunParamsType queueTestRunParams, User user) throws ServiceException
	{
		TestRun testRun;
		// Check if testRun with provided ci_run_id exists in DB (mostly for queued and aborted without execution)
		TestRun existingRun = getTestRunByCiRunId(queueTestRunParams.getCiRunId());
		if(existingRun == null)
		{
			testRun = getLatestJobTestRunByBranchAndJobName(queueTestRunParams.getBranch(),
					queueTestRunParams.getJobName());
			if (testRun != null)
			{
				Long latestTestRunId = testRun.getId();
				if (!StringUtils.isEmpty(queueTestRunParams.getCiParentUrl())) {
					Job job = jobsService.createOrUpdateJobByURL(queueTestRunParams.getCiParentUrl(), user);
					testRun.setUpstreamJob(job);
				}
				if (!StringUtils.isEmpty(queueTestRunParams.getCiParentBuild())) {
					testRun.setUpstreamJobBuildNumber(Integer.valueOf(queueTestRunParams.getCiParentBuild()));
				}
				testRun.setEnv(queueTestRunParams.getEnv());
				testRun.setCiRunId(queueTestRunParams.getCiRunId());
				testRun.setElapsed(null);
				testRun.setPlatform(null);
				testRun.setConfigXML(null);
				testRun.setComments(null);
				testRun.setReviewed(false);

				//make sure to reset below3 fields for existing run as well
				testRun.setStatus(Status.QUEUED);
				testRun.setStartedAt(Calendar.getInstance().getTime());
				testRun.setBuildNumber(Integer.valueOf(queueTestRunParams.getBuildNumber()));

				createTestRun(testRun);
				List<Test> tests = testService.getTestsByTestRunId(latestTestRunId);
				TestRun queuedTestRun = getTestRunByCiRunId(queueTestRunParams.getCiRunId());
				for (Test test : tests) {
					testService.createQueuedTest(test, queuedTestRun.getId());
				}
			}
		} else {
			testRun = existingRun;

			testRun.setStatus(Status.QUEUED);
			testRun.setStartedAt(Calendar.getInstance().getTime());
			testRun.setBuildNumber(Integer.valueOf(queueTestRunParams.getBuildNumber()));
			updateTestRun(testRun);
		}
		return testRun;
	}

	@Transactional(rollbackFor = Exception.class)
	public TestRun updateTestRun(TestRun testRun) throws ServiceException
	{
		testRunMapper.updateTestRun(testRun);
		return testRun;
	}

	@CacheEvict(value = "environments", allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestRun(TestRun testRun) throws ServiceException
	{
		testRunMapper.deleteTestRun(testRun);
	}

	@CacheEvict(value = "environments", allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestRunById(Long id) throws ServiceException
	{
		testRunMapper.deleteTestRunById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestRun startTestRun(TestRun testRun) throws ServiceException
	{
		if(!StringUtils.isEmpty(testRun.getCiRunId()))
		{
			TestRun existingTestRun = testRunMapper.getTestRunByCiRunId(testRun.getCiRunId());
			if(existingTestRun != null)
			{
				existingTestRun.setBuildNumber(testRun.getBuildNumber());
				existingTestRun.setConfigXML(testRun.getConfigXML());
				testRun = existingTestRun;
				//TODO: investigate if startedBy should be also copied
			}
			LOGGER.info("Looking for test run with CI ID: " + testRun.getCiRunId());
			LOGGER.info("Test run found: " + String.valueOf(existingTestRun != null));
		}
		else
		{
			testRun.setCiRunId(UUID.randomUUID().toString());
			LOGGER.info("Generating new test run CI ID: " + testRun.getCiRunId());
		}

		initTestRunWithXml(testRun);

		// Initialize starting time
		testRun.setStartedAt(Calendar.getInstance().getTime());
		testRun.setReviewed(false);
		testRun.setEta(testRunMapper.getTestRunEtaByTestSuiteId(testRun.getTestSuite().getId()));
		
		// New test run
		if(testRun.getId() == null || testRun.getId() == 0)
		{
			switch (testRun.getStartedBy())
			{
				case HUMAN:
					if(testRun.getUser() == null)
					{
						throw new InvalidTestRunException("Specify userName if started by HUMAN!");
					}
					break;
				case SCHEDULER:
					testRun.setUpstreamJobBuildNumber(null);
					testRun.setUpstreamJob(null);
					testRun.setUser(null);
					break;
				case UPSTREAM_JOB:
					if(testRun.getUpstreamJob() == null || testRun.getUpstreamJobBuildNumber() == null)
					{
						throw new InvalidTestRunException("Specify upstreamJobId and upstreaBuildNumber if started by UPSTREAM_JOB!");
					}
					break;
			}
			
			if(testRun.getWorkItem() != null && !StringUtils.isEmpty(testRun.getWorkItem().getJiraId()))
			{
				testRun.setWorkItem(workItemService.createOrGetWorkItem(testRun.getWorkItem()));
			}
			testRun.setStatus(IN_PROGRESS);
			createTestRun(testRun);
		}
		// Existing test run
		else
		{
			testRun.setStatus(IN_PROGRESS);
			updateTestRun(testRun);
		}
		return testRun;
	}

	public void initTestRunWithXml(TestRun testRun) {

		if(!StringUtils.isEmpty(testRun.getConfigXML()))
		{
			for(Argument arg : testConfigService.readConfigArgs(testRun.getConfigXML()))
			{
				if(!StringUtils.isEmpty(arg.getValue()))
				{
					if("env".equals(arg.getKey()))
					{
						testRun.setEnv(arg.getValue());
					}
					else if("browser".equals(arg.getKey()) && !StringUtils.isEmpty(arg.getValue()))
					{
						if(StringUtils.isEmpty(testRun.getPlatform()) || (! StringUtils.isEmpty(testRun.getPlatform())
								&& ! testRun.getPlatform().equalsIgnoreCase("api")))
						{
							testRun.setPlatform(arg.getValue());
						}
					}
					else if("platform".equals(arg.getKey()) && !StringUtils.isEmpty(arg.getValue()) && !arg.getValue().equals("NULL")
							&& !arg.getValue().equals("*"))
					{
						testRun.setPlatform(arg.getValue());
					}
					else if("mobile_platform_name".equals(arg.getKey()) && StringUtils.isEmpty(testRun.getPlatform()))
					{
						testRun.setPlatform(arg.getValue() );
					}
					else if("app_version".equals(arg.getKey()))
					{
						testRun.setAppVersion(arg.getValue());
					}
				}
			}
		}
	};

	@Transactional(rollbackFor = Exception.class)
	public TestRun abortTestRun(TestRun testRun, String abortCause) throws ServiceException, InterruptedException
	{
		if(testRun != null){
			List<Test> tests = testService.getTestsByTestRunId(testRun.getId());
			if(IN_PROGRESS.equals(testRun.getStatus()))
			{
				for(Test test : tests)
				{
					if(IN_PROGRESS.equals(test.getStatus()))
					{
						testService.abortTest(test, abortCause);
					}
				}
			}
			testRun = markAsReviewed(testRun.getId(), abortCause);
			testRun.setStatus(Status.ABORTED);
			updateTestRun(testRun);
			calculateTestRunResult(testRun.getId(), true);
		}
		return testRun;
	}

	@Transactional(rollbackFor = Exception.class)
	public TestRun markAsReviewed(Long id, String comment) throws ServiceException
	{
		addComment(id, comment);
		TestRun tr = getTestRunByIdFull(id);
		TestRunStatistics.Action action = tr.isReviewed() ? TestRunStatistics.Action.MARK_AS_REVIEWED : TestRunStatistics.Action.MARK_AS_NOT_REVIEWED;
		updateStatistics(tr.getId(), action);
		if(!"undefined failure".equalsIgnoreCase(comment)){
			tr.setReviewed(true);
		}
		tr = updateTestRun(tr);
		return tr;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<TestRun> getJobsTestRuns(JobSearchCriteria sc) throws ServiceException
	{
		return testRunMapper.getJobsTestRuns(sc);
	}

	@Transactional(rollbackFor = Exception.class)
	public TestRun calculateTestRunResult(long id, boolean finishTestRun) throws ServiceException, InterruptedException
	{
		TestRun testRun = getNotNullTestRunById(id);

		List<Test> tests = testService.getTestsByTestRunId(testRun.getId());

		//Aborted testruns don't need status recalculation (already recalculated on abort end-point)
		if (!ABORTED.equals(testRun.getStatus()))
		{
			// Do not update test run status if tests are running and one clicks mark as passed or mark as known issue (https://github.com/qaprosoft/zafira/issues/34)
			if ((finishTestRun || !IN_PROGRESS.equals(testRun.getStatus())))
			{
				for (Test test : tests)
				{
					if (IN_PROGRESS.equals(test.getStatus()))
					{
						testService.skipTest(test);
					}
				}
				testRun.setStatus(tests.size() > 0 ? PASSED : SKIPPED);
				testRun.setKnownIssue(false);
				testRun.setBlocker(false);
				for (Test test : tests)
				{
					if (test.isKnownIssue())
					{
						testRun.setKnownIssue(true);
					}
					if (test.isBlocker())
					{
						testRun.setBlocker(true);
					}
					if (Arrays.asList(FAILED, SKIPPED).contains(test.getStatus()) && (!test.isKnownIssue() || (
							test.isKnownIssue() && test.isBlocker())))
					{
						testRun.setStatus(FAILED);
						break;
					}
				}
			}
		}
		if(finishTestRun && testRun.getStartedAt() != null)
		{
			LocalDateTime startedAt = new LocalDateTime(testRun.getStartedAt());
			LocalDateTime finishedAt = new LocalDateTime(Calendar.getInstance().getTime());
			Integer elapsed = Seconds.secondsBetween(startedAt, finishedAt).getSeconds();
			// according to https://github.com/qaprosoft/zafira/issues/748
			if(testRun.getElapsed() != null)
			{
				testRun.setElapsed(testRun.getElapsed() + elapsed);
			} 
			else
			{
				testRun.setElapsed(elapsed);
			}
		}
		
		updateTestRun(testRun);
		testService.updateTestRerunFlags(testRun, tests);
		return testRun;
	}
	
	@Transactional(readOnly=true)
	public Map<Long, Map<String, Test>> createCompareMatrix(List<Long> testRunIds) throws ServiceException
	{
		Map<Long, Map<String, Test>> testNamesWithTests = new HashMap<>();
		Set<String> testNames = new HashSet<>();
		for(Long id : testRunIds)
		{
			List<Test> tests = testService.getTestsByTestRunId(id);
			testNamesWithTests.put(id, new HashMap<String, Test>());
			for(Test test : tests)
			{
				testNames.add(test.getName());
				testNamesWithTests.get(id).put(test.getName(), test);
			}
		}
		for(Long testRunId : testRunIds)
		{
			for(String testName : testNames)
			{
				if(testNamesWithTests.get(testRunId).get(testName) == null)
				{
					testNamesWithTests.get(testRunId).put(testName, null);
				}
			}
		}
		return testNamesWithTests;
	}
	
	@Transactional(readOnly=true)
	public String sendTestRunResultsEmail(final String testRunId, boolean showOnlyFailures, boolean showStacktrace, final String ... recipients) throws ServiceException, JAXBException
	{
		TestRun testRun = getTestRunByIdFull(testRunId);
		if(testRun == null)
		{
			throw new TestRunNotFoundException("No test runs found by ID: " + testRunId);
		}
		List<Test> tests = testService.getTestsByTestRunId(testRunId);
		return sendTestRunResultsNotification(testRun, tests, showOnlyFailures, showStacktrace, recipients);
	}

	public String sendTestRunResultsNotification(final TestRun testRun, final List<Test> tests, boolean showOnlyFailures, boolean showStacktrace, final String ... recipients) throws ServiceException, JAXBException
	{
		Configuration configuration = readConfiguration(testRun.getConfigXML());
		// Forward from API to Web
		configuration.getArg().add(new Argument("zafira_service_url", StringUtils.removeEnd(wsURL, "-ws")));

		for (Test test: tests)
		{
			test.setArtifacts(new TreeSet<>(test.getArtifacts()));
		}
		TestRunResultsEmail email = new TestRunResultsEmail(configuration, testRun, tests);
		email.setJiraURL(settingsService.getSettingByType(JIRA_URL));
		email.setShowOnlyFailures(showOnlyFailures);
		email.setShowStacktrace(showStacktrace);
		email.setSuccessRate(calculateSuccessRate(testRun));
		return emailService.sendEmail(email, recipients);
	}

	@Transactional(readOnly=true)
	public String exportTestRunHTML(final String id) throws ServiceException, JAXBException
	{
		TestRun testRun = getTestRunByIdFull(id);
		if(testRun == null)
		{
			throw new ServiceException("No test runs found by ID: " + id);
		}
		Configuration configuration = readConfiguration(testRun.getConfigXML());
		configuration.getArg().add(new Argument("zafira_service_url", wsURL));

		List<Test> tests = testService.getTestsByTestRunId(id);

		TestRunResultsEmail email = new TestRunResultsEmail(configuration, testRun, tests);
		email.setJiraURL(settingsService.getSettingByType(JIRA_URL));
		email.setSuccessRate(calculateSuccessRate(testRun));
		return freemarkerUtil.getFreeMarkerTemplateContent(email.getTemplate(), email);
	}
	
	public Configuration readConfiguration(String xml) throws JAXBException
	{
        Configuration configuration = new Configuration();
        if (!StringUtils.isEmpty(xml)) {
            ByteArrayInputStream xmlBA = new ByteArrayInputStream(xml.getBytes());
            configuration = (Configuration) JAXBContext.newInstance(Configuration.class).createUnmarshaller().unmarshal(xmlBA);
            IOUtils.closeQuietly(xmlBA);
        }
		return configuration;
	}
	
	public static int calculateSuccessRate(TestRun testRun)
	{
		int total = testRun.getPassed() + testRun.getFailed() + testRun.getSkipped();
		double rate = (double) testRun.getPassed() / (double) total;
		return total > 0 ? (new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(100))).intValue() : 0;
	}
	
	@Transactional
	public void addComment(long id, String comment) throws ServiceException
	{
		TestRun testRun = getTestRunById(id);
		if(testRun == null)
		{
			throw new ServiceException("No test run found by ID: " + id);
		}
		testRun.setComments(comment);
		updateTestRun(testRun);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "environments", condition = "#result != null && #result.size() != 0")
	public List<String> getEnvironments() throws ServiceException
	{
		return testRunMapper.getEnvironments();
	}

	@Transactional(readOnly = true)
	public List<String> getPlatforms() throws ServiceException
	{
		return testRunMapper.getPlatforms();
	}

	/**
	 * Method contains a container needed to do a work safe with Test run statistics cache thread
	 * @param testRunId - test run id
	 * @param statisticsFunction - action with cached values
	 * @return testRunStatistics with value incremented
	 */
	private TestRunStatistics updateStatisticsSafe(Long testRunId, Function<TestRunStatistics, TestRunStatistics> statisticsFunction)
	{
		TestRunStatistics testRunStatistics = null;
		Lock lock = null;
		try
		{
			lock = updateLocks.get(testRunId);
			lock.lock();
			testRunStatistics = statisticsService.getTestRunStatistic(testRunId);
			testRunStatistics = statisticsFunction.apply(testRunStatistics);
			testRunStatistics = statisticsService.setTestRunStatistic(testRunStatistics);
		} catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		} finally
		{
			if(lock != null)
			{
				lock.unlock();
			}
		}
		return testRunStatistics;
	}

	/**
	 * Increment value to statistics
	 * @param testRunStatistics - cached test run statistic
	 * @param status - test status
	 * @param increment - integer (to increment - positive number, to decrement - negative number)
	 * @return testRunStatistics with value incremented
	 */
	private TestRunStatistics updateStatistics(TestRunStatistics testRunStatistics, Status status, int increment)
	{
		switch (status)
		{
			case PASSED:
				testRunStatistics.setPassed(testRunStatistics.getPassed() + increment);
				break;
			case FAILED:
				testRunStatistics.setFailed(testRunStatistics.getFailed() + increment);
				break;
			case SKIPPED:
				testRunStatistics.setSkipped(testRunStatistics.getSkipped() + increment);
				break;
			case ABORTED:
				testRunStatistics.setAborted(testRunStatistics.getAborted() + increment);
				break;
			case IN_PROGRESS:
				testRunStatistics.setInProgress(testRunStatistics.getInProgress() + increment);
				break;
			default:
				break;
		}
		return testRunStatistics;
	}

	/**
	 * Update statistic by {@link com.qaprosoft.zafira.models.dto.TestRunStatistics.Action}
	 * @param testRunId - test run id
	 * @return new statistics
	 */
	public TestRunStatistics updateStatistics(Long testRunId, TestRunStatistics.Action action)
	{
		return updateStatisticsSafe(testRunId, testRunStatistics -> {
			switch (action)
			{
			case MARK_AS_KNOWN_ISSUE:
				testRunStatistics.setFailedAsKnown(testRunStatistics.getFailedAsKnown() + 1);
				break;
			case REMOVE_KNOWN_ISSUE:
				testRunStatistics.setFailedAsKnown(testRunStatistics.getFailedAsKnown() - 1);
				break;
			case MARK_AS_BLOCKER:
				testRunStatistics.setFailedAsBlocker(testRunStatistics.getFailedAsBlocker() + 1);
				break;
			case REMOVE_BLOCKER:
				testRunStatistics.setFailedAsBlocker(testRunStatistics.getFailedAsBlocker() - 1);
				break;
			case MARK_AS_REVIEWED:
				testRunStatistics.setReviewed(true);
				break;
			case MARK_AS_NOT_REVIEWED:
				testRunStatistics.setReviewed(false);
				break;
			default:
				break;
			}
			return testRunStatistics;
		});
	}

	/**
	 * Calculate new statistic by {@link com.qaprosoft.zafira.models.db.TestRun getStatus}
	 * @param testRunId - test run id
	 * @param status - new status
	 * @return new statistics
	 */
	public TestRunStatistics updateStatistics(Long testRunId, Status status, boolean isRerun)
	{
		int increment = isRerun ? -1 : 1;
		TestRunStatistics trs = updateStatisticsSafe(testRunId, testRunStatistics -> ! status.equals(IN_PROGRESS) && (isRerun || testRunStatistics.getInProgress() > 0)
				? updateStatistics(testRunStatistics, IN_PROGRESS, -increment) :  testRunStatistics);
		if(trs != null && trs.getQueued() > 0)
		{
			updateStatisticsSafe(testRunId, testRunStatistics -> {
				testRunStatistics.setQueued(testRunStatistics.getQueued() - 1);
				return testRunStatistics;
			});
		}

		return updateStatisticsSafe(testRunId, testRunStatistics -> updateStatistics(testRunStatistics, status, increment));
	}

	/**
	 * Calculate new statistic by {@link com.qaprosoft.zafira.models.db.TestRun getStatus}
	 * @param testRunId - test run id
	 * @param newStatus - new test status
	 * @param currentStatus - current test status
	 * @return new statistics
	 */
	public TestRunStatistics updateStatistics(Long testRunId, Status newStatus, Status currentStatus)
	{
		return updateStatisticsSafe(testRunId, testRunStatistics -> updateStatistics(
				updateStatistics(testRunStatistics, currentStatus, -1), newStatus, 1));
	}

	public TestRunStatistics updateStatistics(Long testRunId, Status status)
	{
		return updateStatistics(testRunId, status, false);
	}
}
