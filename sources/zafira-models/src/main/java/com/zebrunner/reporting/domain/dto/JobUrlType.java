package com.zebrunner.reporting.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class JobUrlType implements Serializable {

    private static final long serialVersionUID = 7898966367734619663L;

    @NotNull
    private String jobUrlValue;

}
