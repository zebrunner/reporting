package com.qaprosoft.zafira.models.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class JobUrlType implements Serializable {

    private static final long serialVersionUID = 7898966367734619663L;

    @NotNull
    private String jobUrlValue;

    public String getJobUrlValue() {
        return jobUrlValue;
    }

    public void setJobUrlValue(String jobUrlValue) {
        this.jobUrlValue = jobUrlValue;
    }
}
