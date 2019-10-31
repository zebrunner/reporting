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
package com.qaprosoft.zafira.dbaccess.dao.mysql.application.search;

import com.qaprosoft.zafira.models.db.Project;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SearchResult<T> extends SearchCriteria {

    private List<T> results;
    private Integer totalResults;

    @Builder
    public SearchResult(String query, String orderBy, Integer page, Integer pageSize, List<Project> projects, SortOrder sortOrder, List<T> results, Integer totalResults) {
        super(query, orderBy, page, pageSize, projects, sortOrder);
        this.results = results;
        this.totalResults = totalResults;
    }
}
