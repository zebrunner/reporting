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
package com.zebrunner.reporting.service.project;

import com.zebrunner.reporting.persistence.dao.mysql.application.ProjectMapper;
import com.zebrunner.reporting.persistence.repository.ProjectRepository;
import com.zebrunner.reporting.domain.db.Project;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    private static final String ERR_MSG_PROJECT_NOT_FOUND_BY_ID = "Requested company can not be found by id '%d'";
    private static final String ERR_MSG_PROJECT_NOT_FOUND_BY_NAME = "Requested company can not be found by name '%s'";

    private static final String DEFAULT_PROJECT = "UNKNOWN";

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final Map<String, ProjectReassignable> projectReassignables;

    public ProjectService(ProjectMapper projectMapper, ProjectRepository projectRepository, @Lazy Map<String, ProjectReassignable> projectReassignables) {
        this.projectMapper = projectMapper;
        this.projectRepository = projectRepository;
        this.projectReassignables = projectReassignables;
    }

    @CachePut(value = "projects", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #project.name", condition = "#project != null && #project.name != null")
    @Transactional(rollbackFor = Exception.class)
    public Project createProject(Project project) {
        projectMapper.createProject(project);
        return project;
    }

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectMapper.getAllProjects();
    }

    @Transactional(readOnly = true)
    public com.zebrunner.reporting.domain.entity.Project getProjectById(Long id) {
        return projectRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.PROJECT_NOT_FOUND, String.format(ERR_MSG_PROJECT_NOT_FOUND_BY_ID, id)));
    }

    @Transactional(readOnly = true)
    public com.zebrunner.reporting.domain.entity.Project getNotNullProjectByName(String name) {
        return projectRepository.findByName(name)
                                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.PROJECT_NOT_FOUND, String.format(ERR_MSG_PROJECT_NOT_FOUND_BY_NAME, name)));
    }

    @Cacheable(value = "projects", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #name", condition = "#name != null")
    @Transactional(readOnly = true)
    public Project getProjectByName(String name) {
        return !StringUtils.isEmpty(name) ? projectMapper.getProjectByName(name) : null;
    }

    @Cacheable(value = "projects", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #name", condition = "#name != null")
    @Transactional(readOnly = true)
    public Project getProjectByNameOrDefault(String name) {
        return !StringUtils.isEmpty(name) ? projectMapper.getProjectByName(name) : projectMapper.getProjectByName(DEFAULT_PROJECT);
    }

    @CachePut(value = "projects", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #project.name", condition = "#project != null && #project.name != null")
    @Transactional(rollbackFor = Exception.class)
    public Project updateProject(Project project) {
        projectMapper.updateProject(project);
        return project;
    }

    @CacheEvict(value = "projects", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectById(Long id, Long reassignToId) {
        com.zebrunner.reporting.domain.entity.Project fromProject = getProjectById(id);
        com.zebrunner.reporting.domain.entity.Project reassignToProject;
        if (reassignToId == null) {
            reassignToProject = getNotNullProjectByName(getDefaultProject());
        } else {
            reassignToProject = getProjectById(reassignToId);
        }
        projectReassignables.forEach((beanName, projectReassignable) -> projectReassignable.reassignProject(fromProject.getId(), reassignToProject.getId()));
        projectMapper.deleteProjectById(fromProject.getId());
    }

    public static String getDefaultProject() {
        return DEFAULT_PROJECT;
    }
}
