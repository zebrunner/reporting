package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.AbstractEntity;

import javax.validation.constraints.*;

/**
 * @author Kirill Bugrim
 *
 * @version 1.0
 */

@JsonInclude(Include.NON_NULL)
public class MonitorType extends AbstractEntity {

    private enum HttpMethod{GET, POST, PUT, DELETE}
    private enum Type{HTTP, PING}


    @NotNull(message = "Invalid name!")
    private String name;
    @NotNull(message = "Invalid url")
    private String url;
    @NotNull(message = "Invalid HTTP method")
    private HttpMethod httpMethod;
    private String requestBody;
    @NotNull(message = "Invalid cron expression")
    private String cronExpression;
    @NotNull(message = "Invalid type")
    private Type type;
    @NotNull(message = "Invalid type!Type must be true or false!")
    private boolean enableNotification;
    private String recipients;
    @NotNull(message = "Invalid type of response code!")
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

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
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

    public boolean isEnableNotification() {
        return enableNotification;
    }

    public void setEnableNotification(boolean enableNotification) {
        this.enableNotification = enableNotification;
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


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
