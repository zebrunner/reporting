package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.MonitorMapper;
import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.jobs.MonitorJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MonitorService
{
	@Autowired
	private MonitorMapper monitorMapper;

	@Autowired
	private MonitorJobService monitorJobService;

	@Transactional(rollbackFor = Exception.class)
	public Monitor createMonitor(Monitor monitor)
	{
		monitorMapper.createMonitor(monitor);
		monitorJobService.addJob(monitor);
		return monitor;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteMonitor(Monitor monitor)
	{
		monitorJobService.deleteJob(monitor.getId());
		monitorMapper.deleteMonitor(monitor);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteMonitorById(long id)
	{
		monitorJobService.deleteJob(id);
		monitorMapper.deleteMonitorById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public Monitor updateMonitor(Monitor monitor, Boolean switchJob, boolean updateJob) throws ServiceException
	{
		Monitor currentMonitor = getMonitorById(monitor.getId());
		if (switchJob)
		{
			currentMonitor.setMonitorEnabled(monitor.isMonitorEnabled());
			monitorJobService.updateMonitor(currentMonitor);
			monitorJobService.switchMonitor(monitor.isMonitorEnabled(), monitor.getId());
		} else
		{
			currentMonitor.setName(monitor.getName());
			currentMonitor.setUrl(monitor.getUrl());
			currentMonitor.setHttpMethod(monitor.getHttpMethod());
			currentMonitor.setRequestBody(monitor.getRequestBody());
			currentMonitor.setCronExpression(monitor.getCronExpression());
			currentMonitor.setNotificationsEnabled(monitor.isNotificationsEnabled());
			currentMonitor.setRecipients(monitor.getRecipients());
			currentMonitor.setType(monitor.getType());
			currentMonitor.setExpectedCode(monitor.getExpectedCode());
			currentMonitor.setSuccess(monitor.isSuccess());
			currentMonitor.setMonitorEnabled(monitor.isMonitorEnabled());
			if(updateJob) {
				monitorJobService.updateMonitor(currentMonitor);
			}
			if(!currentMonitor.isMonitorEnabled()) {
				monitorJobService.switchMonitor(monitor.isMonitorEnabled(), monitor.getId());
			}
		}
		monitorMapper.updateMonitor(currentMonitor);
		return currentMonitor;
	}

	@Transactional(readOnly = true)
	public List<Monitor> getAllMonitors()
	{
		return monitorMapper.getAllMonitors();
	}

	@Transactional(readOnly = true)
	public Monitor getMonitorById(long id)
	{
		return monitorMapper.getMonitorById(id);
	}

	@Transactional(readOnly = true)
	public Integer getMonitorsCount()
	{
		return monitorMapper.getMonitorsCount();
	}
}
