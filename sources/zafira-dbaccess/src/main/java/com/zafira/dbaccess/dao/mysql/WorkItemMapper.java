package com.zafira.dbaccess.dao.mysql;

import com.zafira.dbaccess.model.WorkItem;

public interface WorkItemMapper
{
	void createWorkItem(WorkItem workItem);

	WorkItem getWorkItemById(long id);

	WorkItem getWorkItemByJiraId(String jiraId);

	void updateWorkItem(WorkItem workItem);

	void deleteWorkItemById(long id);

	void deleteWorkItem(WorkItem workItem);
}
