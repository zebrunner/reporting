package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;
import java.util.Map;

import com.qaprosoft.zafira.dbaccess.model.Dashboard;
import com.qaprosoft.zafira.dbaccess.utils.SQLAdapter;

public interface DashboardMapper
{
	List<Map<String, Object>> executeSQL(SQLAdapter sql);
	
	void createDashboard(Dashboard dashboard);

	Dashboard getDashboardById(Long id);

	List<Dashboard> getAllDashboards();

	void updateDashboard(Dashboard dashboard);

	void deleteDashboardById(long id);
}
