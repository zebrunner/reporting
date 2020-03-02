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

import static org.springframework.util.StringUtils.isEmpty;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestRun extends AbstractEntity {
    private static final long serialVersionUID = -1847933012610222160L;

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

    public String getName() {
        // For most cases config is present, but for a small amount of
        // invalid data we should process this case
        if (config == null) {
            return "";
        }
        String name = "%s %s (%s) on %s %s";
        String appVersion = isEmpty(config.getAppVersion()) ? config.getAppVersion() + " - " : "";
        String platformInfo = buildPlatformInfo();
        return String.format(name, appVersion, testSuite.getName(), testSuite.getFileName(), config.getEnv(), platformInfo).trim();
    }

    private String buildPlatformInfo() {
        StringBuilder platformInfoBuilder = new StringBuilder();
        platformInfoBuilder.append(config.buildPlatformName());
        if (!"en_US".equals(config.getLocale())) {
            platformInfoBuilder.append(" ")
                               .append(config.getLocale());
        }
        platformInfoBuilder.insert(0, "(");
        platformInfoBuilder.append(")");
        return platformInfoBuilder.toString();
    }

    public enum Initiator {
        SCHEDULER,
        UPSTREAM_JOB,
        HUMAN
    }

}
