package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.GroupService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Controller
@Api(value = "Groups operations")
@RequestMapping("groups")
public class GroupController {
    
    @Autowired
    private GroupService groupService;

    @ResponseStatusDetails
    @ApiOperation(value = "Create group", nickname = "createGroup", code = 200, httpMethod = "POST",
            notes = "Creates a new group.", response = Group.class, responseContainer = "Group")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Group createGroup(@RequestBody Group group) throws ServiceException
    {
        return groupService.createGroup(group);
    }

    @ApiIgnore
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Group getGroup(@PathVariable(value="id") long id) throws ServiceException
    {
        return groupService.getGroupById(id);
    }

    @ApiIgnore
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Group> getAllGroups() throws ServiceException
    {
        return groupService.getAllGroups();
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
    @RequestMapping(value="{id}", method = RequestMethod.DELETE)
    public void deleteGroup(@PathVariable(value="id") long id) throws ServiceException
    {
        groupService.deleteGroup(id);
    }
}
