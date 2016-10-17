package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.DashboardMapper;
import com.qaprosoft.zafira.dbaccess.model.Dashboard;
import com.qaprosoft.zafira.dbaccess.model.Widget;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class DashboardService
{
	@Autowired
	private DashboardMapper dashboardMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public Dashboard createDashboard(Dashboard dashboard) throws ServiceException
	{
		dashboardMapper.createDashboard(dashboard);
		return dashboard;
	}
	
	@Transactional(readOnly = true)
	public Dashboard getDashboardById(long id) throws ServiceException
	{
		return dashboardMapper.getDashboardById(id);
	}
	
	@Transactional(readOnly = true)
	public List<Dashboard> getAllDashboards() throws ServiceException
	{
		return dashboardMapper.getAllDashboards();
	}
	
	@Transactional(readOnly = true)
	public List<Dashboard> getAllDashboardsByType(Dashboard.Type type) throws ServiceException
	{
		return dashboardMapper.getAllDashboardsByType(type);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Dashboard updateDashboard(Dashboard dashboard) throws ServiceException
	{
		dashboardMapper.updateDashboard(dashboard);
		return dashboard;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteDashboardById(Long id) throws ServiceException
	{
		dashboardMapper.deleteDashboardById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Widget addDashboardWidget(Long dashboardId, Widget widget) throws ServiceException
	{
		dashboardMapper.addDashboardWidget(dashboardId, widget);
		return widget;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Widget updateDashboardWidget(Long dashboardId, Widget widget) throws ServiceException
	{
		dashboardMapper.updateDashboardWidget(dashboardId, widget);
		return widget;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteDashboardWidget(Long dashboardId, Long widgetId) throws ServiceException
	{
		dashboardMapper.deleteDashboardWidget(dashboardId, widgetId);
	}
}