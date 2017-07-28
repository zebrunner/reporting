package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.models.db.Widget;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

	List<Dashboard> getDashboardsByHidden(boolean hidden);

	List<Attribute> getAttributesByDashboardId(long dashboardId);

	Attribute getAttributeById(long attributeId);

	void createDashboardAttribute(@Param("dashboardId") long dashboardId, @Param("attribute") Attribute attribute);

	void updateAttribute(Attribute attribute);

	void deleteDashboardAttributeById(long attributeId);
}
