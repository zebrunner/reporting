package com.qaprosoft.zafira.ws.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.qaprosoft.zafira.dbaccess.model.Dashboard;
import com.qaprosoft.zafira.dbaccess.model.Widget;
import com.qaprosoft.zafira.dbaccess.model.Dashboard.Type;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.DashboardService;
import com.qaprosoft.zafira.services.services.WidgetService;

@Controller
@RequestMapping("dashboards")
public class DashboardsController extends AbstractController
{
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private WidgetService widgetService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView index()
	{
		return new ModelAndView("dashboards/index");
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard createDashboard(@RequestBody @Valid Dashboard dashboard, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		dashboard.setType(Type.GENERAL);
		return dashboardService.createDashboard(dashboard);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Dashboard> getAllDashboards() throws ServiceException
	{
		return dashboardService.getAllDashboardsByType(Type.GENERAL);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard getDashboard(@PathVariable(value="id") long id) throws ServiceException
	{
		return dashboardService.getDashboardById(id);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteDashboard(@PathVariable(value="id") long id) throws ServiceException
	{
		dashboardService.deleteDashboardById(id);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard updateDashboard(@RequestBody Dashboard dashboard) throws ServiceException
	{
		return dashboardService.updateDashboard(dashboard);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget addDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.addDashboardWidget(dashboardId, widget);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{dashboardId}/widgets/{widgetId}", method = RequestMethod.DELETE)
	public void deleteDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @PathVariable(value="widgetId") long widgetId) throws ServiceException
	{
		dashboardService.deleteDashboardWidget(dashboardId, widgetId);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget updateDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.updateDashboardWidget(dashboardId, widget);
	}
}
