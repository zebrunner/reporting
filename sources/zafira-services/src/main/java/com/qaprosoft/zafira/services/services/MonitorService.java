package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.MonitorMapper;
import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.jobs.MonitorJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Kirill Bugrim
 * @version 1.0
 */

@Service
public class MonitorService {

    @Autowired
    private MonitorMapper monitorMapper;
    @Autowired
    private MonitorJobService monitorJobService;

    @Transactional(rollbackFor = Exception.class)
    public Monitor createMonitor(Monitor monitor) {
        monitorMapper.createMonitor(monitor);
        monitorJobService.addJob(monitor);
        return monitor;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMonitor(Monitor monitor) {
        monitorMapper.deleteMonitor(monitor);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMonitorById(long id) {
        monitorJobService.deleteJob(id);
        monitorMapper.deleteMonitorById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Monitor updateMonitor(Monitor monitor) throws ServiceException {
        monitorJobService.updateMonitor(monitor);
        monitorMapper.updateMonitor(monitor);

        return monitor;
    }

    @Transactional(readOnly = true)
    public List<Monitor> getAllMonitors() {
        return monitorMapper.getAllMonitors();
    }

    @Transactional(readOnly = true)
    public Monitor getMonitorById(long id) {
        return monitorMapper.getMonitorById(id);
    }

    @Transactional(readOnly = true)
    public Integer getMonitorsCount() {
        return monitorMapper.getMonitorsCount();
    }
}
