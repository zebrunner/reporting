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
package com.qaprosoft.zafira.services.services.application;

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
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestCaseMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

import static com.qaprosoft.zafira.services.util.DateFormatter.*;

@Service
public class TestCaseService
{
	@Autowired
	private TestCaseMapper testCaseMapper;
	
	private static final LoadingCache<String, Lock> updateLocks = CacheBuilder.newBuilder()
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
	@Cacheable(value="testCases", key="{ T(com.qaprosoft.zafira.dbaccess.utils.TenancyContext).tenantName + ':' + #testClass,  T(com.qaprosoft.zafira.dbaccess.utils.TenancyContext).tenantName + ':' + #testMethod }")
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
		actualizeSearchCriteriaDate(sc);
		SearchResult<TestCase> results = new SearchResult<>();
		results.setPage(sc.getPage());
		results.setPageSize(sc.getPageSize());
		results.setSortOrder(sc.getSortOrder());
		results.setResults(testCaseMapper.searchTestCases(sc));
		results.setTotalResults(testCaseMapper.getTestCasesSearchCount(sc));
		return results;
	}
}
