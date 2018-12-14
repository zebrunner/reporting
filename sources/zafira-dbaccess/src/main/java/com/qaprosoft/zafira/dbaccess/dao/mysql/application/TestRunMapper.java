/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.dbaccess.dao.mysql.application;

import java.util.Date;
import java.util.List;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.JobSearchCriteria;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;


public interface TestRunMapper
{
	void createTestRun(TestRun testRun);

	TestRun getTestRunById(long id);
	
	TestRun getTestRunByIdFull(long id);
	
	TestRun getTestRunByCiRunId(String ciRunId);

	TestRun getTestRunByCiRunIdFull(String ciRunId);

	TestRun getLatestJobTestRunByBranch(@Param("branch") String branch, @Param("jobId") Long jobId);

	TestRunStatistics getTestRunStatistics(Long id);

	List<TestRun> getTestRunsForRerun(@Param("testSuiteId") long testSuiteId, @Param("jobId") long jobId, @Param("upstreamJobId") long upstreamJobId, @Param("upstreamBuildNumber") long upstreamBuildNumber, @Param("uniqueArgs") List<Argument> uniqueArgs);
	
	void updateTestRun(TestRun testRun);

	void deleteTestRunById(long id);

	void deleteTestRun(TestRun testRun);
	
	List<TestRun> getTestRunsByStatusAndStartedBefore(@Param("status") Status status, @Param("startedBefore") Date startedBefore);

	List<TestRun> searchTestRuns(TestRunSearchCriteria sc);

	List<TestRun> getTestRunsForSmartRerun(JobSearchCriteria sc);
	
	Integer getTestRunsSearchCount(TestRunSearchCriteria sc);
	
	Integer getTestRunEtaByTestSuiteId(long testRunId);
	
	List<TestRun> getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(@Param("jobId") Long jobId, @Param("buildNumber") Integer buildNumber);
	
	List<TestRun> getLatestJobTestRuns(@Param("env") String env, @Param("jobIds") List<Long> jobIds);

	List<String> getEnvironments();

	List<String> getPlatforms();
}
