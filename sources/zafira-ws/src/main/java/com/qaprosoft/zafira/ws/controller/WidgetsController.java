package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.dbaccess.model.Widget;
import com.qaprosoft.zafira.dbaccess.utils.SQLAdapter;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.WidgetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@ApiIgnore
@RequestMapping("widgets")
public class WidgetsController extends AbstractController
{
	@Autowired
	private WidgetService widgetService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget createWidget(@RequestBody @Valid Widget widget, @RequestHeader(value="Project", required=false) String project) throws ServiceException
	{
		return widgetService.createWidget(widget);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget getWidget(@PathVariable(value="id") long id) throws ServiceException
	{
		return widgetService.getWidgetById(id);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteWidget(@PathVariable(value="id") long id) throws ServiceException
	{
		widgetService.deleteWidgetById(id);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Widget updateWidget(@RequestBody Widget widget) throws ServiceException
	{
		return widgetService.updateWidget(widget);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="sql", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, Object>> executeSQL(@RequestBody @Valid SQLAdapter sql, @RequestParam(value="project", defaultValue="", required=false) String project, @RequestParam(value="currentUserId", required=false) String currentUserId) throws ServiceException
	{
		return widgetService.executeSQL(sql.getSql()
				.replaceAll("#\\{project\\}", !StringUtils.isEmpty(project) ? project : "")
				.replaceAll("#\\{currentUserId\\}", !StringUtils.isEmpty(currentUserId) ? currentUserId : String.valueOf(getPrincipalId()))
				.replaceAll("#\\{currentUserName\\}", String.valueOf(getPrincipalName())));
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Widget> getAllWidgets() throws ServiceException
	{
		return widgetService.getAllWidgets();
	}
}
