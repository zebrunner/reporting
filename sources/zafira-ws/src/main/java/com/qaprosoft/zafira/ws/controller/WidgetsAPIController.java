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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import com.qaprosoft.zafira.models.db.application.Attachment;
import com.qaprosoft.zafira.models.db.application.User;
import com.qaprosoft.zafira.models.dto.DashboardEmailType;
import com.qaprosoft.zafira.services.services.application.EmailService;
import com.qaprosoft.zafira.services.services.application.SeleniumService;
import com.qaprosoft.zafira.services.services.auth.JWTService;
import com.qaprosoft.zafira.services.services.application.emails.DashboardEmail;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.dbaccess.utils.SQLAdapter;
import com.qaprosoft.zafira.models.db.application.Attribute;
import com.qaprosoft.zafira.models.db.application.Widget;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.WidgetService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("api/widgets")
public class WidgetsAPIController extends AbstractController
{

	private static final Logger LOGGER = Logger.getLogger(WidgetsAPIController.class);

	@Value("${zafira.webservice.url}")
	private String wsURL;

	@Autowired
	private WidgetService widgetService;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private JWTService jwtService;

	@Autowired
	private SeleniumService seleniumService;

	@Autowired
	private EmailService emailService;

	@ResponseStatusDetails
	@ApiOperation(value = "Create widget", nickname = "createWidget", code = 200, httpMethod = "POST", response = Widget.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget createWidget(@RequestBody @Valid Widget widget,
			@RequestHeader(value = "Project", required = false) String project) throws ServiceException
	{
		return widgetService.createWidget(widget);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get widget", nickname = "getWidget", code = 200, httpMethod = "GET", response = Widget.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget getWidget(@PathVariable(value = "id") long id) throws ServiceException
	{
		return widgetService.getWidgetById(id);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete widget", nickname = "deleteWidget", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteWidget(@PathVariable(value = "id") long id) throws ServiceException
	{
		widgetService.deleteWidgetById(id);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update widget", nickname = "updateWidget", code = 200, httpMethod = "PUT", response = Widget.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_WIDGETS')")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget updateWidget(@RequestBody Widget widget) throws ServiceException
	{
		return widgetService.updateWidget(widget);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Execute SQL", nickname = "executeSQL", code = 200, httpMethod = "POST", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "sql", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, Object>> executeSQL(@RequestBody @Valid SQLAdapter sql,
			@RequestParam(value = "projects", defaultValue = "", required = false) List<String> projects,
			@RequestParam(value = "currentUserId", required = false) String currentUserId,
			@RequestParam(value = "dashboardName", required = false) String dashboardName,
            @RequestParam(value = "stackTraceRequired", required = false) boolean stackTraceRequired ) throws ServiceException
	{
		String query = sql.getSql();
		List<Map<String, Object>> resultList;
		try {
		if (sql.getAttributes() != null)
		{
			for (Attribute attribute : sql.getAttributes())
			{
				query = query.replaceAll("#\\{" + attribute.getKey() + "\\}", attribute.getValue());
			}
		}

			query = query
				.replaceAll("#\\{project\\}", formatProjects(projects))
				.replaceAll("#\\{dashboardName\\}", !StringUtils.isEmpty(dashboardName) ? dashboardName : "")
				.replaceAll("#\\{currentUserId\\}", !StringUtils.isEmpty(currentUserId) ? currentUserId : String.valueOf(getPrincipalId()))
				.replaceAll("#\\{currentUserName\\}", String.valueOf(getPrincipalName()))
				.replaceAll("#\\{zafiraURL\\}", StringUtils.removeEnd(wsURL, "-ws"))
				.replaceAll("#\\{jenkinsURL\\}", settingsService.getSettingByName("JENKINS_URL").getValue())
				.replaceAll("#\\{hashcode\\}", "0")
				.replaceAll("#\\{testCaseId\\}", "0");

            resultList = widgetService.executeSQL(query);
        }
        catch (Exception e) {
            if (stackTraceRequired) {
                resultList = new ArrayList<>();
                Map<String, Object> exceptionMap = new HashMap<>();
                exceptionMap.put("Check your query", ExceptionUtils.getFullStackTrace(e));
                resultList.add(exceptionMap);
                return resultList;
            }
            else {
                throw e;
            }
        }
        return resultList;
    }
	
	private String formatProjects(List<String> projects)
	{
		String result = "%";
		if(!CollectionUtils.isEmpty(projects))
		{
			StringBuilder sb = new StringBuilder();
			for(String project : projects)
			{
				sb.append(project + ","); 
			}
			result = StringUtils.removeEnd(sb.toString(), ",");
		}
		return result;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get all widgets", nickname = "getAllWidgets", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Widget> getAllWidgets() throws ServiceException
	{
		return widgetService.getAllWidgets();
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Send widget by email", nickname = "sendWidgetByEmail", code = 200, httpMethod = "POST")
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Access-Token", paramType = "header") })
	@RequestMapping(value="email", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String sendWidgetByEmail(@RequestHeader(name="Access-Token", required=true) String accessToken,
			@RequestParam(value = "projects", required = false, defaultValue = "") String projects,
			@RequestParam(value = "widgetId", required = false, defaultValue = "") Long widgetId,
			@RequestBody @Valid DashboardEmailType email) throws ServiceException, JAXBException
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
						By.id("widget-container-" + widgetId),
						By.id("widget-title-" + widgetId),
						dimension);
				if(attachments.size() == 0)
				{
					throw new ServiceException("Unable to create widget screenshots");
				}
				emailService.sendEmail(new DashboardEmail(email.getSubject(), email.getText(), attachments), email.getRecipients().trim().replaceAll(",", " ").replaceAll(";", " ").split(" "));
			} catch (ServiceException e)
			{
				LOGGER.error(e);
			}
		}).start();
		return null;
	}
}