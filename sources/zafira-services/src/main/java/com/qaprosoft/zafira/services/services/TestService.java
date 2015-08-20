package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestMapper;
import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.WorkItem;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class TestService
{
	@Autowired
	private TestMapper testMapper;
	
	@Autowired
	private WorkItemService workItemService;
	
	@Transactional(rollbackFor = Exception.class)
	public Test createTest(Test test, List<String> jiraIds) throws ServiceException
	{
		testMapper.createTest(test);
		if(jiraIds != null)
		{
			for(String jiraId : jiraIds)
			{
				WorkItem workItem = workItemService.createOrGetWorkItem(new WorkItem(jiraId));
				testMapper.createTestWorkItem(test, workItem);
			}
		}
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
	public void deleteTestByTestRunIdAndTestCaseIdAndTestLogURL(Test test) throws ServiceException
	{
		testMapper.deleteTestByTestRunIdAndTestCaseIdAndTestLogURL(test.getTestRunId(), test.getTestCaseId(), test.getLogURL());
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestWorkItemByTestId(long testId) throws ServiceException
	{
		testMapper.deleteTestWorkItemByTestId(testId);
	}
}
