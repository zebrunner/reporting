package com.qaprosoft.zafira.ws.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.models.dto.DashboardEmailType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.DashboardService;
import com.qaprosoft.zafira.services.services.EmailService;
import com.qaprosoft.zafira.services.services.SeleniumService;
import com.qaprosoft.zafira.services.services.auth.JWTService;
import com.qaprosoft.zafira.services.services.emails.DashboardEmail;
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
	
	@Autowired
	private SeleniumService seleniumService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private JWTService jwtService;

    @ResponseStatusDetails
    @ApiOperation(value = "Create dashboard", nickname = "createDashboard", code = 200, httpMethod = "POST", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
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
		if(hidden)
		{
			dashboards = dashboardService.getDashboardsByHidden(false);
		}
		else
		{
            dashboards = (dashboardService.getAllDashboards());
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
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteDashboard(@PathVariable(value="id") long id) throws ServiceException
	{
		dashboardService.deleteDashboardById(id);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Update dashboard", nickname = "updateDashboard", code = 200, httpMethod = "PUT", response = Dashboard.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard updateDashboard(@RequestBody Dashboard dashboard) throws ServiceException
	{
		return dashboardService.updateDashboard(dashboard);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Add dashboard widget", nickname = "addDashboardWidget", code = 200, httpMethod = "POST", response = Widget.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget addDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.addDashboardWidget(dashboardId, widget);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Delete dashboard widget", nickname = "deleteDashboardWidget", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="{dashboardId}/widgets/{widgetId}", method = RequestMethod.DELETE)
	public void deleteDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @PathVariable(value="widgetId") long widgetId) throws ServiceException
	{
		dashboardService.deleteDashboardWidget(dashboardId, widgetId);
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Update dashboard widget", nickname = "updateDashboardWidget", code = 200, httpMethod = "PUT", response = Widget.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="{dashboardId}/widgets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget updateDashboardWidget(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Widget widget) throws ServiceException
	{
		return dashboardService.updateDashboardWidget(dashboardId, widget);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update dashboard widget", nickname = "updateDashboardWidget", code = 200, httpMethod = "PUT", response = Widget.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
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
	public @ResponseBody String sendDashboardByEmail(@RequestHeader(name="Access-Token", required=true) String accessToken, @RequestBody @Valid DashboardEmailType email) throws ServiceException, JAXBException
	{
	    	User user = jwtService.parseRefreshToken(accessToken);
	    	if(user == null)
	    	{
	    		throw new BadCredentialsException("Invalid access token");
	    	}
    	
		Dimension dimension = null;
		if(!StringUtils.isEmpty(email.getDimension()))
		{
			String [] dimensions = email.getDimension().toLowerCase().split("x");
			dimension = new Dimension(Integer.valueOf(dimensions[0]), Integer.valueOf(dimensions[1]));
		}
		List<Attachment> attachments = seleniumService.captureScreenshoots(email.getUrls(), 
															 email.getHostname(), 
															 accessToken,
															 By.id("content"),
															 By.id("dashboard_title"), dimension);
		if(attachments.size() == 0)
		{
			throw new ServiceException("Unable to create dashboard screenshots");
		}
		
		return emailService.sendEmail(new DashboardEmail(email.getSubject(), email.getText(), attachments), email.getRecipients().trim().replaceAll(",", " ").replaceAll(";", " ").split(" "));
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Create dashboard attribute", nickname = "createDashboardAttribute", code = 200, httpMethod = "POST", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value="{dashboardId}/attributes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> createDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Attribute attribute)
	{
		dashboardService.createDashboardAttribute(dashboardId, attribute);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Update dashboard attribute", nickname = "createDashboardAttribute", code = 200, httpMethod = "PUT", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
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
	@RequestMapping(value="{dashboardId}/attributes/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> deleteDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @PathVariable(value="id") long id)
	{
		dashboardService.deleteDashboardAttributeById(id);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
}
