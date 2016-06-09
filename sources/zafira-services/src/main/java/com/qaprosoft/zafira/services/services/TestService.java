package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestMapper;
import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.TestConfig;
import com.qaprosoft.zafira.dbaccess.model.WorkItem;
import com.qaprosoft.zafira.dbaccess.model.Test.Status;
import com.qaprosoft.zafira.dbaccess.model.push.TestPush;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestNotFoundException;
import com.qaprosoft.zafira.services.services.thirdparty.push.Channel;
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
	@Qualifier("pubNubService")
	private IPushService notificationService;
	
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
			test.setDemoURL(null);
			test.setLogURL(null);
			test.setStatus(Status.IN_PROGRESS);
			updateTest(test);
		}
		
		notificationService.publish(Channel.TEST_EVENTS, new TestPush(test));
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
		
		notificationService.publish(Channel.TEST_EVENTS, existingTest);
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
}
