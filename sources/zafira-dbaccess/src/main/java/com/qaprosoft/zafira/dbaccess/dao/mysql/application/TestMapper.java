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

import java.util.List;

import com.qaprosoft.zafira.models.db.application.Status;
import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestSearchCriteria;
import com.qaprosoft.zafira.models.db.application.Test;
import com.qaprosoft.zafira.models.db.application.WorkItem;

public interface TestMapper
{
	void createTest(Test test);

	Test getTestById(long id);
	
	List<Test> getTestsByTestRunId(long testRunId);

	List<Test> getTestsByTestRunCiRunId(String ciRunId);

	List<Test> getTestsByTestRunIdAndStatus(@Param("testRunId") long testRunId, @Param("status") Status status);

	List<Test> getTestsByWorkItemId(long workItemId);
	
	void createTestWorkItem(@Param("test") Test test, @Param("workItem") WorkItem workItem);

	void deleteTestWorkItemByWorkItemIdAndTestId(@Param("workItemId") long workItemId, @Param("testId") long testId);

	void deleteTestWorkItemByTestIdAndWorkItemType(@Param("testId") long testId, @Param("type") WorkItem.Type type);
	
	void updateTest(Test test);
	
	void updateTestsNeedRerun(@Param("ids") List<Long> ids, @Param("rerun") boolean needRerun);

	void deleteTestById(long id);

	void deleteTestByTestRunIdAndNameAndStatus(@Param("testRunId") long testRunId, @Param("testName") String testName, @Param("status") Status status);

	void deleteTest(Test test);
	
	List<Test> searchTests(TestSearchCriteria sc);
	
	Integer getTestsSearchCount(TestSearchCriteria sc);
}
