package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagIntegrationData {

    private String projectId;
    private String suiteId;
    private List<TestInfo> testInfo;
    private String testRunName;
    private String testRunId;
    private String env;
    private Date createdAfter;
    private Date startedAt;
    private Date finishedAt;
    private Map<String, String> customParams;
    private String zafiraServiceUrl;

}
