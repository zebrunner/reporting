package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.AbstractEntity;

import javax.validation.constraints.NotNull;

/**
 * @author Kirill Bugrim
 *
 * @version 1.0
 */

@JsonInclude(Include.NON_NULL)
public class MonitorType extends AbstractEntity {

    private enum HttpMethod{GET, POST, PUT, DELETE}
    private enum Type{HTTP, PING}


    @NotNull
    private String name;
    @NotNull
    private String url;
    @NotNull
    private HttpMethod httpMethod;
    private String requestBody;
    @NotNull
    private String cronExpression;
    @NotNull
    private Type type;
    @NotNull
    private boolean enableNotification;

    private String emails;
    @NotNull
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

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
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
