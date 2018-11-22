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

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.db.WorkItem.Type;

public interface WorkItemMapper
{
	void createWorkItem(WorkItem workItem);

	WorkItem getWorkItemById(long id);

	WorkItem getWorkItemByJiraIdAndType(@Param("jiraId") String jiraId, @Param("type") Type type);
	
	WorkItem getWorkItemByTestCaseIdAndHashCode(@Param("testCaseId") long testCaseId, @Param("hashCode") int hashCode);
	
	List<WorkItem> getWorkItemsByTestCaseIdAndType(@Param("testCaseId") long testCaseId, @Param("type") Type type);

	void updateWorkItem(WorkItem workItem);

	void deleteWorkItemById(long id);

	void deleteWorkItem(WorkItem workItem);
	
	void deleteKnownIssuesByTestId(long id);
}
