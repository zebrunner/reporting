package com.qaprosoft.zafira.ws.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.GroupService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@Api(value = "Groups operations")
@RequestMapping("groups")
@Secured({"ROLE_ADMIN"})
public class GroupController
{

	@Autowired
	private GroupService groupService;

	@ResponseStatusDetails
	@ApiOperation(value = "Create group", nickname = "createGroup", code = 200, httpMethod = "POST", notes = "Creates a new group.", response = Group.class, responseContainer = "Group")
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Group createGroup(@RequestBody Group group) throws ServiceException
	{
		return groupService.createGroup(group);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Group getGroup(@PathVariable(value = "id") long id) throws ServiceException
	{
		return groupService.getGroupById(id);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Group> getAllGroups() throws ServiceException
	{
		return groupService.getAllGroups();
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Integer getGroupsCount() throws ServiceException
	{
		return groupService.getGroupsCount();
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "roles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Group.Role> getRoles() throws ServiceException
	{
		return Arrays.asList(Group.Role.values());
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Group updateGroup(@RequestBody Group group) throws ServiceException
	{
		return groupService.updateGroup(group);
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteGroup(@PathVariable(value = "id") long id) throws ServiceException
	{
		groupService.deleteGroup(id);
	}
}