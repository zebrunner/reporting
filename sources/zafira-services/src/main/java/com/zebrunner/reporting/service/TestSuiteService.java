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
package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.TestSuiteMapper;
import com.zebrunner.reporting.domain.db.TestSuite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestSuiteService {

    private final TestSuiteMapper testSuiteMapper;

    public TestSuiteService(TestSuiteMapper testSuiteMapper) {
        this.testSuiteMapper = testSuiteMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createTestSuite(TestSuite testSuite) {
        testSuiteMapper.createTestSuite(testSuite);
    }

    @Transactional(readOnly = true)
    public TestSuite getTestSuiteByIdFull(long id) {
        return testSuiteMapper.getTestSuiteByIdFull(id);
    }

    @Transactional(readOnly = true)
    public TestSuite getTestSuiteByNameAndFileNameAndUserId(String name, String fileName, long userId) {
        return testSuiteMapper.getTestSuiteByNameAndFileNameAndUserId(name, fileName, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public TestSuite updateTestSuite(TestSuite testSuite) {
        testSuiteMapper.updateTestSuite(testSuite);
        return testSuite;
    }

    @Transactional(rollbackFor = Exception.class)
    public TestSuite createOrUpdateTestSuite(TestSuite newTestSuite) {
        TestSuite testSuite = getTestSuiteByNameAndFileNameAndUserId(newTestSuite.getName(), newTestSuite.getFileName(),
                newTestSuite.getUser().getId());
        if (testSuite == null) {
            createTestSuite(newTestSuite);
        } else if (!testSuite.equals(newTestSuite)) {
            newTestSuite.setId(testSuite.getId());
            updateTestSuite(newTestSuite);
        } else {
            newTestSuite = testSuite;
        }
        return newTestSuite;
    }
}
