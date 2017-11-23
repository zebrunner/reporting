package com.qaprosoft.zafira.models.dto;

public class TestRunStatistics
{
	public enum Action
	{
		MARK_AS_KNOWN_ISSUE, REMOVE_KNOWN_ISSUE,  MARK_AS_BLOCKER, REMOVE_BLOCKER, MARK_AS_PASSED, MARK_AS_REVIEWED, MARK_AS_NOT_REVIEWED;
	}

	private Long testRunId;
	private Integer passed;
	private Integer failed;
	private Integer failedAsKnown;
	private Integer failedAsBlocker;
	private Integer skipped;
	private Integer inProgress;
	private boolean reviewed;

	public Long getTestRunId()
	{
		return testRunId;
	}

	public void setTestRunId(Long testRunId)
	{
		this.testRunId = testRunId;
	}

	public Integer getPassed()
	{
		return passed;
	}

	public void setPassed(Integer passed)
	{
		this.passed = passed;
	}

	public Integer getFailed()
	{
		return failed;
	}

	public void setFailed(Integer failed)
	{
		this.failed = failed;
	}

	public Integer getFailedAsKnown()
	{
		return failedAsKnown;
	}

	public void setFailedAsKnown(Integer failedAsKnown)
	{
		this.failedAsKnown = failedAsKnown;
	}

	public Integer getFailedAsBlocker()
	{
		return failedAsBlocker;
	}

	public void setFailedAsBlocker(Integer failedAsBlocker)
	{
		this.failedAsBlocker = failedAsBlocker;
	}

	public Integer getSkipped()
	{
		return skipped;
	}

	public void setSkipped(Integer skipped)
	{
		this.skipped = skipped;
	}

	public Integer getInProgress()
	{
		return inProgress;
	}

	public void setInProgress(Integer inProgress)
	{
		this.inProgress = inProgress;
	}

	public boolean isReviewed()
	{
		return reviewed;
	}

	public void setReviewed(boolean reviewed)
	{
		this.reviewed = reviewed;
	}
}
