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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.AbstractEntity;

import java.util.Arrays;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class Setting extends AbstractEntity
{
	private static final long serialVersionUID = -6809215085336377266L;

	private static final List<String> TO_ENCRYPT = Arrays.asList("JIRA_PASSWORD", "JENKINS_API_TOKEN_OR_PASSWORD", "EMAIL_PASSWORD", "AMAZON_SECRET_KEY", "HIPCHAT_ACCESS_TOKEN", "LDAP_MANAGER_PASSWORD", "RABBITMQ_PASSWORD");

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
		GOOGLE_CLIENT_SECRET_ORIGIN, GOOGLE_ENABLED,
		JIRA_URL, JIRA_USER, JIRA_PASSWORD, JIRA_CLOSED_STATUS, JIRA_ENABLED,
		JENKINS_URL, JENKINS_USER, JENKINS_API_TOKEN_OR_PASSWORD, JENKINS_ENABLED,
		SLACK_WEB_HOOK_URL, SLACK_NOTIF_CHANNEL_EXAMPLE,
		EMAIL_HOST, EMAIL_PORT, EMAIL_USER, EMAIL_FROM_ADDRESS, EMAIL_PASSWORD, EMAIL_ENABLED,
		AMAZON_ACCESS_KEY, AMAZON_SECRET_KEY, AMAZON_REGION, AMAZON_BUCKET, AMAZON_DISTRIBUTION_DOMAIN, AMAZON_KEY_PAIR_ID, AMAZON_ENABLED,
		HIPCHAT_ACCESS_TOKEN, HIPCHAT_ENABLED,
		LDAP_DN, LDAP_SEARCH_FILTER, LDAP_URL, LDAP_MANAGER_USER, LDAP_MANAGER_PASSWORD, LDAP_ENABLED,
		CRYPTO_KEY_TYPE, CRYPTO_ALGORITHM, CRYPTO_KEY_SIZE, KEY, 
		RABBITMQ_ENABLED, RABBITMQ_HOST, RABBITMQ_PORT, RABBITMQ_USER, RABBITMQ_PASSWORD,
		COMPANY_LOGO_URL
	}

	public enum Tool
	{
		GOOGLE, JIRA, ELASTICSEARCH, JENKINS, SLACK, EMAIL, AMAZON, HIPCHAT, LDAP, CRYPTO, RABBITMQ
	}

	public boolean isValueForEncrypting()
	{
		return TO_ENCRYPT.contains(this.getName());
	}
}