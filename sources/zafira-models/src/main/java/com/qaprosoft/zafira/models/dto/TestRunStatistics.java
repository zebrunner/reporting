package com.qaprosoft.zafira.models.dto;

public class TestRunStatistics
{
	public enum Action
	{
		MARK_AS_KNOWN_ISSUE, REMOVE_KNOWN_ISSUE,  MARK_AS_BLOCKER, REMOVE_BLOCKER, MARK_AS_PASSED, MARK_AS_REVIEWED, MARK_AS_NOT_REVIEWED;
	}

	private long testRunId;
	private int passed;
	private int failed;
	private int failedAsKnown;
	private int failedAsBlocker;
	private int skipped;
	private int inProgress;
	private boolean reviewed;

	public long getTestRunId()
	{
		return testRunId;
	}

	public void setTestRunId(long testRunId)
	{
		this.testRunId = testRunId;
	}

	public int getPassed()
	{
		return passed;
	}

	public void setPassed(int passed)
	{
		this.passed = passed;
	}

	public int getFailed()
	{
		return failed;
	}

	public void setFailed(int failed)
	{
		this.failed = failed;
	}

	public int getFailedAsKnown()
	{
		return failedAsKnown;
	}

	public void setFailedAsKnown(int failedAsKnown)
	{
		this.failedAsKnown = failedAsKnown;
	}

	public int getFailedAsBlocker()
	{
		return failedAsBlocker;
	}

	public void setFailedAsBlocker(int failedAsBlocker)
	{
		this.failedAsBlocker = failedAsBlocker;
	}

	public int getSkipped()
	{
		return skipped;
	}

	public void setSkipped(int skipped)
	{
		this.skipped = skipped;
	}

	public int getInProgress()
	{
		return inProgress;
	}

	public void setInProgress(int inProgress)
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
