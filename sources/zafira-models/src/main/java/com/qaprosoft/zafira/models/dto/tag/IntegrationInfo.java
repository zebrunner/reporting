/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.models.dto.tag;

import com.qaprosoft.zafira.models.dto.AbstractType;
import java.util.List;

public abstract class IntegrationInfo extends AbstractType
{
	private static final long serialVersionUID = 3974731737677785447L;

	public enum IntegrationType {TESTRAIL_TESTCASE_UUID, QTEST_TESTCASE_UUID}

	private String projectId;
    private String suiteId;
    private List<String> testCaseIds;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(String suiteId) {
        this.suiteId = suiteId;
    }

    public List<String> getTestCaseIds() {
        return testCaseIds;
    }

    public void setTestCaseIds(List<String> testCaseIds) {
        this.testCaseIds = testCaseIds;
    }
}
