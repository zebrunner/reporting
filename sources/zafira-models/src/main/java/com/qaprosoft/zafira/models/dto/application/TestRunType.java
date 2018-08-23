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
package com.qaprosoft.zafira.models.dto.application;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.application.Status;
import com.qaprosoft.zafira.models.db.application.TestRun.DriverMode;
import com.qaprosoft.zafira.models.db.application.TestRun.Initiator;
import com.qaprosoft.zafira.models.dto.AbstractType;

@JsonInclude(Include.NON_NULL)
public class TestRunType extends AbstractType
{
	private static final long serialVersionUID = -1687311347861782118L;
	private String ciRunId;
	@NotNull
	private Long testSuiteId;
	private Status status;
	private String scmURL;
	private String scmBranch;
	private String scmCommit;
	private String configXML;
	@NotNull
	private Long jobId;
	private Long upstreamJobId;
	private Integer upstreamJobBuildNumber;
	@NotNull
	private Integer buildNumber;
	@NotNull
	private Initiator startedBy;
	private Long userId;
	private String workItem;
	private ProjectType project;
	private boolean knownIssue;
	private boolean blocker;
	private DriverMode driverMode;
	
	private boolean reviewed;

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	public TestRunType() {
		
	}
	
	public TestRunType(String ciRunId, Long testSuiteId, Long userId, String scmURL, String scmBranch, String scmCommit,
			String configXML, Long jobId, Integer buildNumber, Initiator startedBy, String workItem)
	{
		this.ciRunId = ciRunId;
		this.testSuiteId = testSuiteId;
		this.userId = userId;
		this.scmURL = scmURL;
		this.scmBranch = scmBranch;
		this.scmCommit = scmCommit;
		this.configXML = configXML;
		this.jobId = jobId;
		this.buildNumber = buildNumber;
		this.startedBy = startedBy;
		this.workItem = workItem;
	}
	
	public TestRunType(String ciRunId, Long testSuiteId, String scmURL, String scmBranch, String scmCommit,
			String configXML, Long jobId, Long upstreamJobId, Integer upstreamJobBuildNumber, Integer buildNumber,
			Initiator startedBy, String workItem)
	{
		this.ciRunId = ciRunId;
		this.testSuiteId = testSuiteId;
		this.scmURL = scmURL;
		this.scmBranch = scmBranch;
		this.scmCommit = scmCommit;
		this.configXML = configXML;
		this.jobId = jobId;
		this.upstreamJobId = upstreamJobId;
		this.upstreamJobBuildNumber = upstreamJobBuildNumber;
		this.buildNumber = buildNumber;
		this.startedBy = startedBy;
		this.workItem = workItem;
	}
	
	public TestRunType(String ciRunId, Long testSuiteId, String scmURL, String scmBranch, String scmCommit,
			String configXML, Long jobId, Integer buildNumber, Initiator startedBy, String workItem)
	{
		this.ciRunId = ciRunId;
		this.testSuiteId = testSuiteId;
		this.scmURL = scmURL;
		this.scmBranch = scmBranch;
		this.scmCommit = scmCommit;
		this.configXML = configXML;
		this.jobId = jobId;
		this.buildNumber = buildNumber;
		this.startedBy = startedBy;
		this.workItem = workItem;
	}
	
	public String getCiRunId()
	{
		return ciRunId;
	}

	public void setCiRunId(String ciRunId)
	{
		this.ciRunId = ciRunId;
	}

	public Long getTestSuiteId()
	{
		return testSuiteId;
	}

	public void setTestSuiteId(Long testSuiteId)
	{
		this.testSuiteId = testSuiteId;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public String getScmURL()
	{
		return scmURL;
	}

	public void setScmURL(String scmURL)
	{
		this.scmURL = scmURL;
	}

	public String getScmBranch()
	{
		return scmBranch;
	}

	public void setScmBranch(String scmBranch)
	{
		this.scmBranch = scmBranch;
	}

	public String getScmCommit()
	{
		return scmCommit;
	}

	public void setScmCommit(String scmCommit)
	{
		this.scmCommit = scmCommit;
	}

	public String getConfigXML()
	{
		return configXML;
	}

	public void setConfigXML(String configXML)
	{
		this.configXML = configXML;
	}

	public Long getJobId()
	{
		return jobId;
	}

	public void setJobId(Long jobId)
	{
		this.jobId = jobId;
	}

	public Integer getBuildNumber()
	{
		return buildNumber;
	}

	public void setBuildNumber(Integer buildNumber)
	{
		this.buildNumber = buildNumber;
	}

	public Initiator getStartedBy()
	{
		return startedBy;
	}

	public void setStartedBy(Initiator startedBy)
	{
		this.startedBy = startedBy;
	}

	public Long getUpstreamJobId()
	{
		return upstreamJobId;
	}

	public void setUpstreamJobId(Long upstreamJobId)
	{
		this.upstreamJobId = upstreamJobId;
	}

	public Integer getUpstreamJobBuildNumber()
	{
		return upstreamJobBuildNumber;
	}

	public void setUpstreamJobBuildNumber(Integer upstreamJobBuildNumber)
	{
		this.upstreamJobBuildNumber = upstreamJobBuildNumber;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public String getWorkItem()
	{
		return workItem;
	}

	public void setWorkItem(String workItem)
	{
		this.workItem = workItem;
	}

	public ProjectType getProject()
	{
		return project;
	}

	public void setProject(ProjectType project)
	{
		this.project = project;
	}

	public boolean isKnownIssue()
	{
		return knownIssue;
	}

	public void setKnownIssue(boolean knownIssue)
	{
		this.knownIssue = knownIssue;
	}

	public boolean isBlocker()
	{
		return blocker;
	}

	public void setBlocker(boolean blocker)
	{
		this.blocker = blocker;
	}

	public DriverMode getDriverMode()
	{
		return driverMode;
	}

	public void setDriverMode(DriverMode driverMode)
	{
		this.driverMode = driverMode;
	}
}