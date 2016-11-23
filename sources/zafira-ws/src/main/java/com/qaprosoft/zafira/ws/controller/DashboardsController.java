package com.qaprosoft.zafira.ws.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
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

import com.qaprosoft.zafira.dbaccess.model.Dashboard;
import com.qaprosoft.zafira.dbaccess.model.Dashboard.Type;
import com.qaprosoft.zafira.dbaccess.model.Widget;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.DashboardService;
import com.qaprosoft.zafira.services.services.EmailService;
import com.qaprosoft.zafira.services.services.SeleniumService;
import com.qaprosoft.zafira.services.services.emails.DashboardEmail;
import com.qaprosoft.zafira.ws.dto.EmailType;

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
	public @ResponseBody List<Dashboard> getAllDashboards(@ApiParam(value = "Type of the dashboard", required = true) @RequestParam(value="type", required=false) String type) throws ServiceException
	{
		List<Dashboard> dashboards = new ArrayList<>();
		if(StringUtils.isEmpty(type))
		{
			dashboards.addAll(dashboardService.getAllDashboardsByType(Type.GENERAL));
			if(isAdmin())
			{
				dashboards.addAll(dashboardService.getAllDashboardsByType(Type.USER_PERFORMANCE));
			}
		}
		else
		{
			dashboards = dashboardService.getAllDashboardsByType(Type.valueOf(type));
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
	public @ResponseBody String sendDashboardByEmail(@RequestBody @Valid EmailType email) throws ServiceException, JAXBException
	{
		File attachment = seleniumService.captureScreenshoot(email.getData().get("href"), email.getData().get("hostname"), RequestContextHolder.currentRequestAttributes().getSessionId());
		if(attachment == null)
		{
			throw new ServiceException("Unable to create dashboard screenshot");
		}
		return emailService.sendEmail(new DashboardEmail(email.getSubject(), email.getText(), attachment), email.getRecipients().trim().replaceAll(",", " ").replaceAll(";", " ").split(" "));
	}
}
