package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import com.qaprosoft.zafira.dbaccess.model.Test;

public interface TestMapper
{
	void createTest(Test test);

	Test getTestById(long id);
	
	List<Test> getTestsByTestRunId(long testRunId);

	void updateTest(Test test);

	void deleteTestById(long id);

	void deleteTest(Test test);
}
