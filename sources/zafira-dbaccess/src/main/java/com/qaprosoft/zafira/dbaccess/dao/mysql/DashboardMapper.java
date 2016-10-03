package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.dbaccess.model.Dashboard;
//public interface DashboardMapper
//{
//	List<Map<String, Object>> executeSQL(SQLAdapter sql);
//	
//	void createDashboard(Dashboard dashboard);
//
//	Dashboard getDashboardById(Long id);
//
//	List<Dashboard> getAllDashboards();
//
//	void updateDashboard(Dashboard dashboard);
//
//	void deleteDashboardById(long id);
//}
import com.qaprosoft.zafira.dbaccess.model.Widget;

public interface DashboardMapper
{
	void createDashboard(Dashboard dashboard);
	
	Dashboard getDashboardById(Long id);

	List<Dashboard> getAllDashboards();
	
	void updateDashboard(Dashboard dashboard);

	void deleteDashboardById(long id);
	
	void addWidget(@Param("dashboardId") Long dashboardId, @Param("widget") Widget widget);

	void removeWidget(Long id);

	void updateWidget(@Param("dashboard") Dashboard dashboard, @Param("widget") Widget widget);
}
