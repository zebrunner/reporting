package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import com.qaprosoft.zafira.dbaccess.model.Project;

public interface ProjectMapper
{
	void createProject(Project project);

	Project getProjectById(long id);
	
	Project getProjectByName(String name);

	List<Project> getAllProjects();
	
	void updateProject(Project project);

	void deleteProjectById(long id);
}
