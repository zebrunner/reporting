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
package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.MonitorMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.MonitorSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.models.db.MonitorStatus;
import com.qaprosoft.zafira.models.dto.monitor.MonitorCheckType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.jobs.MonitorEmailNotificationTask;
import com.qaprosoft.zafira.services.services.application.jobs.MonitorJobService;
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

	@Autowired
	private MonitorEmailNotificationTask monitorEmailNotificationTask;

	@Transactional(rollbackFor = Exception.class)
	public Monitor createMonitor(Monitor monitor)
	{
		monitorMapper.createMonitor(monitor);
		monitorJobService.addJob(monitor);
		return monitor;
	}

	@Transactional(rollbackFor = Exception.class)
	public MonitorStatus createMonitorStatus(MonitorStatus monitorStatus, Long monitorId)
	{
		monitorMapper.createMonitorStatus(monitorStatus, monitorId);
		return monitorStatus;
	}

	@Transactional(rollbackFor = Exception.class)
	public MonitorCheckType checkMonitor(Monitor monitor, boolean check) throws ServiceException
	{
		if(monitor.getId() != null && ! check)
		{
			monitor = getMonitorById(monitor.getId());
		}
		Integer actualCode = monitorEmailNotificationTask.getResponseCode(monitor);
		Boolean success = actualCode.equals(monitor.getExpectedCode());
		if(monitor.getId() != null && ! check)
		{
			monitor.setSuccess(success);
			updateMonitor(monitor, false, false);
		}
		return new MonitorCheckType(actualCode, success);
	}

	@Transactional(readOnly = true)
	public SearchResult<Monitor> searchMonitors(MonitorSearchCriteria sc)
	{
		SearchResult<Monitor> searchResult = new SearchResult<>();
		searchResult.setPage(sc.getPage());
		searchResult.setPageSize(sc.getPageSize());
		searchResult.setSortOrder(sc.getSortOrder());
		searchResult.setResults(monitorMapper.searchMonitors(sc));
		searchResult.setTotalResults(monitorMapper.getMonitorsSearchCount(sc));
		return searchResult;
	}

	@Transactional(readOnly = true)
	public MonitorStatus getLastMonitorStatus(Long monitorId)
	{
		return monitorMapper.getLastMonitorStatus(monitorId);
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
			currentMonitor.setEnvironment(monitor.getEnvironment());
			currentMonitor.setComment(monitor.getComment());
			currentMonitor.setTag(monitor.getTag());
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
