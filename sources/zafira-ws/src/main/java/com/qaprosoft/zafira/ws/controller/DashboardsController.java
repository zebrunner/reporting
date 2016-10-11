package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.dbaccess.model.Dashboard;
import com.qaprosoft.zafira.dbaccess.model.Dashboard.Type;
import com.qaprosoft.zafira.dbaccess.model.Widget;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.DashboardService;
import com.qaprosoft.zafira.services.services.WidgetService;
import com.qaprosoft.zafira.ws.annotations.DeleteResponse;
import com.qaprosoft.zafira.ws.annotations.GetResponse;
import com.qaprosoft.zafira.ws.annotations.PostResponse;
import com.qaprosoft.zafira.ws.annotations.UpdateResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Controller
@ApiIgnore
@Api(value = "dashboardsController", description = "Dashboards operations")
@RequestMapping("dashboards")
public class DashboardsController extends AbstractController
{
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private WidgetService widgetService;

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView index()
	{
		return new ModelAndView("dashboards/index");
	}

	@PostResponse
	@ApiOperation(value = "Create dashboard", nickname = "createDashboard", code = 200, httpMethod = "POST",
			notes = "create a new dashboard", response = Dashboard.class, responseContainer = "Dashboard")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard createDashboard(@RequestBody @Valid Dashboard dashboard, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		dashboard.setType(Type.GENERAL);
		return dashboardService.createDashboard(dashboard);
	}

	@GetResponse
	@ApiOperation(value = "Get all dashboards", nickname = "getAllDashboards", code = 200, httpMethod = "GET",
			notes = "return the matches dashboards", response = java.util.List.class, responseContainer = "Dashboard")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Dashboard> getAllDashboards(@ApiParam(value = "Type of the dashboard", required = true) @RequestParam(value="type", defaultValue="GENERAL", required=false) String type) throws ServiceException
	{
		return dashboardService.getAllDashboardsByType(Type.valueOf(type));
	}

	@GetResponse
	@ApiOperation(value = "Get dashboard by id", nickname = "getDashboard", code = 200, httpMethod = "GET",
			notes = "return dashboard by id", response = Dashboard.class, responseContainer = "Dashboard")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard getDashboard(@ApiParam(value = "Id of the dashboard", required = true) @PathVariable(value="id") long id) throws ServiceException
	{
		return dashboardService.getDashboardById(id);
	}

	@DeleteResponse
	@ApiOperation(value = "Delete dashboard by id", nickname = "deleteDashboard", code = 200, httpMethod = "DELETE",
			notes = "delete dashboard by id", response = Dashboard.class, responseContainer = "Dashboard")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteDashboard(@ApiParam(value = "Id of the dashboard", required = true) @PathVariable(value="id") long id) throws ServiceException
	{
		dashboardService.deleteDashboardById(id);
	}

	@UpdateResponse
	@ApiOperation(value = "Update dashboard", nickname = "updateDashboard", code = 200, httpMethod = "UPDATE",
			notes = "update dashboard", response = Dashboard.class, responseContainer = "Dashboard")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard updateDashboard(@RequestBody Dashboard dashboard) throws ServiceException
	{
		return dashboardService.updateDashboard(dashboard);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget addDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.addDashboardWidget(dashboardId, widget);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{dashboardId}/widgets/{widgetId}", method = RequestMethod.DELETE)
	public void deleteDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @PathVariable(value="widgetId") long widgetId) throws ServiceException
	{
		dashboardService.deleteDashboardWidget(dashboardId, widgetId);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget updateDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.updateDashboardWidget(dashboardId, widget);
	}
}
