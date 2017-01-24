package com.qaprosoft.zafira.dbaccess.dao.mysql;

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
