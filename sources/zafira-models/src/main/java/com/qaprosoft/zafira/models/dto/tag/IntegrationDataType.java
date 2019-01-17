package com.qaprosoft.zafira.models.dto.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qaprosoft.zafira.models.db.TestInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationDataType
{

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

    public List<TestInfo> getTestInfo() {
        return testInfo;
    }

    public void setTestInfo(List<TestInfo> testInfo) {
        this.testInfo = testInfo;
    }

    public String getTestRunName() {
        return testRunName;
    }

    public void setTestRunName(String testRunName) {
        this.testRunName = testRunName;
    }

    public String getTestRunId()
    {
        return testRunId;
    }

    public void setTestRunId(String testRunId)
    {
        this.testRunId = testRunId;
    }

    public String getEnv()
    {
        return env;
    }

    public void setEnv(String env)
    {
        this.env = env;
    }

    public Date getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Date createdAfter) {
        this.createdAfter = createdAfter;
    }

    public Date getStartedAt()
    {
        return startedAt;
    }

    public void setStartedAt(Date startedAt)
    {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt()
    {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt)
    {
        this.finishedAt = finishedAt;
    }

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, String> customParams) {
        this.customParams = customParams;
    }

}
