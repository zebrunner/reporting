package com.qaprosoft.zafira.ws.controller;

import java.util.List;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.qaprosoft.zafira.models.db.View;
import com.qaprosoft.zafira.models.dto.ViewType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.ViewService;

import io.swagger.annotations.Api;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@Api(value = "Views operations")
@RequestMapping("views")
public class ViewsController extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private ViewService viewService;
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView openJobViewsIndexPage()
	{
		return new ModelAndView("views/index");
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody View getViewById(@PathVariable(value="id") long id) throws ServiceException
	{
		return viewService.getViewById(id);
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<View> getAllViews(@RequestParam(value="projectId", required=false) Long projectId) throws ServiceException
	{
		return viewService.getAllViews(projectId);
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({"ROLE_ADMIN"})
	public @ResponseBody ViewType createView(@RequestBody @Valid ViewType view) throws ServiceException
	{
		return mapper.map(viewService.createView(mapper.map(view, View.class)), ViewType.class);
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({"ROLE_ADMIN"})
	public @ResponseBody ViewType updateView(@RequestBody @Valid ViewType view) throws ServiceException
	{
		return mapper.map(viewService.updateView(mapper.map(view, View.class)), ViewType.class);
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	@Secured({"ROLE_ADMIN"})
	public void deleteView(@PathVariable(value="id") long id) throws ServiceException
	{
		viewService.deleteViewById(id);
	}
}