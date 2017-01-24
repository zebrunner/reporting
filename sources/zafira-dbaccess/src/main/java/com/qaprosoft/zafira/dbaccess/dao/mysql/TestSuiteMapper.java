package com.qaprosoft.zafira.dbaccess.dao.mysql;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.models.db.TestSuite;

public interface TestSuiteMapper
{
	void createTestSuite(TestSuite testSuite);

	TestSuite getTestSuiteById(long id);

	TestSuite getTestSuiteByName(String name);

	TestSuite getTestSuiteByNameAndFileNameAndUserId(@Param("name") String name, @Param("fileName") String fileName, @Param("userId") long userId);

	void updateTestSuite(TestSuite testSuite);

	void deleteTestSuiteById(long id);

	void deleteTestSuite(TestSuite testSuite);
}
