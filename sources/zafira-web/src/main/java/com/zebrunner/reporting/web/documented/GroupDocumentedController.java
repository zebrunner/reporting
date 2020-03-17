package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.db.Group;
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
            value = "Creates a group and grants it specified permissions",
            notes = "Returns the created group with granted permissions",
            nickname = "createGroup",
            httpMethod = "POST",
            response = Group.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "group", paramType = "body", dataType = "Group", required = true, value = "The group to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created group with granted permissions", response = Group.class)
    })
    Group createGroup(Group group);

    @ApiOperation(
            value = "Adds permissions to a group",
            notes = "Returns the group with granted permissions",
            nickname = "addPermissionsToGroup",
            httpMethod = "POST",
            response = Group.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "group", paramType = "body", dataType = "Group", required = true, value = "The group with permissions to add")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the group with granted permissions", response = Group.class)
    })
    Group addPermissionsToGroup(Group group);

    @ApiOperation(
            value = "Retrieves a group by its id",
            notes = "Returns the found group",
            nickname = "getGroup",
            httpMethod = "GET",
            response = Group.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The group id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found group", response = Group.class),
            @ApiResponse(code = 404, message = "Indicates that the group does not exist", response = ResponseEntity.class)
    })
    Group getGroup(long id);

    @ApiOperation(
            value = "Retrieves all groups",
            notes = "Returns all found groups",
            nickname = "getAllGroups",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "isPublic", paramType = "query", dataType = "boolean", value = "A flag to retrieve public groups only")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all found groups", response = List.class)
    })
    List<Group> getAllGroups(boolean isPublic);

    @ApiOperation(
            value = "Retrieves the count of groups",
            notes = "Returns the count of available groups",
            nickname = "getGroupsCount",
            httpMethod = "GET",
            response = Integer.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the count of available groups", response = Integer.class)
    })
    Integer getGroupsCount();

    @ApiOperation(
            value = "Retrieves all roles in the application",
            notes = "Returns all roles given to users in the application",
            nickname = "getRoles",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all roles", response = List.class)
    })
    List<Group.Role> getRoles();

    @ApiOperation(
            value = "Updates group properties and group permissions",
            notes = "Returns the updated group with granted permissions",
            nickname = "updateGroup",
            httpMethod = "PUT",
            response = Group.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "group", paramType = "body", dataType = "Group", required = true, value = "The group to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated group with granted permissions", response = Group.class)
    })
    Group updateGroup(Group group);

    @ApiOperation(
            value = "Deletes a group",
            notes = "Deletes a group by its id and removes all permissions from the group",
            nickname = "deleteGroup",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The id of the group to delete")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The dashboard was deleted successfully"),
            @ApiResponse(code = 400, message = "Indicates that the group contains users. It is required to remove all users before the delete operation", response = ResponseEntity.class)
    })
    void deleteGroup(long id);

}
