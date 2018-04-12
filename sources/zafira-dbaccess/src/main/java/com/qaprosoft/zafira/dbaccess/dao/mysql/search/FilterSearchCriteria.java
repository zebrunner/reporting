package com.qaprosoft.zafira.dbaccess.dao.mysql.search;

public class FilterSearchCriteria {

    private String filterTemplate;
    private String filterSearchCountTemplate;

    public String getFilterTemplate() {
        return filterTemplate;
    }

    public void setFilterTemplate(String filterTemplate) {
        this.filterTemplate = filterTemplate;
    }

    public String getFilterSearchCountTemplate() {
        return filterSearchCountTemplate;
    }

    public void setFilterSearchCountTemplate(String filterSearchCountTemplate) {
        this.filterSearchCountTemplate = filterSearchCountTemplate;
    }
}
