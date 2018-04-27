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
	private int queued;
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

	public int getQueued()
	{
		return queued;
	}

	public void setQueued(int queued)
	{
		this.queued = queued;
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
