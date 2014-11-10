package com.zafira.dbaccess.dao.mysql;

import org.apache.ibatis.annotations.Param;

import com.zafira.dbaccess.model.TestCase;


public interface TestCaseMapper
{
	void createTestCase(TestCase testCase);

	TestCase getTestCaseById(long id);

	TestCase getTestCaseByClassAndMethod(@Param("testClass") String testClass, @Param("testMethod") String testMethod);

	void updateTestCase(TestCase testCase);

	void deleteTestCaseById(long id);

	void deleteTestCase(TestCase testCase);
}
