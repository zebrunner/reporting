package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

import java.util.List;

public class TestSearchCriteria extends SearchCriteria
{
	private List<Long> testRunIds;
	private Long testCaseId;

	public List<Long> getTestRunIds()
	{
		return testRunIds;
	}

	public void setTestRunIds(List<Long> testRunIds)
	{
		this.testRunIds = testRunIds;
	}

	public Long getTestCaseId()
	{
		return testCaseId;
	}

	public void setTestCaseId(Long testCaseId)
	{
		this.testCaseId = testCaseId;
	}
}
