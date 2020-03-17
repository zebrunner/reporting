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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class WorkItem extends AbstractEntity {

    private static final long serialVersionUID = 5440580857483390564L;

    private String jiraId;
    private String description;
    private boolean blocker;
    private Integer hashCode;
    private Long testCaseId;
    private User user;
    // TODO: think about default type
    private Type type = Type.TASK;

    public WorkItem(String jiraId) {
        this.jiraId = jiraId;
    }

    public WorkItem(String jiraId, Type type) {
        this.jiraId = jiraId;
        this.type = type;
    }

    public enum Type {
        TASK,
        BUG,
        COMMENT,
        EVENT
    }

}
