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
package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestConfigMapper;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestConfig;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.util.XmlConfigurationUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestConfigService {

    private static final Logger logger = Logger.getLogger(TestConfigService.class);

    @Autowired
    private TestConfigMapper testConfigMapper;

    @Autowired
    private TestRunService testRunService;

    @Transactional(rollbackFor = Exception.class)
    public void createTestConfig(TestConfig testConfig) {
        testConfigMapper.createTestConfig(testConfig);
    }

    @Transactional(rollbackFor = Exception.class)
    public TestConfig createTestConfigForTest(Test test, String testConfigXML) {
        TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
        if (testRun == null) {
            throw new ServiceException("Test run not found!");
        }

        List<Argument> testRunConfig = XmlConfigurationUtil.readArguments(testRun.getConfigXML()).getArg();
        List<Argument> testConfig = XmlConfigurationUtil.readArguments(testConfigXML).getArg();

        TestConfig config = new TestConfig().init(testRunConfig).init(testConfig);

        TestConfig existingTestConfig = searchTestConfig(config);
        if (existingTestConfig != null) {
            config = existingTestConfig;
        } else {
            createTestConfig(config);
        }

        return config;
    }

    @Transactional(rollbackFor = Exception.class)
    public TestConfig createTestConfigForTestRun(String configXML) {
        List<Argument> testRunConfig = XmlConfigurationUtil.readArguments(configXML).getArg();

        TestConfig config = new TestConfig().init(testRunConfig);

        TestConfig existingTestConfig = searchTestConfig(config);
        if (existingTestConfig != null) {
            config = existingTestConfig;
        } else {
            createTestConfig(config);
        }
        return config;
    }

    @Transactional(readOnly = true)
    public TestConfig searchTestConfig(TestConfig testConfig) {
        return testConfigMapper.searchTestConfig(testConfig);
    }
}
