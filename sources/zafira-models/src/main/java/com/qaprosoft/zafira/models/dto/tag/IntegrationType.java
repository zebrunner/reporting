package com.qaprosoft.zafira.models.dto.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qaprosoft.zafira.models.db.IntegrationInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationType {

    private String projectId;
    private String suiteId;
    private List<IntegrationInfo> integrationInfo;
    private String testRunName;
    private Long createdAfter;
    private Map<String, String> customParams = new HashMap<>();

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(String suiteId) {
        this.suiteId = suiteId;
    }

    public List<IntegrationInfo> getIntegrationInfo() {
        return integrationInfo;
    }

    public void setIntegrationInfo(List<IntegrationInfo> integrationInfo) {
        this.integrationInfo = integrationInfo;
    }

    public String getTestRunName() {
        return testRunName;
    }

    public void setTestRunName(String testRunName) {
        this.testRunName = testRunName;
    }

    public Long getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Long createdAfter) {
        this.createdAfter = createdAfter;
    }

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, String> customParams) {
        this.customParams = customParams;
    }

}
