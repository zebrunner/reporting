package com.qaprosoft.zafira.ws.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@Controller
@Api(value = "Groups API")
@CrossOrigin
@RequestMapping("api/groups")
public class GroupsAPIController extends AbstractController {

    @Autowired
    private GroupService groupService;

    @ResponseStatusDetails
    @ApiOperation(value = "Create group", nickname = "createGroup", code = 200, httpMethod = "POST", response = Group.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Group createGroup(@RequestBody Group group) throws ServiceException
    {
        return groupService.createGroup(group);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get group", nickname = "getGroup", code = 200, httpMethod = "GET", response = Group.class)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Group getGroup(@PathVariable(value = "id") long id) throws ServiceException
    {
        return groupService.getGroupById(id);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all groups", nickname = "getAllGroups", code = 200, httpMethod = "GET", response = List.class)
    @RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Group> getAllGroups() throws ServiceException
    {
        return groupService.getAllGroups();
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get groups count", nickname = "getGroupsCount", code = 200, httpMethod = "GET", response = Integer.class)
    @RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Integer getGroupsCount() throws ServiceException
    {
        return groupService.getGroupsCount();
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get roles", nickname = "getRoles", code = 200, httpMethod = "GET", response = List.class)
    @RequestMapping(value = "roles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Group.Role> getRoles() throws ServiceException
    {
        return Arrays.asList(Group.Role.values());
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update group", nickname = "updateGroup", code = 200, httpMethod = "PUT", response = Group.class)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Group updateGroup(@RequestBody Group group) throws ServiceException
    {
        return groupService.updateGroup(group);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete group", nickname = "deleteGroup", code = 200, httpMethod = "DELETE")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void deleteGroup(@PathVariable(value = "id") long id) throws ServiceException
    {
        groupService.deleteGroup(id);
    }
}
