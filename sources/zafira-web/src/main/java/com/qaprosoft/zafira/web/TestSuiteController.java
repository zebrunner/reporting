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

import com.qaprosoft.zafira.models.db.TestSuite;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.services.services.application.TestSuiteService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api("Test suites operations")
@RequestMapping(path = "api/tests/suites", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestSuiteController extends AbstractController {

    private final Mapper mapper;
    private final TestSuiteService testSuiteService;

    public TestSuiteController(Mapper mapper, TestSuiteService testSuiteService) {
        this.mapper = mapper;
        this.testSuiteService = testSuiteService;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create test suite", nickname = "createTestSuite", httpMethod = "POST", notes = "Create a new test suite.", response = TestSuiteType.class, responseContainer = "TestSuiteType")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping()
    public TestSuiteType createTestSuite(@RequestBody @Valid TestSuiteType testSuite) {
        return mapper.map(testSuiteService.createOrUpdateTestSuite(mapper.map(testSuite, TestSuite.class)), TestSuiteType.class);
    }

}
