package com.qaprosoft.zafira.services.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.qaprosoft.zafira.dbaccess.dao.mysql.TestCaseMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class TestCaseService
{
	@Autowired
	private TestCaseMapper testCaseMapper;
	
	private static LoadingCache<String, Lock> updateLocks = CacheBuilder.newBuilder()
			.maximumSize(100000)
			.expireAfterWrite(15, TimeUnit.SECONDS)
			.build(
					new CacheLoader<String, Lock>()
					{
						public Lock load(String key)
						{
							return new ReentrantLock();
						}
					});
	
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
	@Cacheable(value="testCases", key="{ #testClass,  #testMethod }")
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
	public TestCase createOrUpdateCase(TestCase newTestCase) throws ServiceException, ExecutionException
	{
		final String CLASS_METHOD = newTestCase.getTestClass() + "." + newTestCase.getTestMethod();
		try
		{
			// Locking by class name and method name to avoid concurrent save of the same test case https://github.com/qaprosoft/zafira/issues/46
			updateLocks.get(CLASS_METHOD).lock();
			
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
		}
		finally
		{
			updateLocks.get(CLASS_METHOD).unlock();
		}
		return newTestCase;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestCase [] createOrUpdateCases(TestCase [] newTestCases) throws ServiceException, ExecutionException
	{
		int index = 0;
		for(TestCase newTestCase : newTestCases)
		{
			newTestCases[index++] = createOrUpdateCase(newTestCase);
		}
		return newTestCases;
	}
	
	@Transactional(readOnly = true)
	public SearchResult<TestCase> searchTestCases(TestCaseSearchCriteria sc) throws ServiceException
	{
		SearchResult<TestCase> results = new SearchResult<TestCase>();
		results.setPage(sc.getPage());
		results.setPageSize(sc.getPageSize());
		results.setSortOrder(sc.getSortOrder());
		results.setResults(testCaseMapper.searchTestCases(sc));
		results.setTotalResults(testCaseMapper.getTestCasesSearchCount(sc));
		return results;
	}
}
