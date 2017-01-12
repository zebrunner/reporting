package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.dbaccess.model.Status;
import com.qaprosoft.zafira.dbaccess.model.TestRun;
import com.qaprosoft.zafira.dbaccess.model.config.Argument;


public interface TestRunMapper
{
	void createTestRun(TestRun testRun);

	TestRun getTestRunById(long id);
	
	TestRun getTestRunByIdFull(long id);
	
	TestRun getTestRunByCiRunId(String ciRunId);
	
	List<TestRun> getTestRunsForRerun(@Param("testSuiteId") long testSuiteId, @Param("jobId") long jobId, @Param("upstreamJobId") long upstreamJobId, @Param("upstreamBuildNumber") long upstreamBuildNumber, @Param("uniqueArgs") List<Argument> uniqueArgs);
	
	void updateTestRun(TestRun testRun);

	void deleteTestRunById(long id);

	void deleteTestRun(TestRun testRun);
	
	List<TestRun> getTestRunsByStatusAndStartedBefore(@Param("status") Status status, @Param("startedBefore") Date startedBefore);

	List<TestRun> searchTestRuns(TestRunSearchCriteria sc);
	
	Integer getTestRunsSearchCount(TestRunSearchCriteria sc);
	
	Integer getTestRunEtaByTestSuiteId(long testRunId);
	
	List<TestRun> getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(@Param("jobId") Long jobId, @Param("buildNumber") Integer buildNumber);
}
