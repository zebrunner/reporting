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
package com.qaprosoft.zafira.dbaccess.dao.mysql.application.search;

import com.qaprosoft.zafira.models.db.application.Project;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class SearchCriteria
{
	public enum SortOrder {ASC, DESC};
	
	// Pages are zero-based
	private Integer page = 1;
	// The very default page size, just not to get NPE'd
	private Integer pageSize = 20;
	private List<Project> projects;
	
	private SortOrder sortOrder = SortOrder.ASC;

	public Integer getPage()
	{
		return page;
	}

	public void setPage(Integer page)
	{
		this.page = page;
	}

	public Integer getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(Integer pageSize)
	{
		this.pageSize = pageSize;
	}

	public Integer getOffset()
	{
		return (page - 1) * pageSize;
	}

	public SortOrder getSortOrder()
	{
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder)
	{
		this.sortOrder = sortOrder;
	}

	public List<Project> getProjects()
	{
		return projects;
	}

	public void setProjects(List<Project> projects)
	{
		if(!CollectionUtils.isEmpty(projects))
		{
			this.projects = projects;
		}
	}
}
