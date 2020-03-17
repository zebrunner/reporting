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
package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Project;
import com.zebrunner.reporting.domain.dto.ProjectDTO;
import com.zebrunner.reporting.service.project.ProjectService;
import com.zebrunner.reporting.web.documented.ProjectDocumentedController;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RequestMapping(path = "api/projects", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ProjectController extends AbstractController implements ProjectDocumentedController {

    private final Mapper mapper;

    private final ProjectService projectService;

    public ProjectController(Mapper mapper, ProjectService projectService) {
        this.mapper = mapper;
        this.projectService = projectService;
    }

    @PreAuthorize("hasPermission('MODIFY_PROJECTS')")
    @PostMapping()
    @Override
    public ProjectDTO createProject(@RequestBody @Valid ProjectDTO project) {
        Project newProject = projectService.createProject(mapper.map(project, Project.class));
        return mapper.map(newProject, ProjectDTO.class);
    }

    @PreAuthorize("hasPermission('MODIFY_PROJECTS')")
    @DeleteMapping("/{id}")
    @Override
    public void deleteProject(@PathVariable("id") long id, @RequestParam(name = "reassignTo", required = false) Long reassignToId) {
        projectService.deleteProjectById(id, reassignToId);
    }

    @PreAuthorize("hasPermission('MODIFY_PROJECTS')")
    @PutMapping()
    @Override
    public ProjectDTO updateProject(@RequestBody @Valid ProjectDTO project) {
        Project updatedProject = projectService.updateProject(mapper.map(project, Project.class));
        return mapper.map(updatedProject, ProjectDTO.class);
    }

    @GetMapping()
    @Override
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return projects.stream()
                       .map(project -> mapper.map(project, ProjectDTO.class))
                       .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    @Override
    public ProjectDTO getProjectByName(@PathVariable("name") String name) {
        return mapper.map(projectService.getNotNullProjectByName(name), ProjectDTO.class);
    }

}
