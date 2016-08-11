package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

import org.apache.commons.lang3.StringUtils;

public class SearchCriteria
{
	public enum SortOrder {ASC, DESC};
	
	// Pages are zero-based
	private Integer page = 1;
	// The very default page size, just not to get NPE'd
	private Integer pageSize = 25;
	private String project;
	
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

	public String getProject()
	{
		return project;
	}

	public void setProject(String project)
	{
		if(!StringUtils.isEmpty(project))
		{
			this.project = project;
		}
	}
}
