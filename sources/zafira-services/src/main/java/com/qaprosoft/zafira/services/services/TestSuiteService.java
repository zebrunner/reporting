package com.qaprosoft.zafira.services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestSuiteMapper;
import com.qaprosoft.zafira.dbaccess.model.TestSuite;
import com.qaprosoft.zafira.dbaccess.model.User;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class TestSuiteService
{
	@Autowired
	private TestSuiteMapper testSuiteMapper;
	
	@Autowired
	private UserService userService;
	
	@Transactional(rollbackFor = Exception.class)
	public void createTestSuite(TestSuite testSuite) throws ServiceException
	{
		testSuiteMapper.createTestSuite(testSuite);
	}
	
	@Transactional(readOnly = true)
	public TestSuite getTestSuiteById(long id) throws ServiceException
	{
		return testSuiteMapper.getTestSuiteById(id);
	}
	
	@Transactional(readOnly = true)
	public TestSuite getTestSuiteByName(String name) throws ServiceException
	{
		return testSuiteMapper.getTestSuiteByName(name);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestSuite updateTestSuite(TestSuite testSuite) throws ServiceException
	{
		testSuiteMapper.updateTestSuite(testSuite);
		return testSuite;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestSuite(TestSuite testSuite) throws ServiceException
	{
		testSuiteMapper.deleteTestSuite(testSuite);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestSuite initializeTestSuite(TestSuite newTestSuite) throws ServiceException
	{
		User user = userService.createUser(newTestSuite.getUser().getUserName());
		newTestSuite.setUser(user);
		TestSuite testSuite = getTestSuiteByName(newTestSuite.getName());
		if(testSuite == null)
		{
			createTestSuite(newTestSuite);
		}
		else if(!testSuite.equals(newTestSuite))
		{
			newTestSuite.setId(testSuite.getId());
			updateTestSuite(testSuite);
		}
		return testSuite;
	}
}
