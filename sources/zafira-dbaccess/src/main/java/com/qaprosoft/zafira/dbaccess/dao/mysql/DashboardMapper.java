package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.dbaccess.model.Dashboard;
import com.qaprosoft.zafira.dbaccess.model.Widget;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

public interface DashboardMapper
{
	void createDashboard(Dashboard dashboard);
	
	Dashboard getDashboardById(Long id);

	List<Dashboard> getAllDashboards();
	
	void updateDashboard(Dashboard dashboard);

	void deleteDashboardById(long id);
	
	void addDashboardWidget(@Param("dashboardId") Long dashboardId, @Param("widget") Widget widget);

	void deleteDashboardWidget(@Param("dashboardId") Long dashboardId, @Param("widgetId") Long widgetId);

	void updateDashboardWidget(@Param("dashboardId") Long dashboardId, @Param("widget") Widget widget);

	List<Dashboard> getAllDashboardsByType(Dashboard.Type type);
}
