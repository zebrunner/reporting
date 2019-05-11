/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.dbaccess.dao.mysql.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchCriteria;
import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.models.db.MonitorStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MonitorMapper {

    void createMonitor(Monitor monitor);

    void createMonitorStatus(@Param(value = "monitorStatus") MonitorStatus monitorStatus, @Param(value = "monitorId") Long monitorId);

    Monitor getMonitorById(long id);

    Monitor getMonitorByMonitorName(String monitorName);

    List<Monitor> searchMonitors(SearchCriteria sc);

    Integer getMonitorsSearchCount(SearchCriteria sc);

    MonitorStatus getLastMonitorStatus(@Param(value = "monitorId") Long monitorId);

    void updateMonitor(Monitor monitor);

    void deleteMonitorById(long id);

    void deleteMonitor(Monitor monitor);

    List<Monitor> getAllMonitors();

    Integer getMonitorsCount();

}
