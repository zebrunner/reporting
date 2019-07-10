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
package com.qaprosoft.zafira.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.models.dto.TagType;
import org.testng.ITestResult;

import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.config.ConfigurationType;

import static com.qaprosoft.zafira.client.ClientDefaults.USER;

/**
 * Default implementation of Zafira {@link IConfigurator} used for more deep integration with test frameworks.
 * It should be enough to use default configurator to get base reporting functionality.
 * 
 * @author akhursevich
 */
public class DefaultConfigurator implements IConfigurator {

    @Override
    public ConfigurationType getConfiguration() {
        return new ConfigurationType();
    }

    @Override
    public String getOwner(SuiteAdapter adapter) {
        return USER;
    }

    @Override
    public String getPrimaryOwner(TestResultAdapter adapter) {
        return USER;
    }

    @Override
    public String getSecondaryOwner(TestResultAdapter adapter) {
        return null;
    }

    @Override
    public String getTestName(TestResultAdapter adapter) {
        ITestResult testResult = (ITestResult) adapter.getTestResult();
        return testResult.getTestName();
    }

    @Override
    public String getTestMethodName(TestResultAdapter adapter) {
        return adapter.getMethodAdapter().getMethodName();
    }

    @Override
    public int getRunCount(TestResultAdapter adapter) {
        return 0;
    }

    @Override
    public List<String> getTestWorkItems(TestResultAdapter adapter) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Long> getTestMetrics(TestResultAdapter adapter) {
        return null;
    }

    @Override
    public Set<TestArtifactType> getArtifacts(TestResultAdapter adapter) {
        return new HashSet<>();
    }

    @Override
    public void clearArtifacts() {
    }

    @Override
    public Set<TagType> getTestTags(TestResultAdapter adapter) {
        return new HashSet<>();
    }

}