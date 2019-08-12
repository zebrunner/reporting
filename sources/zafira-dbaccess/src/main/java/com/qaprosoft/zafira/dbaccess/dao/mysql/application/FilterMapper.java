package com.qaprosoft.zafira.dbaccess.dao.mysql.application;

import com.qaprosoft.zafira.models.db.Filter;

import java.util.List;

public interface FilterMapper {

    void createFilter(Filter filter);

    Filter getFilterById(Long id);

    List<Filter> getFiltersByName(String name);

    List<Filter> getAllFilters();

    List<Filter> getAllPublicFilters(Long userId);

    Integer getFiltersCount();

    void updateFilter(Filter filter);

    void deleteFilterById(Long id);
}
