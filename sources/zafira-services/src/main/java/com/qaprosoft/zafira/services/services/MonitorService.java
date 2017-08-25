package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.MonitorMapper;
import com.qaprosoft.zafira.models.db.monitor.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Kirill Bugrim
 *
 * @version 1.0
 */

@Service
public class MonitorService {

    @Autowired
    private MonitorMapper monitorMapper;


    @Transactional(rollbackFor = Exception.class)
    public void createMonitor(Monitor monitor){
        monitorMapper.createMonitor(monitor);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMonitor(Monitor monitor){
        monitorMapper.deleteMonitor(monitor);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMonitorById(long id){
        monitorMapper.deleteMonitorById(id);
    }

    @Transactional(readOnly = true)
    public List<String> getListEmailsByMonitorId(long id){
      return monitorMapper.getListEmailsByMonitorId(id);
    }

    @Transactional(readOnly = true)
    public List<String> getListEmailsByMonitor(Monitor monitor){
        return monitorMapper.getListEmailsByMonitor(monitor);
    }

}
