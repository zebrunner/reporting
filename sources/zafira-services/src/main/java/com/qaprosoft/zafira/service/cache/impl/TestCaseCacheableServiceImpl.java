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
package com.qaprosoft.zafira.service.cache.impl;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestCaseMapper;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.service.cache.TestCaseCacheableService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestCaseCacheableServiceImpl implements TestCaseCacheableService {

    private static final String TEST_CASE_CACHE_NAME = "testCases";

    private final TestCaseMapper testCaseMapper;

    public TestCaseCacheableServiceImpl(TestCaseMapper testCaseMapper) {
        this.testCaseMapper = testCaseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = TEST_CASE_CACHE_NAME, key = "{ new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #userId, new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #testClass,  new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #testMethod }")
    public TestCase getOwnedTestCase(Long userId, String testClass, String testMethod, Long projectId) {
        return testCaseMapper.getOwnedTestCase(userId, testClass, testMethod, projectId);
    }
}
