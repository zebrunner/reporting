package com.qaprosoft.zafira.services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.WorkItemMapper;
import com.qaprosoft.zafira.dbaccess.model.WorkItem;
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
	public WorkItem getWorkItemByJiraId(String jiraId) throws ServiceException
	{
		return workItemMapper.getWorkItemByJiraId(jiraId);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public WorkItem updateWorkItem(WorkItem workItem) throws ServiceException
	{
		workItemMapper.updateWorkItem(workItem);
		return workItem;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteWorkItem(WorkItem workItem) throws ServiceException
	{
		workItemMapper.deleteWorkItem(workItem);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public WorkItem createOrGetWorkItem(WorkItem newWorkItem) throws ServiceException
	{
		WorkItem workItem = getWorkItemByJiraId(newWorkItem.getJiraId());
		if(workItem == null )
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
