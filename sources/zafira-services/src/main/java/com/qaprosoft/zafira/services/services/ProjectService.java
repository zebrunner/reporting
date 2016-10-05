package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.ProjectMapper;
import com.qaprosoft.zafira.dbaccess.model.Project;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class ProjectService
{
	@Autowired
	private ProjectMapper projectMapper;

	@Transactional(rollbackFor = Exception.class)
	public Project createProject(Project project) throws ServiceException
	{
		projectMapper.createProject(project);
		return project;
	}
	
	@Transactional(readOnly = true)
	public List<Project> getAllProjects() throws ServiceException
	{
		return projectMapper.getAllProjects();
	}
	
	@Transactional(readOnly = true)
	@Cacheable("projects")
	public Project getProjectByName(String name) throws ServiceException
	{
		return !StringUtils.isEmpty(name) ? projectMapper.getProjectByName(name) : null;
	}

	@Transactional(rollbackFor = Exception.class)
	public Project updateProject(Project project) throws ServiceException
	{
		projectMapper.updateProject(project);
		return project;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteProjectById(Long id) throws ServiceException
	{
		projectMapper.deleteProjectById(id);
	}
}
