package com.qaprosoft.zafira.ws.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.models.db.Dashboard.Type;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.models.dto.DashboardEmailType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.DashboardService;
import com.qaprosoft.zafira.services.services.EmailService;
import com.qaprosoft.zafira.services.services.SeleniumService;
import com.qaprosoft.zafira.services.services.emails.DashboardEmail;

import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("dashboards")
public class DashboardsController extends AbstractController
{
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private SeleniumService seleniumService;
	
	@Autowired
	private EmailService emailService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView index() throws IOException, InterruptedException, ServiceException
	{
		return new ModelAndView("dashboards/index");
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard createDashboard(@RequestBody @Valid Dashboard dashboard, @RequestHeader(value="Project", required=false) String project) throws ServiceException, IOException, InterruptedException
	{
		return dashboardService.createDashboard(dashboard);
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Dashboard> getAllDashboards(@ApiParam(value = "Type of the dashboard", required = false) @RequestParam(value="type", required=false) String type, @ApiParam(value = "User id", required = false) @RequestParam(value="userId", required=false) Long userId) throws ServiceException
	{
		List<Dashboard> dashboards = new ArrayList<>();
		if(StringUtils.isEmpty(type))
		{
			dashboards.addAll(dashboardService.getAllDashboardsByType(Type.GENERAL));
		}
		else
		{
			dashboards = dashboardService.getAllDashboardsByType(Type.valueOf(type));
		}
		
		if(isAdmin() || userId == getPrincipalId())
		{
			dashboards.addAll(dashboardService.getAllDashboardsByType(Type.USER_PERFORMANCE));
		}
		
		return dashboards;
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Dashboard getDashboard(@ApiParam(value = "Id of the dashboard", required = true) @PathVariable(value="id") long id) throws ServiceException
	{
		return dashboardService.getDashboardById(id);
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteDashboard(@ApiParam(value = "Id of the dashboard", required = true) @PathVariable(value="id") long id) throws ServiceException
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
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="email", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String sendDashboardByEmail(@RequestHeader(name="Authorization", required=false) String auth, @RequestBody @Valid DashboardEmailType email) throws ServiceException, JAXBException
	{
		List<Attachment> attachments = seleniumService.captureScreenshoots(email.getUrls(), 
															 email.getHostname(), 
															 auth != null ? auth : RequestContextHolder.currentRequestAttributes().getSessionId(),
															 By.id("dashboard_content"),
															 By.id("dashboard_title"));
		if(attachments.size() == 0)
		{
			throw new ServiceException("Unable to create dashboard screenshots");
		}
		
		return emailService.sendEmail(new DashboardEmail(email.getSubject(), email.getText(), attachments), email.getRecipients().trim().replaceAll(",", " ").replaceAll(";", " ").split(" "));
	}
	
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{dashboardId}/attributes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> createDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Attribute attribute)
	{
		dashboardService.createDashboardAttribute(dashboardId, attribute);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{dashboardId}/attributes", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> updateDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @RequestBody Attribute attribute)
	{
		dashboardService.updateAttribute(attribute);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{dashboardId}/attributes/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Attribute> deleteDashboardAttribute(@PathVariable(value="dashboardId") long dashboardId, @PathVariable(value="id") long id)
	{
		dashboardService.deleteDashboardAttributeById(id);
		return dashboardService.getAttributesByDashboardId(dashboardId);
	}
}
