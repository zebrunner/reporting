package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.monitor.Monitor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Kirirll Bugrim
 * @version 1.0
 */
public interface MonitorMapper {

    void createMonitor(Monitor monitor);

    Monitor getMonitorById(long id);

    Monitor getMonitorByMonitorName(String monitorName);

    void updateMonitor(Monitor monitor);

    void deleteMonitorById(long id);

    void deleteMonitor(Monitor monitor);

    void addUserToMonitor(@Param("monitorId") Long monitorId, @Param("userId")Long userId);

    List<String> getListEmailsByMonitorId(long id);

    List<String> getListEmailsByMonitor(Monitor monitor);



}
