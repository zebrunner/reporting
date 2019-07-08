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
package com.qaprosoft.zafira.listener.service.impl;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.listener.service.TestTypeService;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.dto.TagType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.util.http.HttpClient;

import java.util.List;
import java.util.Set;

public class TestTypeServiceImpl implements TestTypeService {

    private final ZafiraClient zafiraClient;

    public TestTypeServiceImpl(ZafiraClient zafiraClient) {
        this.zafiraClient = zafiraClient;
    }


    @Override
    public TestType registerTestRestart(TestType test) {
        return zafiraClient.registerTestRestart(test);
    }

    @Override
    public TestType registerTestStart(String testName, String group, Status status, String testArgs, long testRunId, long testCaseId, int runCount,
                                      String xmlConfiguration, String[] dependsOnMethods, String testCiId, Set<TagType> tags) {
        return zafiraClient.registerTestStart(testName, group, status, testArgs, testRunId, testCaseId, runCount,
                xmlConfiguration, dependsOnMethods, testCiId, tags);
    }

    @Override
    public TestType registerWorkItems(long testId, List<String> workItems) {
        return zafiraClient.registerWorkItems(testId, workItems);
    }

    @Override
    public TestType finishTest(TestType test) {
        HttpClient.Response<TestType> result = zafiraClient.finishTest(test);
        if (result.getStatus() != 200 && result.getObject() == null) {
            throw new RuntimeException("Unable to register test " + test.getName() + " for zafira service: " + zafiraClient.getServiceUrl());
        }
        return result.getObject();
    }

}
