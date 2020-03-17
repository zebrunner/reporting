package com.zebrunner.reporting.persistence.dao.mysql.application.search;

public class FilterSearchCriteria {

    private String filterWhereClause;

    public FilterSearchCriteria(String filterWhereClause) {
        this.filterWhereClause = filterWhereClause;
    }

    public String getFilterWhereClause() {
        return filterWhereClause;
    }

    public void setFilterWhereClause(String filterWhereClause) {
        this.filterWhereClause = filterWhereClause;
    }
}
