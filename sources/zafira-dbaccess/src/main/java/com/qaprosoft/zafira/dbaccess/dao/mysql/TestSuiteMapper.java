package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.dbaccess.model.TestSuite;


public interface TestSuiteMapper
{
	void createTestSuite(TestSuite testSuite);

	TestSuite getTestSuiteById(long id);

	TestSuite getTestSuiteByName(String name);

	void updateTestSuite(TestSuite testSuite);

	void deleteTestSuiteById(long id);

	void deleteTestSuite(TestSuite testSuite);
}
