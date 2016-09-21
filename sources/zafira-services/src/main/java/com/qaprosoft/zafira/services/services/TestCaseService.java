package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestCaseMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.statistics.TestCaseImplementationCount;
import com.qaprosoft.zafira.dbaccess.dao.mysql.statistics.TestCaseOwnersCount;
import com.qaprosoft.zafira.dbaccess.model.Status;
import com.qaprosoft.zafira.dbaccess.model.TestCase;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class TestCaseService
{
	@Autowired
	private TestCaseMapper testCaseMapper;
	
	@Autowired
	private UserService userService;
	
	@Transactional(rollbackFor = Exception.class)
	public void createTestCase(TestCase testCase) throws ServiceException
	{
		if(testCase.getStatus() == null)
		{
			testCase.setStatus(Status.UNKNOWN);
		}
		testCaseMapper.createTestCase(testCase);
	}
	
	@Transactional(readOnly = true)
	public TestCase getTestCaseById(long id) throws ServiceException
	{
		return testCaseMapper.getTestCaseById(id);
	}
	
	@Transactional(readOnly = true)
	public TestCase getTestCaseByClassAndMethod(String testClass, String testMethod) throws ServiceException
	{
		return testCaseMapper.getTestCaseByClassAndMethod(testClass, testMethod);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestCase updateTestCase(TestCase testCase) throws ServiceException
	{
		testCaseMapper.updateTestCase(testCase);
		return testCase;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestCase(TestCase testCase) throws ServiceException
	{
		testCaseMapper.deleteTestCase(testCase);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestCase createOrUpdateCase(TestCase newTestCase) throws ServiceException
	{
		TestCase testCase = getTestCaseByClassAndMethod(newTestCase.getTestClass(), newTestCase.getTestMethod());
		if(testCase == null)
		{
			createTestCase(newTestCase);
		}
		else if(!testCase.equals(newTestCase))
		{
			newTestCase.setId(testCase.getId());
			updateTestCase(newTestCase);
		}
		else
		{
			newTestCase = testCase;
		}
		return newTestCase;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestCase [] createOrUpdateCases(TestCase [] newTestCases) throws ServiceException
	{
		int index = 0;
		for(TestCase newTestCase : newTestCases)
		{
			newTestCases[index++] = createOrUpdateCase(newTestCase);
		}
		return newTestCases;
	}
	
	@Transactional(readOnly = true)
	public SearchResult<TestCase> searchTestRuns(TestCaseSearchCriteria sc) throws ServiceException
	{
		SearchResult<TestCase> results = new SearchResult<TestCase>();
		results.setPage(sc.getPage());
		results.setPageSize(sc.getPageSize());
		results.setSortOrder(sc.getSortOrder());
		results.setResults(testCaseMapper.searchTestCases(sc));
		results.setTotalResults(testCaseMapper.getTestCasesSearchCount(sc));
		return results;
	}
	
	@Transactional(readOnly = true)
	public List<TestCaseOwnersCount> getTestCaseOwnersStatistics(String project) throws ServiceException
	{
		return testCaseMapper.getTestCaseOwnersStatistics(project);
	}
	
	@Transactional(readOnly = true)
	public List<TestCaseImplementationCount> getTestCaseImplementationStatistics(String project) throws ServiceException
	{
		return testCaseMapper.getTestCaseImplementationStatistics(project);
	}
}
