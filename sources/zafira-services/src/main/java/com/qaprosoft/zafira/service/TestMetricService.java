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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestMetricMapper;
import com.qaprosoft.zafira.models.db.TestMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestMetricService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMetricService.class);

    @Autowired
    private TestMetricMapper testMetricMapper;

    @Transactional(readOnly = true)
    public List<String> getEnvsByTestCaseId(Long testCaseId) {
        return testMetricMapper.getEnvsByTestCaseId(testCaseId);
    }

    @Transactional(readOnly = true)
    public Map<String, List<TestMetric>> getTestMetricsByTestCaseId(Long testCaseId) {
        Map<String, List<TestMetric>> result = new HashMap<>();
        getEnvsByTestCaseId(testCaseId).forEach(env -> {
            List<TestMetric> testMetrics = testMetricMapper.getTestMetricsByTestCaseIdAndEnv(testCaseId, env);
            result.put(env, testMetrics);
        });
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createTestMetrics(Long testId, Map<String, Long> metrics) {
        try {
            if (metrics != null) {
                metrics.keySet().stream()
                       .map(key -> new TestMetric(key, metrics.get(key), testId))
                       .forEach(metric -> testMetricMapper.createTestMetric(metric));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to register test metrics: " + e.getMessage());
        }
    }
}