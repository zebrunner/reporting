/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.models.dto.ProjectDTO;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@Api("Projects API")
public interface ProjectDocumentedController {

    @ApiOperation(
            value = "Creates project",
            notes = "Returns created project",
            nickname = "createProject",
            httpMethod = "POST",
            response = ProjectDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "project", paramType = "body", dataType = "ProjectDTO", required = true, value = "Project to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns created project", response = ProjectDTO.class)
    })
    ProjectDTO createProject(ProjectDTO project);

    @ApiOperation(
            value = "Deletes project by id",
            notes = "Reassign all artifacts to provided project id for reassign or to default project if project id for reassign id null",
            nickname = "deleteProject",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "Project id"),
            @ApiImplicitParam(name = "reassignToId", paramType = "query", dataType = "number", value = "Project id to reassign all artifacts on delete action")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Project was deleted successfully"),
            @ApiResponse(code = 404, message = "Indicates that default project does not exist", response = ErrorResponse.class)
    })
    void deleteProject(long id, Long reassignToId);

    @ApiOperation(
            value = "Updates project",
            notes = "Returns updated project",
            nickname = "updateProject",
            httpMethod = "PUT",
            response = ProjectDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "project", paramType = "body", dataType = "ProjectDTO", required = true, value = "Project to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated project", response = ProjectDTO.class)
    })
    ProjectDTO updateProject(ProjectDTO project);

    @ApiOperation(
            value = "Retrieves all projects",
            notes = "Returns found projects",
            nickname = "getAllProjects",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found projects", response = List.class)
    })
    List<ProjectDTO> getAllProjects();

    @ApiOperation(
            value = "Retrieve project by name",
            notes = "Returns found project",
            nickname = "getProjectByName",
            httpMethod = "GET",
            response = ProjectDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "name", paramType = "path", dataType = "string", required = true, value = "Project name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found project", response = ProjectDTO.class),
            @ApiResponse(code = 404, message = "Indicates that project does not exist", response = ErrorResponse.class)
    })
    ProjectDTO getProjectByName(String name);

}
