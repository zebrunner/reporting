package com.qaprosoft.zafira.services.services;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.services.exceptions.InvalidTestRunException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;
import com.qaprosoft.zafira.services.services.SettingsService.SettingType;
import com.qaprosoft.zafira.services.services.emails.TestRunResultsEmail;

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
	private TestConfigService testConfigService;

	@Autowired
	private WorkItemService workItemService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private SettingsService settingsService;
	
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
	public SearchResult<TestRun> searchTestRuns(TestRunSearchCriteria sc) throws ServiceException
	{
		SearchResult<TestRun> results = new SearchResult<TestRun>();
		results.setPage(sc.getPage());
		results.setPageSize(sc.getPageSize());
		results.setSortOrder(sc.getSortOrder());
		results.setResults(testRunMapper.searchTestRuns(sc));
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
	
	@Transactional(rollbackFor = Exception.class)
	public TestRun updateTestRun(TestRun testRun) throws ServiceException
	{
		testRunMapper.updateTestRun(testRun);
		return testRun;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestRun(TestRun testRun) throws ServiceException
	{
		testRunMapper.deleteTestRun(testRun);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestRunById(Long id) throws ServiceException
	{
		testRunMapper.deleteTestRunById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestRun startTestRun(TestRun testRun) throws ServiceException, JAXBException
	{
		if(!StringUtils.isEmpty(testRun.getCiRunId()))
		{
			TestRun existingTestRun = testRunMapper.getTestRunByCiRunId(testRun.getCiRunId());
			if(existingTestRun != null)
			{
				existingTestRun.setBuildNumber(testRun.getBuildNumber());
				testRun = existingTestRun;
			}
			LOGGER.info("Looking for test run with CI ID: " + testRun.getCiRunId());
			LOGGER.info("Test run found: " + String.valueOf(existingTestRun != null));
		}
		else
		{
			testRun.setCiRunId(UUID.randomUUID().toString());
			LOGGER.info("Generating new test run CI ID: " + testRun.getCiRunId());
		}
		
		if(!StringUtils.isEmpty(testRun.getConfigXML()))
		{
			for(Argument arg : testConfigService.readConfigArgs(testRun.getConfigXML(), false))
			{
				if(!StringUtils.isEmpty(arg.getValue()))
				{
					if("env".equals(arg.getKey()))
					{
						testRun.setEnv(arg.getValue());
					}
					else if("browser".equals(arg.getKey()))
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
		
		// Initialize starting time
		testRun.setStartedAt(Calendar.getInstance().getTime());
		testRun.setElapsed(null);
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
			testRun.setStatus(Status.IN_PROGRESS);
			createTestRun(testRun);
		}
		// Existing test run
		else
		{
			testRun.setStatus(Status.IN_PROGRESS);
			updateTestRun(testRun);
		}
		
		return testRun;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestRun calculateTestRunResult(long id, boolean finishTestRun) throws ServiceException, InterruptedException
	{
		TestRun testRun = getTestRunById(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		
		List<Test> tests = testService.getTestsByTestRunId(testRun.getId());
		
		// Do not update test run status if tests are running and one clicks mark as passed or mark as known issue (https://github.com/qaprosoft/zafira/issues/34)
		if(finishTestRun || !Status.IN_PROGRESS.equals(testRun.getStatus()))
		{
			// Make sure that all tests managed to register results before we calculate test run status
			for(Test test : tests)
			{
				// If any test IN_PROGRESS search tests once again after timeout
				if(Status.IN_PROGRESS.equals(test.getStatus()))
				{
					final long TIMEOUT = 10 * 1000;
					Thread.sleep(TIMEOUT);
					tests = testService.getTestsByTestRunId(testRun.getId());
					break;
				}
			}
			
			testRun.setStatus(tests.size() > 0 ? Status.PASSED : Status.SKIPPED);
			for(Test test : tests)
			{
				if(test.isKnownIssue())
				{
					testRun.setKnownIssue(true);
				}
				if((test.getStatus().equals(Status.FAILED) && !test.isKnownIssue()) || test.getStatus().equals(Status.SKIPPED))
				{
					testRun.setStatus(Status.FAILED);
					break;
				}
			}
		}
		
		if(finishTestRun && testRun.getStartedAt() != null)
		{
			LocalDateTime startedAt = new LocalDateTime(testRun.getStartedAt());
			LocalDateTime finishedAt = new LocalDateTime(Calendar.getInstance().getTime());
			testRun.setElapsed(Seconds.secondsBetween(startedAt, finishedAt).getSeconds());
		}
		
		updateTestRun(testRun);
		testService.updateTestRerunFlags(testRun, tests);
		return testRun;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestRun abortTestRun(long id) throws ServiceException
	{
		TestRun testRun = getTestRunById(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		testRun.setStatus(Status.ABORTED);
		updateTestRun(testRun);
//		TODO: Replace by websocket.
//		notificationService.publish(xmppChannel, new TestRunPush(getTestRunByIdFull(testRun.getId())));
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
	public String sendTestRunResultsEmail(final Long testRunId, boolean showOnlyFailures, boolean showStacktrace, final String ... recipients) throws ServiceException, JAXBException
	{
		TestRun testRun = getTestRunByIdFull(testRunId);
		if(testRun == null)
		{
			throw new ServiceException("No test runs found by ID: " + testRunId);
		}
		Configuration configuration = readConfiguration(testRun.getConfigXML());
		configuration.getArg().add(new Argument("zafira_service_url", wsURL));
		
		List<Test> tests = testService.getTestsByTestRunId(testRunId);
		
		TestRunResultsEmail email = new TestRunResultsEmail(configuration, testRun, tests);
		email.setJiraURL(settingsService.getSettingByName(SettingType.JIRA_URL));
		email.setShowOnlyFailures(showOnlyFailures);
		email.setShowStacktrace(showStacktrace);
		email.setSuccessRate(calculateSuccessRate(testRun));
		
		return emailService.sendEmail(email, recipients);
	}
	
	private Configuration readConfiguration(String xml) throws JAXBException
	{
		ByteArrayInputStream xmlBA = new ByteArrayInputStream(xml.getBytes());
		Configuration configuration = (Configuration) JAXBContext.newInstance(Configuration.class).createUnmarshaller().unmarshal(xmlBA);
		IOUtils.closeQuietly(xmlBA);
		return configuration;
	}
	
	private static int calculateSuccessRate(TestRun testRun)
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
	@Cacheable("environments")
	public List<String> getEnvironments() throws ServiceException
	{
		return testRunMapper.getEnvironments();
	}
}
