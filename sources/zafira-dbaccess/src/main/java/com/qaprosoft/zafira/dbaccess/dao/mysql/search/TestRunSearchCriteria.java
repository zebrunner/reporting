package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

public class TestRunSearchCriteria extends SearchCriteria
{
	private Long id;
	
	public TestRunSearchCriteria()
	{
		super.setSortOrder(SortOrder.DESC);
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}
}
