package com.zafira.services.services;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zafira.dbaccess.dao.mysql.TestCaseMapper;
import com.zafira.dbaccess.model.TestCase;
import com.zafira.dbaccess.model.User;
import com.zafira.services.exceptions.ServiceException;

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
	public TestCase [] initiateTestCases(TestCase [] testCases) throws ServiceException
	{
		if(ArrayUtils.isEmpty(testCases)) throw new ServiceException("No test cases found!");
		
		int index = 0;
		for(TestCase tc : testCases)
		{
			User user = userService.createUser(tc.getUser().getUserName());
			TestCase testCase = getTestCaseByClassAndMethod(tc.getTestClass(), tc.getTestMethod());
			if(testCase == null)
			{
				testCase = tc;
				testCase.setUser(user);
				createTestCase(testCase);
			}
			else
			{
				testCase.setUser(user);
			}
			testCases[index++] = testCase;
		}
		return testCases;
	}
}
