package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JenkinsLauncherType implements Serializable {

    private static final long serialVersionUID = -5754742673797369219L;

    @NotEmpty(message = "{error.job.url.required}")
    private String jobUrl;

    @NotNull(message = "{error.job.parameters.required}")
    private String jobParameters;

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
