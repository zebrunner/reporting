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
 ******************************************************************************/
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.FilterMapper;
import com.qaprosoft.zafira.models.db.filter.FilterAdapter;
import com.qaprosoft.zafira.models.db.filter.Filter;
import com.qaprosoft.zafira.models.dto.filter.StoredSubject;
import com.qaprosoft.zafira.models.dto.filter.Subject;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import com.qaprosoft.zafira.service.util.FreemarkerUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.FILTER_CAN_NOT_BE_CREATED;
import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.ILLEGAL_FILTER_ACCESS;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.FILTER_NOT_FOUND;

@Service
public class FilterService {

    private static final String ERR_MSG_FILTER_CAN_NOT_BE_FOUND = "Filter with id %s can not be found";
    private static final String ERR_MSG_FILTER_WITH_SUCH_NAME_ALREADY_EXISTS = "Filter with such name already exists";
    private static final String ERR_MSG_ILLEGAL_FILTER_MODIFICATION = "Only creator can modify or delete filter";

    private final FilterMapper filterMapper;
    private final FreemarkerUtil freemarkerUtil;
    private StoredSubject storedSubject;

    public FilterService(FilterMapper filterMapper, FreemarkerUtil freemarkerUtil) {
        this.filterMapper = filterMapper;
        this.freemarkerUtil = freemarkerUtil;
        this.storedSubject = new StoredSubject();
    }

    public enum Template {
        TEST_RUN_TEMPLATE("/filter.ftl");

        private final String path;

        Template(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Filter createFilter(Filter filter, long userId, boolean isAdmin) {
        filter.setUserId(userId);
        if (isFilterExists(filter)) {
            throw new IllegalOperationException(FILTER_CAN_NOT_BE_CREATED, ERR_MSG_FILTER_WITH_SUCH_NAME_ALREADY_EXISTS);
        }
        if (filter.isPublicAccess() && !isAdmin) {
            filter.setPublicAccess(false);
        }
        filterMapper.createFilter(filter);
        return filter;
    }

    @Transactional(readOnly = true)
    public Filter getFilterById(long id) {
        return filterMapper.getFilterById(id);
    }

    @Transactional(readOnly = true)
    public List<Filter> getFiltersByName(String name) {
        return filterMapper.getFiltersByName(name);
    }

    @Transactional(readOnly = true)
    public List<Filter> getAllPublicFilters(Long userId) {
        return filterMapper.getAllPublicFilters(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Filter updateFilter(Filter filter, long userId, boolean isAdmin) {
        Filter dbFilter = getFilterById(filter.getId());
        if (dbFilter == null) {
            throw new ResourceNotFoundException(FILTER_NOT_FOUND, ERR_MSG_FILTER_CAN_NOT_BE_FOUND, filter.getId());
        }
        checkFilterAccess(userId, filter);
        if (!filter.getName().equals(dbFilter.getName()) && isFilterExists(filter)) {
            throw new IllegalOperationException(FILTER_CAN_NOT_BE_CREATED, ERR_MSG_FILTER_WITH_SUCH_NAME_ALREADY_EXISTS);
        }
        dbFilter.setName(filter.getName());
        dbFilter.setDescription(filter.getDescription());
        dbFilter.setSubject(filter.getSubject());
        dbFilter.setPublicAccess(isAdmin && filter.isPublicAccess());
        filterMapper.updateFilter(dbFilter);
        return dbFilter;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFilterById(long id, long userId) {
        Filter filter = getFilterById(id);
        checkFilterAccess(userId, filter);
        filterMapper.deleteFilterById(id);
    }

    private void checkFilterAccess(long userId, Filter filter) {
        boolean ownedByUser = filter.getUserId().equals(userId);
        if (!ownedByUser) {
            throw new IllegalOperationException(ILLEGAL_FILTER_ACCESS, ERR_MSG_ILLEGAL_FILTER_MODIFICATION);
        }
    }

    public Subject getStoredSubject(Subject.Name name) {
        return storedSubject.getSubjectByName(name);
    }

    public String getTemplate(FilterAdapter filterAdapter, Template template) {
        return freemarkerUtil.getFreeMarkerTemplateContent(template.getPath(), filterAdapter);
    }

    public boolean isFilterExists(Filter filter) {
        boolean result;
        List<Filter> filters = getFiltersByName(filter.getName());

        if (filter.isPublicAccess()) {
            result = filters.stream().anyMatch(f -> f.getName().equals(filter.getName()) && f.isPublicAccess());
        } else {
            result = filters.stream().anyMatch(f -> f.getName().equals(filter.getName()) && f.getUserId().equals(filter.getUserId()) && !f.isPublicAccess());
        }
        return result;
    }

}
