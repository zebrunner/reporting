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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.models.dto.TagType;

import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.config.ConfigurationType;

/**
 * Interface provided to perform better integration with Zafira reporting tool.
 * 
 * @author akhursevich
 */
public interface IConfigurator {

    ConfigurationType getConfiguration();

    String getOwner(SuiteAdapter adapter);

    String getPrimaryOwner(TestResultAdapter adapter);

    String getSecondaryOwner(TestResultAdapter adapter);

    String getTestName(TestResultAdapter adapter);

    String getTestMethodName(TestResultAdapter adapter);

    Set<TestArtifactType> getArtifacts(TestResultAdapter adapter);

    void clearArtifacts();

    Set<TagType> getTestTags(TestResultAdapter adapter);

    List<String> getTestWorkItems(TestResultAdapter adapter);

    int getRunCount(TestResultAdapter adapter);

    Map<String, Long> getTestMetrics(TestResultAdapter adapter);

}
