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

import com.qaprosoft.zafira.config.CiConfig;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.config.ConfigurationType;

import java.util.List;

public interface TestRunTypeService {

    TestRunType findTestRunByCiRunId(String ciRunId);

    TestRunType startTestRun(TestRunType testRun);

    /**
     * Resets build number(to map to the latest rerun build),
     *  config(in case of queued tests),
     *  suite (https://github.com/qaprosoft/zafira/issues/1584) and starts test run
     * @param testRun - test run to start
     * @param ciBuildNumber - build number to map to the latest build
     * @param suiteId - created suite id
     * @param configuration - test run configuration file
     * @return started test run
     */
    TestRunType rerun(TestRunType testRun, int ciBuildNumber, long suiteId, ConfigurationType configuration);

    boolean abort(Long testRunId);

    List<TestType> findTestRunResults(long id);

    TestRunType register(TestRunType testRun, CiConfig.BuildCase buildCase, long suiteId, long jobId, long userId, JobType parentJob,
                         CiConfig ciConfig, String jiraSuiteId, ConfigurationType configuration);

    /**
     * Registers test run results
     * Resets configuration to store for example updated at run-time app_version etc
     * @param testRun - with result to register
     * @param configuration - configuration to override
     * @return - test run from response
     */
    TestRunType registerTestRunResults(TestRunType testRun, ConfigurationType configuration);

    String convertConfigurationToXML(ConfigurationType config);

}
