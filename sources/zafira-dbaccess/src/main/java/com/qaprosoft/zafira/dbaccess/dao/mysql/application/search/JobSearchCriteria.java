package com.qaprosoft.zafira.dbaccess.dao.mysql.application.search;

import org.hibernate.validator.constraints.NotEmpty;

public class JobSearchCriteria {

    @NotEmpty
    private Long upstreamJobId;

    @NotEmpty
    private Integer upstreamJobBuildNumber;

    private String owner;
    private String scmURL;
    private Integer hashcode;
    private Integer failurePercent;
    private String cause;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getUpstreamJobId() {
        return upstreamJobId;
    }

    public void setUpstreamJobId(Long upstreamJobId) {
        this.upstreamJobId = upstreamJobId;
    }

    public Integer getUpstreamJobBuildNumber() {
        return upstreamJobBuildNumber;
    }

    public void setUpstreamJobBuildNumber(Integer upstreamJobBuildNumber) {
        this.upstreamJobBuildNumber = upstreamJobBuildNumber;
    }

    public String getScmURL() {
        return scmURL;
    }

    public void setScmURL(String scmURL) {
        this.scmURL = scmURL;
    }

    public Integer getHashcode() {
        return hashcode;
    }

    public void setHashcode(Integer hashcode) {
        this.hashcode = hashcode;
    }

    public Integer getFailurePercent() {
        return failurePercent;
    }

    public void setFailurePercent(Integer failurePercent) {
        this.failurePercent = failurePercent;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
