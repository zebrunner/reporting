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
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.TestSessionSearchCriteria;
import com.qaprosoft.zafira.models.dto.testsession.SearchParameter;
import com.qaprosoft.zafira.models.entity.TestSession;
import com.qaprosoft.zafira.service.TestSessionService;
import com.qaprosoft.zafira.web.documented.TestSessionDocumentedController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "api/tests/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestSessionController extends AbstractController implements TestSessionDocumentedController {

    private final TestSessionService testSessionService;

    public TestSessionController(TestSessionService testSessionService) {
        this.testSessionService = testSessionService;
    }

    @GetMapping("/{id}")
    @Override
    public TestSession getById(@PathVariable("id") Long id) {
        return testSessionService.retrieveById(id);
    }

    @GetMapping("/search")
    @Override
    public SearchResult<TestSession> search(TestSessionSearchCriteria criteria) {
        return testSessionService.search(criteria);
    }

    @GetMapping("/search/parameters")
    @Override
    public SearchParameter getSearchParameters() {
        return testSessionService.collectSearchParameters();
    }

}
