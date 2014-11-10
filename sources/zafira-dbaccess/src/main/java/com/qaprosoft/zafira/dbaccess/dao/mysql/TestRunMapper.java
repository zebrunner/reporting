package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.dbaccess.model.TestRun;


public interface TestRunMapper
{
	void createTestRun(TestRun testRun);

	TestRun getTestRunById(long id);

	void updateTestRun(TestRun testRun);

	void deleteTestRunById(long id);

	void deleteTestRun(TestRun testRun);
}
