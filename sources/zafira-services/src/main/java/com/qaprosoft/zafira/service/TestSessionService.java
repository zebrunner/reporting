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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.persistence.TestSessionRepository;
import com.qaprosoft.zafira.models.entity.TestSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TestSessionService {

    private final TestSessionRepository testSessionRepository;

    public TestSessionService(TestSessionRepository testSessionRepository) {
        this.testSessionRepository = testSessionRepository;
    }

    @Transactional(readOnly = true)
    public SearchResult<TestSession> search(SearchCriteria criteria) {
        Pageable pageable = buildPageable(criteria);
        Specification<TestSession> specification = Specification.where(null);
        Page<TestSession> page = testSessionRepository.findAll(specification, pageable);
        return SearchResult.<TestSession>builder()
                .results(page.getContent())
                .totalResults((int) page.getTotalElements())
                .pageSize(page.getSize())
                .page(page.getNumber())
                .build();
    }

    private Pageable buildPageable(SearchCriteria criteria) {
        if (!StringUtils.isEmpty(criteria.getOrderBy())) {
            Sort sortBy = Sort.by(criteria.getOrderBy());
            sortBy = criteria.getSortOrder().equals(SearchCriteria.SortOrder.ASC) ? sortBy.ascending() : sortBy.descending();
            return PageRequest.of(criteria.getPage(), criteria.getPageSize(), sortBy);
        } else {
            return PageRequest.of(criteria.getPage(), criteria.getPageSize());
        }
    }

}
