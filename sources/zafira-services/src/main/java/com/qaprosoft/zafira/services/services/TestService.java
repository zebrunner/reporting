package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestSearchCriteria;
import com.qaprosoft.zafira.models.db.*;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestService
{
	private static final String INV_COUNT = "InvCount";
	
	@Autowired
	private TestMapper testMapper;
	
	@Autowired
	private WorkItemService workItemService;
	
	@Autowired
	private TestConfigService testConfigService;
	
	@Autowired
	private TestCaseService testCaseService;
	
	@Autowired
	private TestRunService testRunService;
	
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
					if(!StringUtils.isEmpty(jiraId))
					{
						WorkItem workItem = workItemService.createOrGetWorkItem(new WorkItem(jiraId));
						testMapper.createTestWorkItem(test, workItem);
					}
				}
			}
		}
		// Existing test
		else
		{
			test.setMessage(null);
			test.setFinishTime(null);
			test.setStatus(Status.IN_PROGRESS);
			test.setKnownIssue(false);
			updateTest(test);
			workItemService.deleteKnownIssuesByTestId(test.getId());
		}
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
		existingTest.setDemoURL(test.getDemoURL());
		existingTest.setLogURL(test.getLogURL());
		
		if(test.getMessage() != null)
		{
			existingTest.setMessage(test.getMessage());
		}
		
		if(Status.FAILED.equals(test.getStatus()))
		{
			WorkItem knownIssue = workItemService.getWorkItemByTestCaseIdAndHashCode(existingTest.getTestCaseId(), getTestMessageHashCode(test.getMessage()));
			if(knownIssue != null)
			{
				existingTest.setKnownIssue(true);
				testMapper.createTestWorkItem(existingTest, knownIssue);
				if(existingTest.getWorkItems() == null)
				{
					existingTest.setWorkItems(new ArrayList<WorkItem>());
				}
				existingTest.getWorkItems().add(knownIssue);
			}
		}
		testMapper.updateTest(existingTest);
		
		TestCase testCase = testCaseService.getTestCaseById(test.getTestCaseId());
		if(testCase != null)
		{
			testCase.setStatus(test.getStatus());
			testCaseService.updateTestCase(testCase);
		}
		return existingTest;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Test markTestAsPassed(long id) throws ServiceException
	{
		Test test = getTestById(id);
		if(test == null)
		{
			throw new TestNotFoundException();
		}
		
		test.setStatus(Status.PASSED);
		updateTest(test);
		
		TestCase testCase = testCaseService.getTestCaseById(test.getTestCaseId());
		if(testCase != null)
		{
			testCase.setStatus(test.getStatus());
			testCaseService.updateTestCase(testCase);
		}
		
		testRunService.calculateTestRunResult(test.getTestRunId(), false);
		
		return test;
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
	
	@Transactional(rollbackFor = Exception.class)
	public WorkItem createTestKnownIssue(long testId, WorkItem workItem) throws ServiceException
	{
		Test test = getTestById(testId);
		if(test != null)
		{
			workItem.setHashCode(getTestMessageHashCode(test.getMessage()));
			test.setKnownIssue(true);
			updateTest(test);
		}
		workItemService.createWorkItem(workItem);
		testMapper.createTestWorkItem(test, workItem);
		testRunService.calculateTestRunResult(test.getTestRunId(), false);
		return workItem;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void updateTestRerunFlags(TestRun testRun, List<Test> tests) throws ServiceException
	{
		List<Long> allTestCaseIds = new ArrayList<>();
		List<Long> failedTestCaseIds = new ArrayList<>();
		
		for(Test test : tests)
		{
			allTestCaseIds.add(test.getTestCaseId());
			
			if((test.getStatus().equals(Status.FAILED) && !test.isKnownIssue()) || test.getStatus().equals(Status.SKIPPED))
			{
				failedTestCaseIds.add(test.getTestCaseId());
				if(!test.isNeedRerun())
				{
					test.setNeedRerun(true);
					updateTest(test);
				}
			}
			else
			{
				if(test.isNeedRerun())
				{
					test.setNeedRerun(false);
					updateTest(test);
				}
			}
			// Data-providers without TUID
			if(test.getName().contains(INV_COUNT) && !test.isNeedRerun())
			{
				test.setNeedRerun(true);
				updateTest(test);
			}
		}
		
		if(testRun.isClassMode() && !Status.PASSED.equals(testRun))
		{
			TestCaseSearchCriteria sc = new TestCaseSearchCriteria();
			sc.setIds(allTestCaseIds);
			
			Map<Long, String> idToClass = new HashMap<>();
			List<String> failedTestClasses = new ArrayList<>();
			for(TestCase tc : testCaseService.searchTestCases(sc).getResults())
			{
				idToClass.put(tc.getId(), tc.getTestClass());
				if(failedTestCaseIds.contains(tc.getId()))
				{
					failedTestClasses.add(tc.getTestClass());
				}
			}
			
			for(Test test : tests)
			{
				if(failedTestClasses.contains(idToClass.get(test.getTestCaseId())) && !test.isNeedRerun())
				{
					test.setNeedRerun(true);
					updateTest(test);
				}
			}
		}
	}
	
	private int getTestMessageHashCode(String message)
	{
		return message != null ? message.replaceAll("\\d+", "*").replaceAll("\\[.*\\]", "*").hashCode() : 0;
	}
}
