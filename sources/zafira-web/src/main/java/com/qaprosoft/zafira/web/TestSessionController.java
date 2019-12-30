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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.models.entity.TestSession;
import com.qaprosoft.zafira.service.TestSessionService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("Test session API")
@RequestMapping(path = "api/tests/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestSessionController extends AbstractController {

    private final TestSessionService testSessionService;

    public TestSessionController(TestSessionService testSessionService) {
        this.testSessionService = testSessionService;
    }

    @ApiResponseStatuses
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Search test sessions", nickname = "search", httpMethod = "POST", response = SearchResult.class)
    @PostMapping("/search")
    public SearchResult<TestSession> search(@RequestBody SearchCriteria criteria) {
        return testSessionService.search(criteria);
    }

}
