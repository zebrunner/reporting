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
package com.qaprosoft.zafira.ws.controller.management;

import com.qaprosoft.zafira.models.db.application.Group;
import com.qaprosoft.zafira.models.dto.management.GroupType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.management.MngGroupService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Api(value = "Groups management API")
@CrossOrigin
@RequestMapping("api/mng/groups")
public class MngGroupsAPIController extends AbstractController {

    @Autowired
    private MngGroupService mngGroupService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Create group", nickname = "createGroup", code = 200, httpMethod = "POST", response = GroupType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GroupType createGroup(@Valid @RequestBody GroupType group) throws ServiceException {
        return mapper.map(mngGroupService.createGroup(mapper.map(group, Group.class)), GroupType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Add permissions to group", nickname = "addPermissionsToGroup", code = 200, httpMethod = "POST", response = GroupType.class)
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "permissions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GroupType addPermissionsToGroup(@Valid @RequestBody GroupType group) throws ServiceException {
        return mapper.map(mngGroupService.addPermissionsToGroup(mapper.map(group, Group.class)), GroupType.class);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get group", nickname = "getGroup", code = 200, httpMethod = "GET", response = GroupType.class)
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GroupType getGroup(@PathVariable(value = "id") long id) throws ServiceException {
        return mapper.map(mngGroupService.getGroupById(id), GroupType.class);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get all groups", nickname = "getAllGroups", code = 200, httpMethod = "GET", response = List.class)
    @RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    public @ResponseBody List<GroupType> getAllGroups() throws ServiceException {
        return mngGroupService.getAllGroups().stream().map(group -> mapper.map(group, GroupType.class)).collect(Collectors.toList());
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get groups count", nickname = "getGroupsCount", code = 200, httpMethod = "GET", response = Integer.class)
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Integer getGroupsCount() throws ServiceException {
        return mngGroupService.getGroupsCount();
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get roles", nickname = "getRoles", code = 200, httpMethod = "GET", response = List.class)
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "roles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Group.Role> getRoles() throws ServiceException {
        return Arrays.asList(Group.Role.values());
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Update group", nickname = "updateGroup", code = 200, httpMethod = "PUT", response = GroupType.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GroupType updateGroup(@Valid @RequestBody GroupType group) throws ServiceException {
        return mapper.map(mngGroupService.updateGroup(mapper.map(group, Group.class)), GroupType.class);
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Delete group", nickname = "deleteGroup", code = 200, httpMethod = "DELETE")
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void deleteGroup(@PathVariable(value = "id") long id) throws ServiceException {
        mngGroupService.deleteGroup(id);
    }
}
