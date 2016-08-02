package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.statistics.TestCaseImplementationCount;
import com.qaprosoft.zafira.dbaccess.dao.mysql.statistics.TestCaseOwnersCount;
import com.qaprosoft.zafira.dbaccess.model.TestCase;


public interface TestCaseMapper
{
	void createTestCase(TestCase testCase);

	TestCase getTestCaseById(long id);
	
	List<TestCase> getTestCasesByUsername(String userName);

	TestCase getTestCaseByClassAndMethod(@Param("testClass") String testClass, @Param("testMethod") String testMethod);

	void updateTestCase(TestCase testCase);

	void deleteTestCaseById(long id);

	void deleteTestCase(TestCase testCase);
	
	List<TestCase> searchTestCases(TestCaseSearchCriteria sc);
	
	Integer getTestCasesSearchCount(TestCaseSearchCriteria sc);
	
	List<TestCaseOwnersCount> getTestCaseOwnersStatistics();
	
	List<TestCaseImplementationCount> getTestCaseImplementationStatistics();
}
