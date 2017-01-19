package com.qaprosoft.zafira.config;

/**
 * Simple configuration bean that represent CI configuration properties used to initialize Zafira test runs.
 * 
 * @author akhursevich
 */
public class CIConfig
{
	public enum BuildCasue { UPSTREAMTRIGGER, TIMERTRIGGER, MANUALTRIGGER };
	
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
		this.ciUrl = ciUrl;
	}

	public Integer getCiBuild()
	{
		return ciBuild;
	}

	public void setCiBuild(Integer ciBuild)
	{
		this.ciBuild = ciBuild;
	}

	public BuildCasue getCiBuildCause()
	{
		return ciBuildCause;
	}

	public void setCiBuildCause(String ciBuildCause)
	{
		this.ciBuildCause = BuildCasue.valueOf(ciBuildCause.toUpperCase());
	}

	public String getCiParentUrl()
	{
		return ciParentUrl;
	}

	public void setCiParentUrl(String ciParentUrl)
	{
		this.ciParentUrl = ciParentUrl;
	}

	public Integer getCiParentBuild()
	{
		return ciParentBuild;
	}

	public void setCiParentBuild(Integer ciParentBuild)
	{
		this.ciParentBuild = ciParentBuild;
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