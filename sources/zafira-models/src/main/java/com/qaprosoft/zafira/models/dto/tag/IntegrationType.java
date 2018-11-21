package com.qaprosoft.zafira.models.dto.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qaprosoft.zafira.models.db.IntegrationInfo;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationType {

    private String projectId;
    private String suiteId;
    private List<IntegrationInfo> integrationInfo;

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
}
