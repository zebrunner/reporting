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

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TestCase extends AbstractEntity {
    private static final long serialVersionUID = 4877029098773384360L;

    private String testClass;
    private String testMethod;
    private Status status;
    private String info;
    private Long testSuiteId;
    private User primaryOwner = new User();
    private User secondaryOwner = new User();
    private TestSuite testSuite = new TestSuite();
    private Project project;
    private Long stability;

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TestCase && this.hashCode() == obj.hashCode());
    }

    @Override
    public int hashCode() {
        return (testClass + testMethod + testSuiteId + info +
                primaryOwner.getId() + secondaryOwner.getId() + (project != null ? project.getName() : "")).hashCode();
    }
}
