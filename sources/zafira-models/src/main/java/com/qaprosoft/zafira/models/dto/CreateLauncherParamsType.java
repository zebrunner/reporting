package com.qaprosoft.zafira.models.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateLauncherParamsType implements Serializable {
    private static final long serialVersionUID = -5754742673797369219L;

    @NotNull(message = "{error.repo.required}")
    private String repo;

    @NotEmpty(message = "{error.job.url.required}")
    private String jobUrl;

    private String jobParameters;

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(String jobParameters) {
        this.jobParameters = jobParameters;
    }
}
