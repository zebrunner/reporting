package com.qaprosoft.zafira.services.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.statistics.TestStatusesCount;
import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.Test.Status;
import com.qaprosoft.zafira.dbaccess.model.TestConfig;
import com.qaprosoft.zafira.dbaccess.model.WorkItem;
import com.qaprosoft.zafira.dbaccess.model.push.TestPush;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestNotFoundException;
import com.qaprosoft.zafira.services.services.thirdparty.push.IPushService;

@Service
public class TestService
{
	@Autowired
	private TestMapper testMapper;
	
	@Autowired
	private WorkItemService workItemService;
	
	@Autowired
	private TestConfigService testConfigService;
	
	@Autowired
	private TestRunService testRunService;
	
	@Autowired
	@Qualifier("xmppService")
	private IPushService notificationService;
	
	@Value("${zafira.jabber.username}")
	private String xmppChannel;
	
	@Transactional(rollbackFor = Exception.class)
	public Test startTest(Test test, List<String> jiraIds, String configXML) throws ServiceException
	{
		// New test
		if(test.getId() == null || test.getId() == 0)
		{
			TestConfig config = testConfigService.createTestConfigForTest(test, configXML);
			test.setTestConfig(config);
			test.setStatus(Status.IN_PROGRESS);
			testMapper.createTest(test);
			if(jiraIds != null)
			{
				for(String jiraId : jiraIds)
				{
					WorkItem workItem = workItemService.createOrGetWorkItem(new WorkItem(jiraId));
					testMapper.createTestWorkItem(test, workItem);
				}
			}
		}
		// Existing test
		else
		{
			test.setMessage(null);
			test.setFinishTime(null);
			test.setStatus(Status.IN_PROGRESS);
			updateTest(test);
		}
		
		notificationService.publish(xmppChannel, new TestPush(test));
		return test;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Test finishTest(Test test) throws ServiceException
	{
		Test existingTest = testMapper.getTestById(test.getId());
		if(existingTest == null)
		{
			throw new TestNotFoundException();
		}
		
		existingTest.setFinishTime(test.getFinishTime());
		existingTest.setStatus(test.getStatus());
		existingTest.setRetry(test.getRetry());
		if(test.getMessage() != null)
		{
			existingTest.setMessage(test.getMessage());
		}
		testMapper.updateTest(existingTest);
		
		notificationService.publish(xmppChannel, new TestPush(existingTest));
		return existingTest;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Test createTestWorkItems(long id, List<String> jiraIds) throws ServiceException
	{
		Test test = getTestById(id);
		if(test == null)
		{
			throw new ServiceException("Test not found by id: " + id);
		}
		for(String jiraId : jiraIds)
		{
			WorkItem workItem = workItemService.createOrGetWorkItem(new WorkItem(jiraId));
			testMapper.createTestWorkItem(test, workItem);
		}
		return test;
	}
	
	@Transactional(readOnly = true)
	public Test getTestById(long id) throws ServiceException
	{
		return testMapper.getTestById(id);
	}
	
	@Transactional(readOnly = true)
	public List<Test> getTestsByTestRunId(long testRunId) throws ServiceException
	{
		return testMapper.getTestsByTestRunId(testRunId);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Test updateTest(Test test) throws ServiceException
	{
		testMapper.updateTest(test);
		return test;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTest(Test test) throws ServiceException
	{
		testMapper.deleteTest(test);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestById(long id) throws ServiceException
	{
		testMapper.deleteTestById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestByTestRunIdAndTestCaseIdAndLogURL(Test test) throws ServiceException
	{
		testMapper.deleteTestByTestRunIdAndTestCaseIdAndLogURL(test.getTestRunId(), test.getTestCaseId(), test.getLogURL());
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestWorkItemByTestId(long testId) throws ServiceException
	{
		testMapper.deleteTestWorkItemByTestId(testId);
	}
	
	@Transactional(readOnly = true)
	public SearchResult<Test> searchTests(TestSearchCriteria sc) throws ServiceException
	{
		SearchResult<Test> results = new SearchResult<Test>();
		results.setPage(sc.getPage());
		results.setPageSize(sc.getPageSize());
		results.setSortOrder(sc.getSortOrder());
		results.setResults(testMapper.searchTests(sc));
		results.setTotalResults(testMapper.getTestsSearchCount(sc));
		return results;
	}
	
	@Transactional(readOnly = true)
	public Map<Long, Map<Status, TestStatusesCount>> getTestStatusesStatistics() throws ServiceException
	{
		Map<Long, Map<Status, TestStatusesCount>> statistics = new HashMap<>();
		List<TestStatusesCount> results = testMapper.getTestStatusesStatistics();
		for(TestStatusesCount result : results)
		{
			long time = result.getDate().getTime();
			if(!statistics.containsKey(time))
			{
				statistics.put(time, new HashMap<Status, TestStatusesCount>());
				statistics.get(time).put(Status.IN_PROGRESS, new TestStatusesCount(0, Status.IN_PROGRESS));
				statistics.get(time).put(Status.PASSED, new TestStatusesCount(0, Status.PASSED));
				statistics.get(time).put(Status.FAILED, new TestStatusesCount(0, Status.FAILED));
				statistics.get(time).put(Status.SKIPPED, new TestStatusesCount(0, Status.SKIPPED));
			}
			statistics.get(time).put(result.getStatus(), result);
		}
		return statistics;
	}
}
