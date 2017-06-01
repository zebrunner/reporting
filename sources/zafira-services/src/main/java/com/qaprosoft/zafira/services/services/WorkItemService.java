package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.WorkItemMapper;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.db.WorkItem.Type;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class WorkItemService
{
	@Autowired
	private WorkItemMapper workItemMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createWorkItem(WorkItem workItem) throws ServiceException
	{
		workItemMapper.createWorkItem(workItem);
	}
	
	@Transactional(readOnly = true)
	public WorkItem getWorkItemById(long id) throws ServiceException
	{
		return workItemMapper.getWorkItemById(id);
	}
	
	@Transactional(readOnly = true)
	public WorkItem getWorkItemByJiraIdAndType(String jiraId, Type type) throws ServiceException
	{
		return workItemMapper.getWorkItemByJiraIdAndType(jiraId, type);
	}
	
	@Transactional(readOnly = true)
	public WorkItem getWorkItemByTestCaseIdAndHashCode(long testCaseId, int hashCode) throws ServiceException
	{
		return workItemMapper.getWorkItemByTestCaseIdAndHashCode(testCaseId, hashCode);
	}
	
	@Transactional(readOnly = true)
	public List<WorkItem> getWorkItemsByTestCaseIdAndType(long testCaseId, Type type) throws ServiceException
	{
		return workItemMapper.getWorkItemsByTestCaseIdAndType(testCaseId, type);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public WorkItem updateWorkItem(WorkItem workItem) throws ServiceException
	{
		workItemMapper.updateWorkItem(workItem);
		return workItem;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteWorkItemById(long id) throws ServiceException
	{
		workItemMapper.deleteWorkItemById(id);
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteWorkItem(WorkItem workItem) throws ServiceException
	{
		workItemMapper.deleteWorkItem(workItem);
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteKnownIssuesByTestId(long testId) throws ServiceException
	{
		workItemMapper.deleteKnownIssuesByTestId(testId);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public WorkItem createOrGetWorkItem(WorkItem newWorkItem) throws ServiceException
	{
		WorkItem workItem = getWorkItemByJiraIdAndType(newWorkItem.getJiraId(), newWorkItem.getType());
		if(workItem == null)
		{
			createWorkItem(newWorkItem);
			return newWorkItem;
		}
		else
		{
			return workItem;
		}
	}
}