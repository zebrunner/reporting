/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.MonitorSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.models.dto.monitor.MonitorCheckType;
import com.qaprosoft.zafira.models.dto.monitor.MonitorType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.MonitorService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api("Monitors API")
@CrossOrigin
@RequestMapping(path = "api/monitors", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class MonitorsApiController extends AbstractController {

    @Autowired
    private MonitorService monitorsService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Create monitor", nickname = "createMonitor", httpMethod = "POST", response = Monitor.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_MONITORS')")
    @PostMapping()
    public MonitorType createMonitor(@Valid @RequestBody MonitorType monitor) throws ServiceException {
        return mapper.map(monitorsService.createMonitor(mapper.map(monitor, Monitor.class)), MonitorType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Check monitor", nickname = "checkMonitor", httpMethod = "POST", response = MonitorCheckType.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_MONITORS')")
    @PostMapping("/check")
    public MonitorCheckType checkMonitor(
            @RequestParam(value = "check", required = false) Boolean check,
            @Valid @RequestBody MonitorType monitor
    ) throws ServiceException {
        return monitorsService.checkMonitor(mapper.map(monitor, Monitor.class), check);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Search monitors", nickname = "searchMonitors", httpMethod = "POST", response = SearchResult.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('VIEW_MONITORS')")
    @PostMapping("/search")
    public SearchResult<MonitorType> searchMonitors(@RequestBody MonitorSearchCriteria searchCriteria) {
        SearchResult<Monitor> searchResult = monitorsService.searchMonitors(searchCriteria);

        SearchResult<MonitorType> result = new SearchResult<>();
        result.setTotalResults(searchResult.getTotalResults());
        result.setSortOrder(searchResult.getSortOrder());
        result.setPageSize(searchResult.getPageSize());
        result.setPage(searchResult.getPage());
        List<MonitorType> results = searchResult.getResults().stream()
                                                   .map(monitor -> mapper.map(monitor, MonitorType.class))
                                                   .collect(Collectors.toList());
        result.setResults(results);

        return result;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete monitor", nickname = "deleteMonitor", httpMethod = "DELETE")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_MONITORS')")
    @DeleteMapping("/{id}")
    public void deleteMonitor(@PathVariable("id") long id) {
        monitorsService.deleteMonitorById(id);
    }

    @ResponseStatusDetails
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Update monitor", nickname = "updateMonitor", httpMethod = "PUT", response = Group.class)
    @PreAuthorize("hasPermission('MODIFY_MONITORS')")
    @PutMapping()
    public MonitorType updateMonitor(
            @RequestParam(value = "switchJob", required = false) Boolean switchJob,
            @Valid @RequestBody MonitorType monitor
    ) throws ServiceException {
        return mapper.map(monitorsService.updateMonitor(mapper.map(monitor, Monitor.class), switchJob, true), MonitorType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all monitors", nickname = "getAllMonitors", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasAnyPermission('VIEW_MONITORS', 'MODIFY_MONITORS')")
    @GetMapping()
    public List<MonitorType> getAllMonitors() throws ServiceException {
        List<MonitorType> monitorTypes = new ArrayList<>();
        List<Monitor> monitors = monitorsService.getAllMonitors();
        for (Monitor monitor : monitors) {
            monitorTypes.add(mapper.map(monitor, MonitorType.class));
        }
        return monitorTypes;
    }

    @ResponseStatusDetails
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Get monitor by id", nickname = "getMonitorById", httpMethod = "GET", response = Monitor.class)
    @PreAuthorize("hasAnyPermission('VIEW_MONITORS', 'MODIFY_MONITORS')")
    @GetMapping("/{id}")
    public MonitorType getMonitorById(@PathVariable("id") long id) throws ServiceException {
        return mapper.map(monitorsService.getMonitorById(id), MonitorType.class);
    }

    @ResponseStatusDetails
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Get monitors count", nickname = "getMonitorsCount", httpMethod = "GET", response = Integer.class)
    @PreAuthorize("hasAnyPermission('VIEW_MONITORS', 'MODIFY_MONITORS')")
    @GetMapping("/count")
    public Integer getMonitorsCount() throws ServiceException {
        return monitorsService.getMonitorsCount();
    }

}

