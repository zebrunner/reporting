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
package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun.Initiator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestRunType extends AbstractType {
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
    private ProjectDTO project;
    private boolean knownIssue;
    private boolean blocker;
    private boolean reviewed;

    public TestRunType(String ciRunId, Long testSuiteId, Long userId, String scmURL, String scmBranch, String scmCommit,
            String configXML, Long jobId, Integer buildNumber, Initiator startedBy, String workItem) {
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
            Initiator startedBy, String workItem) {
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
            String configXML, Long jobId, Integer buildNumber, Initiator startedBy, String workItem) {
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

}