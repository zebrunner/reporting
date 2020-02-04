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
package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestSessionSearchCriteria;
import com.qaprosoft.zafira.models.dto.testsession.SearchParameter;
import com.qaprosoft.zafira.models.entity.TestSession;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Test session API")
public interface TestSessionDocumentedController {

    @ApiOperation(
            value = "Searches for test sessions by specified criteria",
            notes = "Returns the found test sessions",
            nickname = "search",
            httpMethod = "GET",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "criteria", paramType = "body", dataType = "TestSessionSearchCriteria", required = true, value = "Search criteria to search")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found test sessions", response = SearchResult.class)
    })
    SearchResult<TestSession> search(TestSessionSearchCriteria criteria);

    @ApiOperation(
            value = "Retrieves unique search parameters set that can be applied to search criteria",
            notes = "Returns collected parameters",
            nickname = "getSearchParameters",
            httpMethod = "GET",
            response = SearchParameter.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns collected parameters", response = SearchParameter.class)
    })
    SearchParameter getSearchParameters();

}
