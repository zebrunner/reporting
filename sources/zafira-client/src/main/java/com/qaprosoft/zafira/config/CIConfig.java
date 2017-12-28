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
package com.qaprosoft.zafira.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple configuration bean that represent CI configuration properties used to initialize Zafira test runs.
 * 
 * @author akhursevich
 */
public class CIConfig
{
	public enum BuildCasue { UPSTREAMTRIGGER, TIMERTRIGGER, MANUALTRIGGER, SCMTRIGGER };
	
	private String ciRunId;
	private String ciUrl;
	private Integer ciBuild;
	private BuildCasue ciBuildCause;
	private String ciParentUrl;
	private Integer ciParentBuild;
	private String ciUserId;
	private String ciUserFirstName;
	private String ciUserLastName;
	private String ciUserEmail;
	
	private String gitBranch;
	private String gitCommit;
	private String gitUrl;

	public String getCiRunId()
	{
		return ciRunId;
	}

	public void setCiRunId(String ciRunId)
	{
		this.ciRunId = ciRunId;
	}

	public String getCiUrl()
	{
		return ciUrl;
	}

	public void setCiUrl(String ciUrl)
	{
		this.ciUrl = StringUtils.removeEnd(ciUrl, "/");
	}

	public Integer getCiBuild()
	{
		return ciBuild;
	}

	public void setCiBuild(String ciBuild)
	{
		this.ciBuild = StringUtils.isEmpty(ciBuild) ? 0 : Integer.valueOf(ciBuild);
	}

	public BuildCasue getCiBuildCause()
	{
		return ciBuildCause;
	}

	public void setCiBuildCause(String ciBuildCause)
	{
		if(ciBuildCause != null)
		{
			// HotFix for 'BuildCasue.UPSTREAMTRIGGER,UPSTREAMTRIGGER,UPSTREAMTRIGGER'
			this.ciBuildCause = BuildCasue.valueOf(ciBuildCause.toUpperCase().split(",")[0]);
		}
	}

	public String getCiParentUrl()
	{
		return ciParentUrl;
	}

	public void setCiParentUrl(String ciParentUrl)
	{
		this.ciParentUrl = StringUtils.removeEnd(ciParentUrl, "/");;
	}

	public Integer getCiParentBuild()
	{
		return ciParentBuild;
	}

	public void setCiParentBuild(String ciParentBuild)
	{
		this.ciParentBuild = StringUtils.isEmpty(ciParentBuild) ? 0 : Integer.valueOf(ciParentBuild);
	}

	public String getCiUserId()
	{
		return ciUserId;
	}

	public void setCiUserId(String ciUserId)
	{
		this.ciUserId = ciUserId;
	}

	public String getCiUserFirstName()
	{
		return ciUserFirstName;
	}

	public void setCiUserFirstName(String ciUserFirstName)
	{
		this.ciUserFirstName = ciUserFirstName;
	}

	public String getCiUserLastName()
	{
		return ciUserLastName;
	}

	public void setCiUserLastName(String ciUserLastName)
	{
		this.ciUserLastName = ciUserLastName;
	}

	public String getCiUserEmail()
	{
		return ciUserEmail;
	}

	public void setCiUserEmail(String ciUserEmail)
	{
		this.ciUserEmail = ciUserEmail;
	}

	public String getGitBranch()
	{
		return gitBranch;
	}

	public void setGitBranch(String gitBranch)
	{
		this.gitBranch = gitBranch;
	}

	public String getGitCommit()
	{
		return gitCommit;
	}

	public void setGitCommit(String gitCommit)
	{
		this.gitCommit = gitCommit;
	}

	public String getGitUrl()
	{
		return gitUrl;
	}

	public void setGitUrl(String gitUrl)
	{
		this.gitUrl = gitUrl;
	}
}