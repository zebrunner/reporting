package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.models.db.Group;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Api("Groups API")
public interface GroupDocumentedController {

    @ApiOperation(
            value = "Creates group and attaches provided permissions",
            notes = "Returns created group with created permissions inside",
            nickname = "createGroup",
            httpMethod = "POST",
            response = Group.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "group", paramType = "body", dataType = "Group", required = true, value = "Group to create"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created group with created permissions inside", response = Group.class)
    })
    Group createGroup(Group group);

    @ApiOperation(
            value = "Adds permissions to group",
            notes = "Returns group with attached permissions inside",
            nickname = "addPermissionsToGroup",
            httpMethod = "POST",
            response = Group.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "group", paramType = "body", dataType = "Group", required = true, value = "Group with permissions to add inside"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns group with attached attributes inside", response = Group.class)
    })
    Group addPermissionsToGroup(Group group);

    @ApiOperation(
            value = "Retrieves group by id",
            notes = "Returns found group",
            nickname = "getGroup",
            httpMethod = "GET",
            response = Group.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Group id"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found group", response = Group.class),
            @ApiResponse(code = 404, message = "Indicates that group does not exist", response = ResponseEntity.class)
    })
    Group getGroup(long id);

    @ApiOperation(
            value = "Retrieves all groups",
            notes = "Returns found groups",
            nickname = "getAllGroups",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "isPublic", paramType = "query", dataType = "boolean", value = "Flag to indicate to retrieve public groups only"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found groups", response = List.class)
    })
    List<Group> getAllGroups(boolean isPublic);

    @ApiOperation(
            value = "Gets groups count",
            notes = "Returns count of available groups",
            nickname = "getGroupsCount",
            httpMethod = "GET",
            response = Integer.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns count of available groups", response = Integer.class)
    })
    Integer getGroupsCount();

    @ApiOperation(
            value = "Retrieves all roles which an application contains",
            notes = "Returns all roles",
            nickname = "getRoles",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all roles", response = List.class)
    })
    List<Group.Role> getRoles();

    @ApiOperation(
            value = "Updates group properties and group permissions",
            notes = "Returns updated group and attached permissions inside",
            nickname = "updateGroup",
            httpMethod = "PUT",
            response = Group.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "group", paramType = "body", dataType = "Group", required = true, value = "Group to update"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated group and attached permissions inside", response = Group.class)
    })
    Group updateGroup(Group group);

    @ApiOperation(
            value = "Deletes group",
            notes = "Deletes group by id and detaches all permissions from group",
            nickname = "deleteGroup",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Group id to delete"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created dashboard"),
            @ApiResponse(code = 400, message = "Indicates that group contains users. It required to delete each user before delete operation", response = ResponseEntity.class)
    })
    void deleteGroup(long id);

}
