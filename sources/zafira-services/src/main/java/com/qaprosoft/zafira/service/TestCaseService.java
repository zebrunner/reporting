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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestCaseMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.service.project.ProjectReassignable;
import com.qaprosoft.zafira.service.project.ProjectService;
import com.qaprosoft.zafira.service.util.DateTimeUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class TestCaseService implements ProjectReassignable {

    private final TestCaseMapper testCaseMapper;
    private final ProjectService projectService;

    public TestCaseService(TestCaseMapper testCaseMapper, ProjectService projectService) {
        this.testCaseMapper = testCaseMapper;
        this.projectService = projectService;
    }

    @Transactional
    public void createTestCase(TestCase testCase) {
        if (testCase.getStatus() == null) {
            testCase.setStatus(Status.UNKNOWN);
        }
        testCaseMapper.createTestCase(testCase);
    }

    @Transactional(readOnly = true)
    public TestCase getTestCaseById(long id) {
        return testCaseMapper.getTestCaseById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "testCases", key = "{ new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #userId, new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #testClass,  new com.qaprosoft.zafira.dbaccess.utils.TenancyContext().getTenantName() + ':' + #testMethod }")
    public TestCase getOwnedTestCase(Long userId, String testClass, String testMethod, Long projectId) {
        return testCaseMapper.getOwnedTestCase(userId, testClass, testMethod, projectId);
    }

    @Transactional
    public TestCase updateTestCase(TestCase testCase) {
        testCaseMapper.updateTestCase(testCase);
        return testCase;
    }

    @Transactional
    public TestCase createOrUpdateCase(TestCase testCase, String projectName) {
        Project project = projectService.getProjectByNameOrDefault(projectName);
        testCase.setProject(project);
        return createOrUpdateCase(testCase);
    }

    @Transactional
    public TestCase createOrUpdateCase(TestCase newTestCase) {
        Project project = newTestCase.getProject();
        TestCase testCase = getOwnedTestCase(newTestCase.getPrimaryOwner().getId(), newTestCase.getTestClass(), newTestCase.getTestMethod(), project.getId());
        if (testCase == null || !testCase.getProject().getName().equals(project.getName())) {
            createTestCase(newTestCase);
        } else if (!testCase.equals(newTestCase)) {
            newTestCase.setId(testCase.getId());
            updateTestCase(newTestCase);
        } else {
            newTestCase = testCase;
        }
        return newTestCase;
    }

    @Transactional
    public TestCase[] createOrUpdateCases(TestCase[] testCases, String projectName) {
        Project project = projectService.getProjectByName(projectName);
        Arrays.stream(testCases)
              .forEach(testCase -> testCase.setProject(project));
        return createOrUpdateCases(testCases);
    }

    @Transactional
    public TestCase[] createOrUpdateCases(TestCase[] newTestCases) {
        int index = 0;
        for (TestCase newTestCase : newTestCases) {
            newTestCases[index++] = createOrUpdateCase(newTestCase);
        }
        return newTestCases;
    }

    @Transactional(readOnly = true)
    public SearchResult<TestCase> searchTestCases(TestCaseSearchCriteria sc) {
        DateTimeUtil.actualizeSearchCriteriaDate(sc);

        List<TestCase> testCases = testCaseMapper.searchTestCases(sc);
        int count = testCaseMapper.getTestCasesSearchCount(sc);

        return SearchResult.<TestCase>builder()
                .page(sc.getPage())
                .pageSize(sc.getPageSize())
                .sortOrder(sc.getSortOrder())
                .results(testCases)
                .totalResults(count)
                .build();
    }

    @Override
    @Transactional
    public void reassignProject(Long fromId, Long toId) {
        testCaseMapper.reassignToProject(fromId, toId);
    }
}
