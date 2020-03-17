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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestCaseType extends AbstractType {

    private static final long serialVersionUID = 4361075320159665047L;

    @NotNull
    private String testClass;
    @NotNull
    private String testMethod;
    private String info;
    @NotNull
    private Long testSuiteId;
    @NotNull
    private Long primaryOwnerId;
    private Long secondaryOwnerId;
    private ProjectDTO project;

    public TestCaseType(String testClass, String testMethod, String info, Long testSuiteId, Long primaryOwnerId) {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.info = info;
        this.testSuiteId = testSuiteId;
        this.primaryOwnerId = primaryOwnerId;
    }

    public TestCaseType(String testClass, String testMethod, String info, Long testSuiteId, Long primaryOwnerId, Long secondaryUserId) {
        this(testClass, testMethod, info, testSuiteId, primaryOwnerId);
        this.secondaryOwnerId = secondaryUserId;
    }

}
