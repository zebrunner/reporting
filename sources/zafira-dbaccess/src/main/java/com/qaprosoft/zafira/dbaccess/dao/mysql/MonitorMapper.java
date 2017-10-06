package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.Monitor;

import java.util.List;

public interface MonitorMapper
{

	void createMonitor(Monitor monitor);

	Monitor getMonitorById(long id);

	Monitor getMonitorByMonitorName(String monitorName);

	void updateMonitor(Monitor monitor);

	void deleteMonitorById(long id);

	void deleteMonitor(Monitor monitor);

	List<Monitor> getAllMonitors();

	Integer getMonitorsCount();

}
