package com.qaprosoft.zafira.dbaccess.dao.mysql;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.dbaccess.model.TestRun;


public interface TestRunMapper
{
	void createTestRun(TestRun testRun);

	TestRun getTestRunById(long id);
	
	TestRun getTestRunForRerun(@Param("testSuiteId") long testSuiteId, @Param("jobId") long jobId, @Param("upstreamJobId") long upstreamJobId, @Param("upstreamBuildNumber") long upstreamBuildNumber);
	
	void updateTestRun(TestRun testRun);

	void deleteTestRunById(long id);

	void deleteTestRun(TestRun testRun);
}
