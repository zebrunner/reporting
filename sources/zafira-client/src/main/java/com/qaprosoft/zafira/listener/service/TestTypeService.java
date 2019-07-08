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
package com.qaprosoft.zafira.listener.service;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.dto.TagType;
import com.qaprosoft.zafira.models.dto.TestType;

import java.util.List;
import java.util.Set;

public interface TestTypeService {

    TestType registerTestRestart(TestType test);

    TestType registerTestStart(String testName, String group, Status status, String testArgs, long testRunId, long testCaseId, int runCount,
                               String xmlConfiguration, String[] dependsOnMethods, String testCiId, Set<TagType> tags);

    TestType registerWorkItems(long testId, List<String> workItems);

    TestType finishTest(TestType test);

}
