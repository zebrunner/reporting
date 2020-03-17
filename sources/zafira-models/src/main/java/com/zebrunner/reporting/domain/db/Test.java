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
package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class Test extends AbstractEntity implements Comparable<Test> {
    private static final long serialVersionUID = -915700504693067056L;

    private String name;
    private Status status;
    private String testArgs;
    private Long testRunId;
    private Long testCaseId;
    private String testGroup;
    private String message;
    private Integer messageHashCode;
    private Date startTime;
    private Date finishTime;
    private int retry;
    private TestConfig testConfig;
    private List<WorkItem> workItems;
    private boolean knownIssue;
    private boolean blocker;
    private boolean needRerun;
    private String owner;
    private String secondaryOwner;
    private String dependsOnMethods;
    private String testClass;
    private Set<TestArtifact> artifacts = new HashSet<>();
    private String ciTestId;
    private Set<Tag> tags;

    public Test() {
        this.testConfig = new TestConfig();
    }

    public String getNotNullTestGroup() {
        return testGroup == null ? "n/a" : testGroup;
    }

    public WorkItem getWorkItemByType(WorkItem.Type type) {
        return workItems.stream()
                        .filter(workItem -> type.equals(workItem.getType()))
                        .findFirst()
                        .orElse(null);
    }

    @Override
    public int compareTo(Test test) {
        if (Arrays.asList(Status.QUEUED, Status.ABORTED, Status.SKIPPED, Status.FAILED).contains(this.getStatus())) {
            return -1;
        } else {
            return 0;
        }
    }
}