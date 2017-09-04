package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Arrays;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class Setting extends AbstractEntity
{
	private static final long serialVersionUID = -6809215085336377266L;

	private static final List<String> TO_ENCRYPT = Arrays.asList("JIRA_PASSWORD", "JENKINS_API_TOKEN_OR_PASSWORD");

	private String name;
	private String value;
	private boolean isEncrypted;
	private Tool tool;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public boolean isEncrypted()
	{
		return isEncrypted;
	}

	public void setEncrypted(boolean encrypted)
	{
		isEncrypted = encrypted;
	}

	public Tool getTool()
	{
		return tool;
	}

	public void setTool(Tool tool)
	{
		this.tool = tool;
	}

	public enum SettingType
	{
		STF_NOTIFICATION_RECIPIENTS,
		JIRA_URL, JIRA_USER, JIRA_PASSWORD, JIRA_CLOSED_STATUS, JIRA_ENABLED,
		JENKINS_URL, JENKINS_USER, JENKINS_API_TOKEN_OR_PASSWORD, JENKINS_ENABLED,
		SLACK_WEB_HOOK_URL, SLACK_NOTIF_CHANNEL_EXAMPLE,
		EMAIL_HOST, EMAIL_PORT, EMAIL_USER, EMAIL_PASSWORD, EMAIL_ENABLED,
		CRYPTO_KEY_TYPE, CRYPTO_ALGORITHM, CRYPTO_KEY_SIZE, KEY
	}

	public enum Tool
	{
		JIRA, JENKINS, SLACK, EMAIL, LDAP, CRYPTO
	}

	public boolean isValueForEncrypting()
	{
		return TO_ENCRYPT.contains(this.getName());
	}
}