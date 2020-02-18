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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestRun extends AbstractEntity {
    private static final long serialVersionUID = -1847933012610222160L;
    private static final String NAME = "%s %s (%s) on %s %s";

    private Map<String, String> configuration = new HashMap<>();
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
    private String appVersion;
    private Date startedAt;
    private Integer elapsed;
    private Integer eta;
    private String comments;
    private String slackChannels;
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

    @Builder
    public TestRun(Long id, String ciRunId) {
        super(id);
        this.ciRunId = ciRunId;
    }

    public String getName(Map<String, String> configuration) {
        this.configuration = configuration;
        String appVersion = argumentIsPresent("app_version") ? this.configuration.get("app_version") + " - " : "";
        String platformInfo = buildPlatformInfo();
        return String.format(NAME, appVersion, testSuite.getName(), testSuite.getFileName(), this.configuration.get("env"), platformInfo).trim();
    }

    private boolean argumentIsPresent(String arg, String... ignoreValues) {
        if (configuration.get(arg) == null || "".equals(configuration.get(arg)) || configuration.get(arg).equalsIgnoreCase("null")) {
            return false;
        }
        for (String ignoreValue : ignoreValues) {
            if (configuration.get(arg).equals(ignoreValue)) {
                return false;
            }
        }
        return true;
    }

    private String buildPlatformInfo() {
        String platformInfo = "%s %s %s";
        String mobilePlatformVersion = argumentIsPresent("mobile_platform_name") ? configuration.get("mobile_platform_name") : "";
        String browser = argumentIsPresent("browser") ? configuration.get("browser") : "";
        String locale = argumentIsPresent("locale", "en_US", "en", "US") ? configuration.get("locale") : "";
        platformInfo = String.format(platformInfo, mobilePlatformVersion, browser, locale);
        platformInfo = platformInfo.trim();
        while (platformInfo.contains("  ")) {
            platformInfo = platformInfo.replaceFirst("  ", " ");
        }
        platformInfo = "(" + platformInfo + ")";
        if (!platformInfo.equals("()"))
            return platformInfo;
        else
            return "";
    }

    public enum Initiator {
        SCHEDULER,
        UPSTREAM_JOB,
        HUMAN
    }

}
