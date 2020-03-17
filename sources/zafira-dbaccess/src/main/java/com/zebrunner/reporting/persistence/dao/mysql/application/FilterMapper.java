package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.filter.Filter;

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
