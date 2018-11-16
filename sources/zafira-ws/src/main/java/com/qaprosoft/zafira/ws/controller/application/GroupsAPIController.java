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
package com.qaprosoft.zafira.ws.controller.application;

import java.util.List;

import com.qaprosoft.zafira.services.exceptions.EntityNotExistsException;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.qaprosoft.zafira.services.services.application.GroupService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Groups API")
@CrossOrigin
@RequestMapping("api/groups")
public class GroupsAPIController extends AbstractController {

    @Autowired
    private GroupService groupService;

    @ResponseStatusDetails
    @ApiOperation(value = "Create group", nickname = "createGroup", httpMethod = "POST", response = Group.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Group createGroup(@RequestBody Group group) throws ServiceException
    {
        return groupService.createGroup(group);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Add permissions to group", nickname = "addPermissionsToGroup", httpMethod = "POST", response = Group.class)
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "permissions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Group addPermissionsToGroup(@RequestBody Group group) throws ServiceException
    {
        return groupService.addPermissionsToGroup(group);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get group", nickname = "getGroup", httpMethod = "GET", response = Group.class)
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Group getGroup(@PathVariable(value = "id") long id) throws ServiceException
    {
        return groupService.getGroupById(id);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get all groups", nickname = "getAllGroups", httpMethod = "GET", response = List.class)
    @RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    public @ResponseBody List<Group> getAllGroups() throws ServiceException
    {
        return groupService.getAllGroups();
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get groups count", nickname = "getGroupsCount", httpMethod = "GET", response = Integer.class)
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Integer getGroupsCount() throws ServiceException
    {
        return groupService.getGroupsCount();
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get roles", nickname = "getRoles", httpMethod = "GET", response = List.class)
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "roles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Group.Role> getRoles() throws ServiceException
    {
        return GroupService.getRoles();
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Update group", nickname = "updateGroup", httpMethod = "PUT", response = Group.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Group updateGroup(@RequestBody Group group) throws ServiceException
    {
        return groupService.updateGroup(group);
    }

    @ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Delete group", nickname = "deleteGroup", httpMethod = "DELETE")
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void deleteGroup(@PathVariable(value = "id") long id) throws ServiceException
    {
        Group group = groupService.getGroupById(id);
        if(group == null) {
            throw new EntityNotExistsException(Group.class, false);
        }
        if(group.getUsers().size() > 0) {
            throw new ForbiddenOperationException("It's necessary to clear the group initially.", true);
        }
        groupService.deleteGroup(id);
    }
}
