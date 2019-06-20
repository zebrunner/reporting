/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class Setting extends AbstractEntity {

    private static final long serialVersionUID = -6809215085336377266L;

    private String name;
    private String value;
    private boolean isEncrypted;
    private Tool tool;
    private byte[] file;

    public enum Tool {
        RABBITMQ(
                true,
                SettingType.RABBITMQ_ENABLED,
                SettingType.RABBITMQ_HOST,
                SettingType.RABBITMQ_PORT,
                SettingType.RABBITMQ_USER,
                SettingType.RABBITMQ_PASSWORD),
        GOOGLE(SettingType.GOOGLE_CLIENT_SECRET_ORIGIN, SettingType.GOOGLE_ENABLED),
        JIRA(SettingType.JIRA_URL, SettingType.JIRA_USER, SettingType.JIRA_PASSWORD, SettingType.JIRA_CLOSED_STATUS, SettingType.JIRA_ENABLED),
        ELASTICSEARCH,
        JENKINS(
                SettingType.JENKINS_URL,
                SettingType.JENKINS_USER,
                SettingType.JENKINS_API_TOKEN_OR_PASSWORD,
                SettingType.JENKINS_FOLDER,
                SettingType.JENKINS_ENABLED),
        SLACK(SettingType.SLACK_WEB_HOOK_URL, SettingType.SLACK_NOTIF_CHANNEL_EXAMPLE, SettingType.SLACK_ENABLED),
        EMAIL(
                SettingType.EMAIL_HOST,
                SettingType.EMAIL_PORT,
                SettingType.EMAIL_USER,
                SettingType.EMAIL_FROM_ADDRESS,
                SettingType.EMAIL_PASSWORD,
                SettingType.EMAIL_ENABLED),
        AMAZON(
                SettingType.AMAZON_ACCESS_KEY,
                SettingType.AMAZON_SECRET_KEY,
                SettingType.AMAZON_REGION,
                SettingType.AMAZON_BUCKET,
                SettingType.AMAZON_ENABLED),
        LDAP(
                SettingType.LDAP_DN,
                SettingType.LDAP_SEARCH_FILTER,
                SettingType.LDAP_URL,
                SettingType.LDAP_MANAGER_USER,
                SettingType.LDAP_MANAGER_PASSWORD,
                SettingType.LDAP_ENABLED),
        SELENIUM(
                SettingType.SELENIUM_URL,
                SettingType.SELENIUM_USER,
                SettingType.SELENIUM_PASSWORD,
                SettingType.SELENIUM_ENABLED),
        CRYPTO(1, SettingType.CRYPTO_KEY_TYPE, SettingType.CRYPTO_ALGORITHM, SettingType.CRYPTO_KEY_SIZE, SettingType.KEY);

        private final List<SettingType> toolSettings;
        private int priority;
        private boolean decrypt;

        Tool(SettingType... toolSettings) {
            this.toolSettings = Arrays.asList(toolSettings);
        }

        Tool(int priority, SettingType... toolSettings) {
            this.priority = priority;
            this.toolSettings = Arrays.asList(toolSettings);
        }

        Tool(boolean decrypt, SettingType... toolSettings) {
            this(toolSettings);
            this.decrypt = decrypt;
        }

        public List<SettingType> getToolSettings() {
            return toolSettings;
        }

        public int getPriority() {
            return priority;
        }

        public boolean isDecrypt() {
            return decrypt;
        }

        public static Tool[] getValues() {
            Tool[] result = Tool.values();
            Arrays.sort(result, Comparator.comparing(Tool::getPriority).reversed());
            return result;
        }
    }

    public enum SettingType {
        GOOGLE_CLIENT_SECRET_ORIGIN,
        GOOGLE_ENABLED,
        JIRA_URL,
        JIRA_USER,
        JIRA_PASSWORD(true),
        JIRA_CLOSED_STATUS,
        JIRA_ENABLED,
        JENKINS_URL,
        JENKINS_USER,
        JENKINS_API_TOKEN_OR_PASSWORD(true),
        JENKINS_FOLDER(false, false),
        JENKINS_ENABLED,
        SLACK_WEB_HOOK_URL,
        SLACK_NOTIF_CHANNEL_EXAMPLE,
        SLACK_ENABLED,
        EMAIL_HOST,
        EMAIL_PORT,
        EMAIL_USER,
        EMAIL_FROM_ADDRESS(false, false),
        EMAIL_PASSWORD(true),
        EMAIL_ENABLED,
        AMAZON_ACCESS_KEY,
        AMAZON_SECRET_KEY(true),
        AMAZON_REGION,
        AMAZON_BUCKET,
        AMAZON_ENABLED,
        LDAP_DN,
        LDAP_SEARCH_FILTER,
        LDAP_URL,
        LDAP_MANAGER_USER,
        LDAP_MANAGER_PASSWORD(true),
        LDAP_ENABLED,
        CRYPTO_KEY_TYPE,
        CRYPTO_ALGORITHM(false, false),
        CRYPTO_KEY_SIZE,
        KEY(false, false),
        RABBITMQ_HOST,
        RABBITMQ_PORT,
        RABBITMQ_USER,
        RABBITMQ_PASSWORD(true),
        RABBITMQ_ENABLED,
        SELENIUM_URL,
        SELENIUM_USER(false, false),
        SELENIUM_PASSWORD(false, true),
        SELENIUM_ENABLED,
        COMPANY_LOGO_URL;

        private final Boolean required;
        private final Boolean encrypted;

        SettingType() {
            this(true, false);
        }

        SettingType(Boolean encrypted) {
            this(true, encrypted);
        }

        SettingType(Boolean required, Boolean encrypted) {
            this.required = required;
            this.encrypted = encrypted;
        }

        public Boolean isRequired() {
            return required;
        }

        public Boolean isEncrypted() {
            return encrypted;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public boolean isValueForEncrypting() {
        return this.tool != null && SettingType.valueOf(this.name).isEncrypted();
    }

}
