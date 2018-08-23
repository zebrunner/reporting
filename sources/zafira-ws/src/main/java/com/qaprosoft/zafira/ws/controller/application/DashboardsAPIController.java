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
import java.util.concurrent.*;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import com.qaprosoft.zafira.ws.controller.AbstractController;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.application.Attachment;
import com.qaprosoft.zafira.models.db.application.Attribute;
import com.qaprosoft.zafira.models.db.application.Dashboard;
import com.qaprosoft.zafira.models.db.application.Permission;
import com.qaprosoft.zafira.models.db.application.User;
import com.qaprosoft.zafira.models.db.application.Widget;
import com.qaprosoft.zafira.models.dto.application.DashboardEmailType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.DashboardService;
import com.qaprosoft.zafira.services.services.application.EmailService;
import com.qaprosoft.zafira.services.services.application.SeleniumService;
import com.qaprosoft.zafira.services.services.auth.JWTService;
import com.qaprosoft.zafira.services.services.application.emails.DashboardEmail;
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

	private static final Logger LOGGER = Logger.getLogger(DashboardsAPIController.class);

	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private SeleniumService seleniumService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private JWTService jwtService;

    @ResponseStatusDetails
    @ApiOperation(value = "Create dashboard", nickname = "createDashboard", code = 200, httpMethod = "POST", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard createDashboard(@RequestBody @Valid Dashboard dashboard) throws ServiceException, IOException, InterruptedException
	{
		return dashboardService.createDashboard(dashboard);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Get dashboards", nickname = "getAllDashboards", code = 200, httpMethod = "GET", response = List.class)
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
    @ApiOperation(value = "Get dashboard by ID", nickname = "getDashboardById", code = 200, httpMethod = "GET", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard getDashboardById(@PathVariable(value="id") long id) throws ServiceException
	{
		return dashboardService.getDashboardById(id);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get dashboard by title", nickname = "getDashboardByTitle", code = 200, httpMethod = "GET", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="title", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard getDashboardByTitle(@RequestParam(value="title", required=false) String title) throws ServiceException
	{
		return dashboardService.getDashboardByTitle(title);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Delete dashboard", nickname = "deleteDashboard", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteDashboard(@PathVariable(value="id") long id) throws ServiceException
	{
		dashboardService.deleteDashboardById(id);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Update dashboard", nickname = "updateDashboard", code = 200, httpMethod = "PUT", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard updateDashboard(@RequestBody Dashboard dashboard) throws ServiceException
	{
		return dashboardService.updateDashboard(dashboard);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Add dashboard widget", nickname = "addDashboardWidget", code = 200, httpMethod = "POST", response = Widget.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget addDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.addDashboardWidget(dashboardId, widget);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Delete dashboard widget", nickname = "deleteDashboardWidget", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(value="{dashboardId}/widgets/{widgetId}", method = RequestMethod.DELETE)
	public void deleteDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @PathVariable(value="widgetId") long widgetId) throws ServiceException
	{
		dashboardService.deleteDashboardWidget(dashboardId, widgetId);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Update dashboard widget", nickname = "updateDashboardWidget", code = 200, httpMethod = "PUT", response = Widget.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget updateDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.updateDashboardWidget(dashboardId, widget);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update dashboard widget", nickname = "updateDashboardWidget", code = 200, httpMethod = "PUT", response = Widget.class)
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
    @ApiOperation(value = "Send dashboard by email", nickname = "sendDashboardByEmail", code = 200, httpMethod = "POST")
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Access-Token", paramType = "header") })
	@RequestMapping(value="email", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String sendDashboardByEmail(@RequestHeader(name="Access-Token", required=true) String accessToken,
			@RequestParam(value = "projects", required = false, defaultValue = "") String projects,
			@RequestBody @Valid DashboardEmailType email)
			throws ServiceException, JAXBException, InterruptedException, ExecutionException
	{
		User user = jwtService.parseRefreshToken(accessToken);
		if(user == null)
		{
			throw new BadCredentialsException("Invalid access token");
		}

		String[] dimensions = new String[2];
		if(!StringUtils.isEmpty(email.getDimension()))
		{
			dimensions = email.getDimension().toLowerCase().split("x");
		}
		Dimension dimension = !StringUtils.isEmpty(email.getDimension()) ? new Dimension(Integer.valueOf(dimensions[0]), Integer.valueOf(dimensions[1])) : null;

		new Thread(() -> {
			try
			{
				List<Attachment> attachments = seleniumService.captureScreenshoots(email.getUrls(),
						email.getHostname(),
						accessToken,
						projects,
						By.id("dashboard_content"),
						By.id("dashboard_title"),
						dimension,
						By.id("main-fab"), By.id("header"));
				if(attachments.size() == 0)
				{
					throw new ServiceException("Unable to create dashboard screenshots");
				}

				emailService.sendEmail(new DashboardEmail(email.getSubject(), email.getText(), attachments), email.getRecipients().trim().replaceAll(",", " ").replaceAll(";", " ").split(" "));
			} catch (ServiceException e)
			{
				LOGGER.error(e);
			}
		}).start();
		return null;
	}

	@ResponseStatusDetails
    @ApiOperation(value = "Create dashboard attribute", nickname = "createDashboardAttribute", code = 200, httpMethod = "POST", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(value="{dashboardId}/attributes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> createDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Attribute attribute)
	{
		dashboardService.createDashboardAttribute(dashboardId, attribute);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Update dashboard attribute", nickname = "createDashboardAttribute", code = 200, httpMethod = "PUT", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
	@RequestMapping(value="{dashboardId}/attributes", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> updateDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Attribute attribute)
	{
		dashboardService.updateAttribute(attribute);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Delete dashboard attribute", nickname = "createDashboardAttribute", code = 200, httpMethod = "DELETE")
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
