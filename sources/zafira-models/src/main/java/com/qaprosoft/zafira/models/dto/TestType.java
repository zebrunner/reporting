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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestType extends AbstractType {
    private static final long serialVersionUID = 7777895715362820880L;
    @NotNull
    private String name;
    private Status status;
    private String testArgs;
    @NotNull
    private Long testRunId;
    @NotNull
    private Long testCaseId;
    private String testGroup;
    private String message;
    private Integer messageHashCode;
    private Long startTime;
    private Long finishTime;
    private List<String> workItems;
    private int retry;
    private String configXML;
    private Map<String, Long> testMetrics;
    private boolean knownIssue;
    private boolean blocker;
    private boolean needRerun;
    private String dependsOnMethods;
    private String testClass;
    @Valid
    private Set<TestArtifactType> artifacts = new HashSet<>();
    private String ciTestId;
    @Valid
    private Set<TagType> tags;

    public TestType(String name, Status status, String testArgs, Long testRunId, Long testCaseId, Long startTime,
            List<String> workItems, int retry, String configXML) {
        this.name = name;
        this.status = status;
        this.testArgs = testArgs;
        this.testRunId = testRunId;
        this.testCaseId = testCaseId;
        this.startTime = startTime;
        this.workItems = workItems;
        this.retry = retry;
        this.configXML = configXML;
    }

}