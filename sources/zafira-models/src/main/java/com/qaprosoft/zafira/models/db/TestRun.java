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
package com.qaprosoft.zafira.models.db;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.db.config.Configuration;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.util.StringUtils;

@JsonInclude(Include.NON_NULL)
public class TestRun extends AbstractEntity
{
	private static final long serialVersionUID = -1847933012610222160L;
	private static final String NAME = "%s %s (%s) on %s %s";
	private Map<String, String> configuration = new HashMap<>();

	public enum Initiator
	{
		SCHEDULER, UPSTREAM_JOB, HUMAN
	}

	public enum DriverMode
	{
		METHOD_MODE, CLASS_MODE, SUITE_MODE
	}

	private String ciRunId;
	private User user;
	private TestSuite testSuite;
	private Status status;
	private String scmURL;
	private String scmBranch;
	private String scmCommit;
	@JsonIgnore
	private String configXML;
	private WorkItem workItem;
	private Job job;
	private Integer buildNumber;
	private Job upstreamJob;
	private Integer upstreamJobBuildNumber;
	private Initiator startedBy;
	private Project project;
	private boolean knownIssue;
	private boolean blocker;
	private String env;
	private String platform;
	private String appVersion;
	private Date startedAt;
	private Integer elapsed;
	private Integer eta;
	private String comments;
	private DriverMode driverMode;
	private TestConfig config;

	private Integer passed;
	private Integer failed;
	private Integer failedAsKnown;
	private Integer failedAsBlocker;
	private Integer skipped;
	private Integer inProgress;
	private Integer aborted;
	private Integer queued;
	private boolean reviewed;

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public TestSuite getTestSuite()
	{
		return testSuite;
	}

	public void setTestSuite(TestSuite testSuite)
	{
		this.testSuite = testSuite;
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

	public String getName(Configuration configuration) {
		for (Argument arg : configuration.getArg())
		{
			this.configuration.put(arg.getKey(), arg.getValue());
		}
		String appVersion = argumentIsPresent("app_version")? this.configuration.get("app_version") + " - ": "";
		String platformInfo = buildPlatformInfo();
		String testRunName = String.format(NAME, appVersion, testSuite.getName(), testSuite.getFileName(),
				this.configuration.get("env"), platformInfo).trim();
		if(StringUtils.isEmpty(this.configuration.get("env"))){
			testRunName = testRunName.split("on")[0];
		}
		return testRunName;
	}

	private boolean argumentIsPresent(String arg, String... ignoreValues) {
		if(configuration.get(arg) == null || "".equals(configuration.get(arg)) || configuration.get(arg).equalsIgnoreCase("null")) {
			return false;
		}
		for(String ignoreValue: ignoreValues) {
			if(configuration.get(arg).equals(ignoreValue)) {
				return false;
			}
		}
		return true;
	}

	private String buildPlatformInfo() {
		String platformInfo = "%s %s %s";
		String mobilePlatformVersion = argumentIsPresent("mobile_platform_name")? configuration.get("mobile_platform_name"): "";
		String browser = argumentIsPresent("browser")? configuration.get("browser"): "";
		String locale = argumentIsPresent("locale", "en_US", "en", "US")? configuration.get("locale"): "";
		platformInfo = String.format(platformInfo, mobilePlatformVersion, browser, locale);
		platformInfo = platformInfo.trim();
		while(platformInfo.contains("  ")) {
			platformInfo = platformInfo.replaceFirst("  ", " ");
		}
		platformInfo = "(" + platformInfo + ")";
		if(!platformInfo.equals("()"))
			return platformInfo;
		else
			return "";
	}

	public WorkItem getWorkItem()
	{
		return workItem;
	}

	public void setWorkItem(WorkItem workItem)
	{
		this.workItem = workItem;
	}

	public Job getJob()
	{
		return job;
	}

	public void setJob(Job job)
	{
		this.job = job;
	}

	public Integer getBuildNumber()
	{
		return buildNumber;
	}

	public void setBuildNumber(Integer buildNumber)
	{
		this.buildNumber = buildNumber;
	}

	public Job getUpstreamJob()
	{
		return upstreamJob;
	}

	public void setUpstreamJob(Job upstreamJob)
	{
		this.upstreamJob = upstreamJob;
	}

	public Integer getUpstreamJobBuildNumber()
	{
		return upstreamJobBuildNumber;
	}

	public void setUpstreamJobBuildNumber(Integer upstreamJobBuildNumber)
	{
		this.upstreamJobBuildNumber = upstreamJobBuildNumber;
	}

	public Initiator getStartedBy()
	{
		return startedBy;
	}

	public void setStartedBy(Initiator startedBy)
	{
		this.startedBy = startedBy;
	}

	public String getCiRunId()
	{
		return ciRunId;
	}

	public void setCiRunId(String ciRunId)
	{
		this.ciRunId = ciRunId;
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

	public Integer getFailedAsBlocker() {
		return failedAsBlocker;
	}

	public void setFailedAsBlocker(Integer failedAsBlocker) {
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

	public Integer getInProgress() {
		return inProgress;
	}

	public void setInProgress(Integer inProgress) {
		this.inProgress = inProgress;
	}

	public Integer getAborted()
	{
		return aborted;
	}

	public void setAborted(Integer aborted)
	{
		this.aborted = aborted;
	}

	public Integer getQueued()
	{
		return queued;
	}

	public void setQueued(Integer queued)
	{
		this.queued = queued;
	}

	public Project getProject()
	{
		return project;
	}

	public void setProject(Project project)
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

	public boolean isBlocker() {
		return blocker;
	}

	public void setBlocker(boolean blocker) {
		this.blocker = blocker;
	}

	public String getEnv()
	{
		return env;
	}

	public void setEnv(String env)
	{
		this.env = env;
	}

	public String getPlatform()
	{
		return platform;
	}

	public void setPlatform(String platform)
	{
		this.platform = platform;
	}

	public Date getStartedAt()
	{
		return startedAt;
	}

	public void setStartedAt(Date startedAt)
	{
		this.startedAt = startedAt;
	}

	public Integer getElapsed()
	{
		return elapsed;
	}

	public void setElapsed(Integer elapsed)
	{
		this.elapsed = elapsed;
	}

	public Integer getEta()
	{
		return eta;
	}

	public void setEta(Integer eta)
	{
		this.eta = eta;
	}

	public String getComments()
	{
		return comments;
	}

	public void setComments(String comments)
	{
		this.comments = comments;
	}

	public String getAppVersion()
	{
		return appVersion;
	}

	public void setAppVersion(String appVersion)
	{
		this.appVersion = appVersion;
	}

	public DriverMode getDriverMode()
	{
		return driverMode;
	}

	public void setDriverMode(DriverMode driverMode)
	{
		this.driverMode = driverMode;
	}

	public TestConfig getConfig()
	{
		return config;
	}

	public void setConfig(TestConfig config)
	{
		this.config = config;
	}

	public Integer getCountdown()
	{
		Integer countdown = null;
		if(Status.IN_PROGRESS.equals(this.status) && this.startedAt != null && this.eta != null)
		{
			LocalDateTime from = new LocalDateTime(this.startedAt.getTime());
			LocalDateTime to = new LocalDateTime(Calendar.getInstance().getTime());
			countdown = Math.max(0, this.eta - Seconds.secondsBetween(from, to).getSeconds());
		}
		return countdown;
	}
}
