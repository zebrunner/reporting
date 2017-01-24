package com.qaprosoft.zafira.ws.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.ProjectService;

@Controller
@RequestMapping("projects")
public class ProjectsController extends AbstractController
{
	@Autowired
	private ProjectService projectService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Project createProject(@RequestBody @Valid Project project) throws ServiceException
	{
		return projectService.createProject(project);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteProject(@PathVariable(value="id") long id) throws ServiceException
	{
		projectService.deleteProjectById(id);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Project updateProject(@RequestBody Project project) throws ServiceException
	{
		return projectService.updateProject(project);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Project> getAllProjects() throws ServiceException
	{
		return projectService.getAllProjects();
	}
}
