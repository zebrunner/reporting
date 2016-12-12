package com.qaprosoft.zafira.services.services;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.dbaccess.model.Status;
import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.TestRun;
import com.qaprosoft.zafira.dbaccess.model.config.Argument;
import com.qaprosoft.zafira.dbaccess.model.config.Configuration;
import com.qaprosoft.zafira.services.exceptions.InvalidTestRunException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;
import com.qaprosoft.zafira.services.services.SettingsService.SettingType;
import com.qaprosoft.zafira.services.services.emails.TestRunResultsEmail;


@Service
public class TestRunService
{
	private static Logger LOGGER = LoggerFactory.getLogger(TestRunService.class);
	
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
				}
			}
		}
		
		// Initialize starting time
		testRun.setStartedAt(Calendar.getInstance().getTime());
		testRun.setElapsed(null);
		// testRun.setEta(calculateETA(testRun));
		
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
	public TestRun calculateTestRunResult(long id, boolean updateElapsedTime) throws ServiceException
	{
		TestRun testRun = getTestRunById(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		
		List<Test> tests = testService.getTestsByTestRunId(testRun.getId());
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
		
		if(updateElapsedTime && testRun.getStartedAt() != null)
		{
			LocalDateTime startedAt = new LocalDateTime(testRun.getStartedAt());
			LocalDateTime finishedAt = new LocalDateTime(Calendar.getInstance().getTime());
			testRun.setElapsed(Seconds.secondsBetween(startedAt, finishedAt).getSeconds());
		}
		
		updateTestRun(testRun);
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
	public String sendTestRunResultsEmail(final Long testRunId, boolean showOnlyFailures, final String ... recipients) throws ServiceException, JAXBException
	{
		TestRun testRun = getTestRunByIdFull(testRunId);
		if(testRun == null)
		{
			throw new ServiceException("No test runs found by ID: " + testRunId);
		}
		Configuration configuration = readConfiguration(testRun.getConfigXML());
		
		List<Test> tests = testService.getTestsByTestRunId(testRunId);
		
		TestRunResultsEmail email = new TestRunResultsEmail(configuration, testRun, tests);
		email.setJiraURL(settingsService.getSettingByName(SettingType.JIRA_URL));
		email.setShowOnlyFailures(showOnlyFailures);
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
	
	private Integer calculateETA(TestRun testRun) throws ServiceException
	{
		Integer eta = null;
		
		try
		{
			TestRunSearchCriteria sc = new TestRunSearchCriteria();
			sc.setTestSuiteId(testRun.getTestSuite().getId());
			sc.setPageSize(25);
			
			List<Integer> elapsed = new ArrayList<>();
			for(TestRun tr : searchTestRuns(sc).getResults())
			{
				if(tr.getElapsed() != null)
				{
					elapsed.add(tr.getElapsed());
				}
			}
			Collections.sort(elapsed);
			
			if(elapsed.size() > 0)
			{
				eta = elapsed.size() > 2 ? elapsed.get(elapsed.size() / 2) : (elapsed.get(elapsed.size() - 1));
			}
		}
		catch(Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		
		return eta;
	}
	
}
