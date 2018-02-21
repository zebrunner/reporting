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
package com.qaprosoft.zafira.ws.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.MonitorSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.models.dto.monitor.MonitorCheckType;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.models.dto.monitor.MonitorType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.MonitorService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Monitors API")
@CrossOrigin
@RequestMapping("api/monitors")
public class MonitorsApiController extends AbstractController
{

	@Autowired
	private MonitorService monitorsService;

	@Autowired
	private Mapper mapper;

	@ResponseStatusDetails
	@ApiOperation(value = "Create monitor", nickname = "createMonitor", code = 200, httpMethod = "POST", response = Monitor.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_MONITORS')")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	MonitorType createMonitor(@Valid @RequestBody MonitorType monitor) throws ServiceException
	{
		return mapper.map(monitorsService.createMonitor(mapper.map(monitor, Monitor.class)), MonitorType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Check monitor", nickname = "checkMonitor", code = 200, httpMethod = "POST", response = MonitorCheckType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_MONITORS')")
	@RequestMapping(value = "check", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody MonitorCheckType checkMonitor(@RequestParam(value = "check", required = false) Boolean check, @Valid @RequestBody MonitorType monitor) throws ServiceException
	{
		return monitorsService.checkMonitor(mapper.map(monitor, Monitor.class), check);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Search monitors", nickname = "searchMonitors", code = 200, httpMethod = "POST", response = SearchResult.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('VIEW_MONITORS')")
	@RequestMapping(value = "search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<MonitorType> searchMonitors(@RequestBody MonitorSearchCriteria sc)
	{
		SearchResult<Monitor> sr = monitorsService.searchMonitors(sc);
		SearchResult<MonitorType> result = new SearchResult<>();
		result.setTotalResults(sr.getTotalResults());
		result.setSortOrder(sr.getSortOrder());
		result.setPageSize(sr.getPageSize());
		result.setPage(sr.getPage());
		result.setResults(sr.getResults().stream().map(monitor -> mapper.map(monitor, MonitorType.class)).collect(
				Collectors.toList()));
		return result;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete monitor", nickname = "deleteMonitor", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_MONITORS')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteMonitor(@PathVariable(value = "id") long id)
	{
		monitorsService.deleteMonitorById(id);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Update monitor", nickname = "updateMonitor", code = 200, httpMethod = "PUT", response = Group.class)
	@PreAuthorize("hasPermission('MODIFY_MONITORS')")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody MonitorType updateMonitor(@RequestParam(value = "switchJob", required = false) Boolean switchJob,
			@Valid @RequestBody MonitorType monitor) throws ServiceException
	{
		return mapper.map(monitorsService.updateMonitor(mapper.map(monitor, Monitor.class), switchJob, true), MonitorType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get all monitors", nickname = "getAllMonitors", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasAnyPermission('VIEW_MONITORS', 'MODIFY_MONITORS')")
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<MonitorType> getAllMonitors() throws ServiceException
	{
		List<MonitorType> monitorTypes = new ArrayList<>();
		List<Monitor> monitors = monitorsService.getAllMonitors();
		for (Monitor monitor : monitors)
		{
			monitorTypes.add(mapper.map(monitor, MonitorType.class));
		}
		return monitorTypes;
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get monitor by id", nickname = "getMonitorById", code = 200, httpMethod = "GET", response = Monitor.class)
	@PreAuthorize("hasAnyPermission('VIEW_MONITORS', 'MODIFY_MONITORS')")
	@RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody MonitorType getMonitorById(@PathVariable(value = "id") long id) throws ServiceException
	{
		return mapper.map(monitorsService.getMonitorById(id), MonitorType.class);
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Get monitors count", nickname = "getMonitorsCount", code = 200, httpMethod = "GET", response = Integer.class)
	@PreAuthorize("hasAnyPermission('VIEW_MONITORS', 'MODIFY_MONITORS')")
	@RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Integer getMonitorsCount() throws ServiceException
	{
		return monitorsService.getMonitorsCount();
	}
}

