package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.statistics.TestCaseOwnersCount;
import com.qaprosoft.zafira.dbaccess.dao.mysql.statistics.TestStatusesCount;
import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.WorkItem;

public interface TestMapper
{
	void createTest(Test test);

	Test getTestById(long id);
	
	List<Test> getTestsByTestRunId(long testRunId);
	
	void createTestWorkItem(@Param("test") Test test, @Param("workItem") WorkItem workItem);

	void deleteTestWorkItemByTestId(long testId);
	
	void updateTest(Test test);

	void deleteTestById(long id);

	void deleteTest(Test test);
	
	void deleteTestByTestRunIdAndTestCaseIdAndLogURL(@Param("testRunId") long testRunId, @Param("testCaseId") long testCaseId, @Param("logURL") String logURL);
	
	List<Test> searchTests(TestSearchCriteria sc);
	
	Integer getTestsSearchCount(TestSearchCriteria sc);
	
	List<TestStatusesCount> getTestStatusesStatistics();
}
