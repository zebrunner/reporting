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

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import com.qaprosoft.zafira.ws.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.models.db.Permission;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.DashboardService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Dashboards API")
@CrossOrigin
@RequestMapping("api/dashboards")
public class DashboardsAPIController extends AbstractController
{

	@Autowired
	private DashboardService dashboardService;

    @ResponseStatusDetails
    @ApiOperation(value = "Create dashboard", nickname = "createDashboard", httpMethod = "POST", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard createDashboard(@RequestBody @Valid Dashboard dashboard) throws ServiceException, IOException, InterruptedException
	{
		return dashboardService.createDashboard(dashboard);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Get dashboards", nickname = "getAllDashboards", httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Dashboard> getAllDashboards(@RequestParam(value="hidden", required=false) boolean hidden) throws ServiceException
	{
		List<Dashboard> dashboards;
		if(!hidden && hasPermission(Permission.Name.VIEW_HIDDEN_DASHBOARDS))
		{
			dashboards = (dashboardService.getAllDashboards());
		}
		else
		{
			dashboards = dashboardService.getDashboardsByHidden(false);
		}

		return dashboards;
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Get dashboard by ID", nickname = "getDashboardById", httpMethod = "GET", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard getDashboardById(@PathVariable(value="id") long id) throws ServiceException
	{
		return dashboardService.getDashboardById(id);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get dashboard by title", nickname = "getDashboardByTitle", httpMethod = "GET", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="title", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard getDashboardByTitle(@RequestParam(value="title", required=false) String title) throws ServiceException
	{
		return dashboardService.getDashboardByTitle(title);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Delete dashboard", nickname = "deleteDashboard", httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteDashboard(@PathVariable(value="id") long id) throws ServiceException
	{
		dashboardService.deleteDashboardById(id);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Update dashboard", nickname = "updateDashboard", httpMethod = "PUT", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard updateDashboard(@RequestBody Dashboard dashboard) throws ServiceException
	{
		return dashboardService.updateDashboard(dashboard);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Add dashboard widget", nickname = "addDashboardWidget", httpMethod = "POST", response = Widget.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget addDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.addDashboardWidget(dashboardId, widget);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Delete dashboard widget", nickname = "deleteDashboardWidget", httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(value="{dashboardId}/widgets/{widgetId}", method = RequestMethod.DELETE)
	public void deleteDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @PathVariable(value="widgetId") long widgetId) throws ServiceException
	{
		dashboardService.deleteDashboardWidget(dashboardId, widgetId);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Update dashboard widget", nickname = "updateDashboardWidget", httpMethod = "PUT", response = Widget.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget updateDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.updateDashboardWidget(dashboardId, widget);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update dashboard widget", nickname = "updateDashboardWidget", httpMethod = "PUT", response = Widget.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(value="{dashboardId}/widgets/all", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Widget> updateDashboardWidgets(@PathVariable(value="dashboardId") long dashboardId, @RequestBody List<Widget> widgets) throws ServiceException
	{
		for(Widget widget : widgets)
		{
			dashboardService.updateDashboardWidget(dashboardId, widget);
		}
		return widgets;
	}

	@ResponseStatusDetails
    @ApiOperation(value = "Create dashboard attribute", nickname = "createDashboardAttribute", httpMethod = "POST", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(value="{dashboardId}/attributes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> createDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Attribute attribute)
	{
		dashboardService.createDashboardAttribute(dashboardId, attribute);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Update dashboard attribute", nickname = "createDashboardAttribute", httpMethod = "PUT", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(value="{dashboardId}/attributes", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> updateDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Attribute attribute)
	{
		dashboardService.updateAttribute(attribute);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Delete dashboard attribute", nickname = "createDashboardAttribute", httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(value="{dashboardId}/attributes/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> deleteDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @PathVariable(value="id") long id)
	{
		dashboardService.deleteDashboardAttributeById(id);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
}
