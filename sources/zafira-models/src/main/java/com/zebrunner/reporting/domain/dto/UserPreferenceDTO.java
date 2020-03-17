package com.zebrunner.reporting.domain.dto;

import com.zebrunner.reporting.domain.db.UserPreference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserPreferenceDTO {

    @NotNull
    private UserPreference.Name name;

    @NotNull
    private String value;

    @NotNull
    private Long userId;
}