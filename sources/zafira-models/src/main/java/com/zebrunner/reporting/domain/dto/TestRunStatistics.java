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
package com.zebrunner.reporting.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TestRunStatistics implements Serializable {

    private static final long serialVersionUID = -1915862891525912222L;

    private long testRunId;
    private int passed;
    private int failed;
    private int failedAsKnown;
    private int failedAsBlocker;
    private int skipped;
    private int inProgress;
    private int aborted;
    private int queued;
    private boolean reviewed;

    public enum Action {
        MARK_AS_KNOWN_ISSUE,
        REMOVE_KNOWN_ISSUE,
        MARK_AS_BLOCKER,
        REMOVE_BLOCKER,
        MARK_AS_REVIEWED,
        MARK_AS_NOT_REVIEWED
    }

}
