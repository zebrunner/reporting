package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import com.qaprosoft.zafira.dbaccess.model.WorkItem;

public interface WorkItemMapper
{
	void createWorkItem(WorkItem workItem);

	WorkItem getWorkItemById(long id);

	WorkItem getWorkItemByJiraId(String jiraId);
	
	List<WorkItem> getWorkItemsByUsername(String userName);
	
	void updateWorkItem(WorkItem workItem);

	void deleteWorkItemById(long id);

	void deleteWorkItem(WorkItem workItem);
}
