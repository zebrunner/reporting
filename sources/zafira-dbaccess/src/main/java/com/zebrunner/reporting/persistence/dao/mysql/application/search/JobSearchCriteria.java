package com.zebrunner.reporting.persistence.dao.mysql.application.search;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
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

}
