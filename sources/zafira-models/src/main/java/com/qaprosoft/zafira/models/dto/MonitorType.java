package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.AbstractEntity;
import com.qaprosoft.zafira.models.db.Monitor;
import org.quartz.CronExpression;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * @author Kirill Bugrim
 *
 * @version 1.0
 */

@JsonInclude(Include.NON_NULL)
public class MonitorType extends AbstractEntity {

    @NotNull(message = "Name required")
    private String name;
    @NotNull(message = "URL required")
    private String url;
    @NotNull(message = "HTTP method required")
    private Monitor.HttpMethod httpMethod;
    private String requestBody;
    @NotNull(message = "Cron expression required")
    private String cronExpression;
    @NotNull(message = "Type required")
    private Monitor.Type type;
    @NotNull(message = "Enable or disable checkbox")
    private boolean active;
    private String recipients;
    @NotNull(message = "Expected code required")
    private int expectedResponseCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Monitor.HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(Monitor.HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public int getExpectedResponseCode() {
        return expectedResponseCode;
    }

    public void setExpectedResponseCode(int expectedResponseCode) {
        this.expectedResponseCode = expectedResponseCode;
    }


    public Monitor.Type getType() {
        return type;
    }

    public void setType(Monitor.Type type) {
        this.type = type;
    }

    @AssertTrue(message = "Cron expression is invalid")
    public boolean isCronExpressionValid()
    {
        return CronExpression.isValidExpression(this.cronExpression);
    }
}
