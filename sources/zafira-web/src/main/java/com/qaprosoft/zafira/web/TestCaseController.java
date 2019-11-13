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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestCaseSearchCriteria;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.models.db.TestMetric;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.service.TestCaseService;
import com.qaprosoft.zafira.service.TestMetricService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Api("Test cases API")
@RequestMapping(path = "api/tests/cases", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestCaseController extends AbstractController {

    private final Mapper mapper;
    private final TestCaseService testCaseService;
    private final TestMetricService testMetricService;

    public TestCaseController(Mapper mapper, TestCaseService testCaseService, TestMetricService testMetricService) {
        this.mapper = mapper;
        this.testCaseService = testCaseService;
        this.testMetricService = testMetricService;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Search test cases", nickname = "searchTestCases", httpMethod = "POST", response = SearchResult.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/search")
    public SearchResult<TestCase> searchTestCases(@Valid @RequestBody TestCaseSearchCriteria sc) {
        return testCaseService.searchTestCases(sc);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get test metrics by test case id", nickname = "getTestMetricsByTestCaseId", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/{id}/metrics")
    public Map<String, List<TestMetric>> getTestMetricsByTestCaseId(@PathVariable("id") Long id) {
        return testMetricService.getTestMetricsByTestCaseId(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create test case", nickname = "createTestCase", httpMethod = "POST", response = TestCaseType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping()
    public TestCaseType createTestCase(
            @RequestBody @Valid TestCaseType testCase,
            @RequestHeader(name = "Project", required = false) String projectName
    ) throws ExecutionException {
        TestCase tc = mapper.map(testCase, TestCase.class);
        return mapper.map(testCaseService.createOrUpdateCase(tc, projectName), TestCaseType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create multiple test cases", nickname = "createTestCases", httpMethod = "POST", response = TestCaseType[].class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/batch")
    public TestCaseType[] createTestCases(
            @RequestBody @Valid TestCaseType[] tcs,
            @RequestHeader(name = "Project", required = false) String projectName
    ) throws ExecutionException {
        if (ArrayUtils.isEmpty(tcs)) {
            return new TestCaseType[0];
        }
        TestCase[] testCases = Arrays.stream(tcs)
                                     .map(testCaseType -> mapper.map(testCaseType, TestCase.class))
                                     .toArray(TestCase[]::new);
        testCases = testCaseService.createOrUpdateCases(testCases, projectName);

        tcs = Arrays.stream(testCases)
                    .map(testCase -> mapper.map(testCase, TestCaseType.class))
                    .toArray(TestCaseType[]::new);
        return tcs;
    }

}