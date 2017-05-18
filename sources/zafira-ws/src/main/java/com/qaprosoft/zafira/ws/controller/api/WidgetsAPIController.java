package com.qaprosoft.zafira.ws.controller.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.dbaccess.utils.SQLAdapter;
import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.WidgetService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("api/widgets")
public class WidgetsAPIController extends AbstractController
{
	@Autowired
	private WidgetService widgetService;
	
	@ResponseStatusDetails
    @ApiOperation(value = "Create widget", nickname = "createWidget", code = 200, httpMethod = "POST", response = Widget.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget createWidget(@RequestBody @Valid Widget widget, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		return widgetService.createWidget(widget);
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Get widget", nickname = "getWidget", code = 200, httpMethod = "GET", response = Widget.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget getWidget(@PathVariable(value="id") long id) throws ServiceException
	{
		return widgetService.getWidgetById(id);
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Delete widget", nickname = "deleteWidget", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteWidget(@PathVariable(value="id") long id) throws ServiceException
	{
		widgetService.deleteWidgetById(id);
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Update widget", nickname = "updateWidget", code = 200, httpMethod = "PUT", response = Widget.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget updateWidget(@RequestBody Widget widget) throws ServiceException
	{
		return widgetService.updateWidget(widget);
	}

	@ResponseStatusDetails
    @ApiOperation(value = "Execute SQL", nickname = "executeSQL", code = 200, httpMethod = "POST", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="sql", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, Object>> executeSQL(@RequestBody @Valid SQLAdapter sql, @RequestParam(value="project", defaultValue="", required=false) String project, @RequestParam(value="currentUserId", required=false) String currentUserId, @RequestParam(value="dashboardName", required=false) String dashboardName) throws ServiceException
	{
		String query = sql.getSql()
				.replaceAll("#\\{project\\}", !StringUtils.isEmpty(project) ? project : "")
				.replaceAll("#\\{dashboardName\\}", !StringUtils.isEmpty(dashboardName) ? dashboardName : "")
				.replaceAll("#\\{currentUserId\\}", !StringUtils.isEmpty(currentUserId) ? currentUserId : String.valueOf(getPrincipalId()))
				.replaceAll("#\\{currentUserName\\}", String.valueOf(getPrincipalName()));
		
		if(sql.getAttributes() != null)
		{
			for(Attribute attribute : sql.getAttributes())
			{
				query = query.replaceAll("#\\{" + attribute.getKey() + "\\}", attribute.getValue());
			}
		}
		
		return widgetService.executeSQL(query);
	}
	
	@ResponseStatusDetails
    @ApiOperation(value = "Get all widgets", nickname = "getAllWidgets", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Widget> getAllWidgets() throws ServiceException
	{
		return widgetService.getAllWidgets();
	}
}