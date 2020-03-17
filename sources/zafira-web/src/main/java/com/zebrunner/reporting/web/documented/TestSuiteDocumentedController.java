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
package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.TestSuiteType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Test suites operations")
public interface TestSuiteDocumentedController {

    @ApiOperation(
            value = "Creates a test suite",
            notes = "Returns the created test suite",
            nickname = "createTestSuite",
            httpMethod = "POST",
            response = TestSuiteType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "testSuite", paramType = "body", dataType = "TestSuiteType", required = true, value = "The test suite to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created test suite", response = TestSuiteType.class)
    })
    TestSuiteType createTestSuite(TestSuiteType testSuite);

}
