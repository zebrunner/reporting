package com.zebrunner.reporting.domain.dto.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zebrunner.reporting.domain.db.TestInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagIntegrationDataDTO {

    private String projectId;
    private String suiteId;
    private List<TestInfo> testInfo;
    private String testRunName;
    private String testRunId;
    private String env;
    private Date createdAfter;
    private Date startedAt;
    private Date finishedAt;
    private Map<String, String> customParams = new HashMap<>();
    private String zafiraServiceUrl;

}
