package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestSearchCriteria;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.WorkItem;

public interface TestMapper
{
	void createTest(Test test);

	Test getTestById(long id);
	
	List<Test> getTestsByTestRunId(long testRunId);

	List<Test> getTestsByWorkItemId(long workItemId);
	
	void createTestWorkItem(@Param("test") Test test, @Param("workItem") WorkItem workItem);

	void deleteTestWorkItemByWorkItemIdAndTestId(@Param("workItemId") long workItemId, @Param("testId") long testId);

	void deleteTestWorkItemByTestIdAndWorkItemType(@Param("testId") long testId, @Param("type") WorkItem.Type type);
	
	void updateTest(Test test);
	
	void updateTestsNeedRerun(@Param("ids") List<Long> ids, @Param("rerun") boolean needRerun);

	void deleteTestById(long id);

	void deleteTest(Test test);
	
	void deleteTestByTestRunIdAndTestCaseIdAndLogURL(@Param("testRunId") long testRunId, @Param("testCaseId") long testCaseId, @Param("logURL") String logURL);
	
	List<Test> searchTests(TestSearchCriteria sc);
	
	Integer getTestsSearchCount(TestSearchCriteria sc);
}
